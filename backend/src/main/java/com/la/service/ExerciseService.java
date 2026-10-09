package com.la.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.la.dto.response.AttemptResponse;
import com.la.dto.response.ExerciseDto;
import com.la.entity.Exercise;
import com.la.entity.ExerciseRecord;
import com.la.entity.KnowledgePoint;
import com.la.entity.WrongBook;
import com.la.exception.BizException;
import com.la.mapper.ExerciseMapper;
import com.la.mapper.ExerciseRecordMapper;
import com.la.mapper.KnowledgePointMapper;
import com.la.mapper.WrongBookMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExerciseService {

    private final ExerciseMapper exerciseMapper;
    private final ExerciseRecordMapper recordMapper;
    private final KnowledgePointMapper kpMapper;
    private final WrongBookMapper wrongBookMapper;
    private final MasteryService masteryService;
    private final ReviewService reviewService;
    private final ObjectMapper objectMapper;

    /**
     * 知识点下的题目（不泄露 answer / analysis / hints）
     */
    public List<ExerciseDto> listByKp(String kpCode) {
        KnowledgePoint kp = kpMapper.selectOne(new LambdaQueryWrapper<KnowledgePoint>()
                .eq(KnowledgePoint::getKpCode, kpCode));
        if (kp == null) {
            throw new BizException("知识点不存在: " + kpCode);
        }
        List<Exercise> list = exerciseMapper.selectList(new LambdaQueryWrapper<Exercise>()
                .eq(Exercise::getKpId, kp.getId()));
        return list.stream().map(this::toDto).toList();
    }

    public ExerciseDto detail(Long exerciseId) {
        Exercise ex = exerciseMapper.selectById(exerciseId);
        if (ex == null) {
            throw new BizException("题目不存在");
        }
        return toDto(ex);
    }

    /**
     * 请求一级提示（level 1-3）
     */
    public Map<String, Object> hint(Long exerciseId, int level) {
        if (level < 1 || level > 3) {
            throw new BizException("提示级别为 1-3");
        }
        Exercise ex = exerciseMapper.selectById(exerciseId);
        if (ex == null) {
            throw new BizException("题目不存在");
        }
        List<String> hints = parseJsonArray(ex.getHints());
        if (hints.size() < level) {
            throw new BizException("该题没有更多提示了");
        }
        return Map.of("hint", hints.get(level - 1), "level", level);
    }

    /**
     * 提交答案（一个事务内完成）：判分 → 掌握度回写 → 练习记录 → 错题本 → SM-2 入队
     */
    @Transactional
    public AttemptResponse attempt(Long userId, Long exerciseId, Integer selected, Integer hintUsed) {
        Exercise ex = exerciseMapper.selectById(exerciseId);
        if (ex == null) {
            throw new BizException("题目不存在");
        }
        if (selected == null || selected < 0) {
            throw new BizException("请选择一个答案");
        }
        boolean correct = ex.getAnswer() != null && ex.getAnswer().equals(selected);
        int applied = correct ? ex.getMasteryDelta() : -3;

        // 1) 练习记录
        ExerciseRecord record = new ExerciseRecord();
        record.setUserId(userId);
        record.setExerciseId(exerciseId);
        record.setKpId(ex.getKpId());
        record.setSelected(selected);
        record.setIsCorrect(correct);
        record.setDelta(applied);
        record.setHintUsed(hintUsed != null ? hintUsed : 0);
        record.setCreatedAt(LocalDateTime.now());
        recordMapper.insert(record);

        // 2) 掌握度回写
        int newMastery = masteryService.adjust(userId, ex.getKpId(), applied);

        // 3) 错题本 + SM-2
        boolean wrongBookAdded = false;
        LocalDate reviewNext = null;
        if (!correct) {
            WrongBook wb = wrongBookMapper.selectOne(new LambdaQueryWrapper<WrongBook>()
                    .eq(WrongBook::getUserId, userId).eq(WrongBook::getExerciseId, exerciseId));
            if (wb == null) {
                wb = new WrongBook();
                wb.setUserId(userId);
                wb.setExerciseId(exerciseId);
                wb.setKpId(ex.getKpId());
                wb.setWrongCount(1);
                wb.setLastWrongAt(LocalDateTime.now());
                wrongBookMapper.insert(wb);
                wrongBookAdded = true;
            } else {
                wb.setWrongCount(wb.getWrongCount() + 1);
                wb.setLastWrongAt(LocalDateTime.now());
                wrongBookMapper.updateById(wb);
            }
            reviewNext = reviewService.enqueueForWrong(userId, ex);
        }

        // 4) 学习时长（一次练习记 3 分钟）
        masteryService.addMinutes(userId, 3);

        return new AttemptResponse(correct, ex.getAnswer(), ex.getAnalysis(), applied,
                newMastery, wrongBookAdded, reviewNext);
    }

    private ExerciseDto toDto(Exercise ex) {
        KnowledgePoint kp = kpMapper.selectById(ex.getKpId());
        return new ExerciseDto(ex.getId(), kp != null ? kp.getKpCode() : null,
                ex.getType(), ex.getDifficulty(), ex.getStem(), ex.getCode(),
                parseJsonArray(ex.getOptions()), ex.getMasteryDelta());
    }

    /**
     * 练习题卡片数据（不含 answer/analysis/hints），嵌入 exercise 卡片消息
     */
    public Map<String, Object> exerciseMap(Exercise ex) {
        KnowledgePoint kp = kpMapper.selectById(ex.getKpId());
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", ex.getId());
        m.put("kp", kp != null ? kp.getKpCode() : null);
        m.put("type", ex.getType());
        m.put("difficulty", ex.getDifficulty());
        m.put("stem", ex.getStem());
        m.put("code", ex.getCode());
        m.put("options", parseJsonArray(ex.getOptions()));
        m.put("masteryDelta", ex.getMasteryDelta());
        return m;
    }

    /**
     * 错题本（对齐 MOCK.wrongBook 形状）
     */
    public List<Map<String, Object>> wrongBook(Long userId) {
        List<WrongBook> list = wrongBookMapper.selectList(new LambdaQueryWrapper<WrongBook>()
                .eq(WrongBook::getUserId, userId).orderByDesc(WrongBook::getLastWrongAt));
        List<Map<String, Object>> result = new ArrayList<>();
        for (WrongBook wb : list) {
            Exercise ex = exerciseMapper.selectById(wb.getExerciseId());
            KnowledgePoint kp = kpMapper.selectById(wb.getKpId());
            Map<String, Object> dto = new LinkedHashMap<>();
            dto.put("id", String.valueOf(wb.getId()));
            dto.put("exerciseId", wb.getExerciseId());
            dto.put("kp", kp != null ? kp.getKpCode() : null);
            dto.put("title", ex != null ? shorten(ex.getStem()) : "");
            dto.put("wrongCount", wb.getWrongCount());
            dto.put("lastAt", wb.getLastWrongAt() != null
                    ? wb.getLastWrongAt().format(DateTimeFormatter.ofPattern("MM-dd")) : null);
            result.add(dto);
        }
        return result;
    }

    /**
     * 练习记录
     */
    public List<Map<String, Object>> records(Long userId, String kpCode) {
        Long kpId = null;
        if (kpCode != null) {
            KnowledgePoint kp = kpMapper.selectOne(new LambdaQueryWrapper<KnowledgePoint>()
                    .eq(KnowledgePoint::getKpCode, kpCode));
            kpId = kp != null ? kp.getId() : -1L;
        }
        LambdaQueryWrapper<ExerciseRecord> qw = new LambdaQueryWrapper<ExerciseRecord>()
                .eq(ExerciseRecord::getUserId, userId)
                .orderByDesc(ExerciseRecord::getCreatedAt);
        if (kpId != null) {
            qw.eq(ExerciseRecord::getKpId, kpId);
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for (ExerciseRecord r : recordMapper.selectList(qw)) {
            Exercise ex = exerciseMapper.selectById(r.getExerciseId());
            KnowledgePoint kp = kpMapper.selectById(r.getKpId());
            Map<String, Object> dto = new LinkedHashMap<>();
            dto.put("id", String.valueOf(r.getId()));
            dto.put("exerciseId", r.getExerciseId());
            dto.put("kp", kp != null ? kp.getKpCode() : null);
            dto.put("title", ex != null ? shorten(ex.getStem()) : "");
            dto.put("difficulty", ex != null ? ex.getDifficulty() : null);
            dto.put("correct", Boolean.TRUE.equals(r.getIsCorrect()));
            dto.put("delta", r.getDelta());
            dto.put("hintUsed", r.getHintUsed());
            dto.put("time", r.getCreatedAt() != null
                    ? r.getCreatedAt().format(DateTimeFormatter.ofPattern("MM-dd HH:mm")) : null);
            result.add(dto);
        }
        return result;
    }

    private String shorten(String stem) {
        String one = stem.replaceAll("\\s+", " ").trim();
        return one.length() > 40 ? one.substring(0, 40) + "…" : one;
    }

    private List<String> parseJsonArray(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(json, objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, String.class));
        } catch (Exception e) {
            return List.of();
        }
    }
}
