package com.la.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.la.dto.response.ReviewItemDto;
import com.la.entity.Exercise;
import com.la.entity.ExerciseRecord;
import com.la.entity.KnowledgePoint;
import com.la.entity.Mastery;
import com.la.entity.ReviewSchedule;
import com.la.entity.User;
import com.la.exception.BizException;
import com.la.mapper.ExerciseMapper;
import com.la.mapper.ExerciseRecordMapper;
import com.la.mapper.KnowledgePointMapper;
import com.la.mapper.MasteryMapper;
import com.la.mapper.ReviewScheduleMapper;
import com.la.mapper.UserMapper;
import com.la.util.Sm2Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewScheduleMapper reviewMapper;
    private final ExerciseMapper exerciseMapper;
    private final KnowledgePointMapper kpMapper;
    private final MasteryMapper masteryMapper;
    private final UserMapper userMapper;
    private final ExerciseRecordMapper exerciseRecordMapper;

    /**
     * 今日复习队列：next_review_date <= 今天 且 PENDING
     */
    public List<ReviewItemDto> today(Long userId) {
        List<ReviewSchedule> items = reviewMapper.selectList(new LambdaQueryWrapper<ReviewSchedule>()
                .eq(ReviewSchedule::getUserId, userId)
                .eq(ReviewSchedule::getStatus, "PENDING")
                .le(ReviewSchedule::getNextReviewDate, LocalDate.now())
                .orderByAsc(ReviewSchedule::getNextReviewDate));
        return toDto(items);
    }

    /**
     * 全部队列（含未来到期的），供报告页展示
     */
    public List<ReviewItemDto> all(Long userId) {
        List<ReviewSchedule> items = reviewMapper.selectList(new LambdaQueryWrapper<ReviewSchedule>()
                .eq(ReviewSchedule::getUserId, userId)
                .eq(ReviewSchedule::getStatus, "PENDING")
                .orderByAsc(ReviewSchedule::getNextReviewDate));
        return toDto(items);
    }

    /**
     * 复习完成：按 SM-2 更新并顺延下次复习时间
     */
    @Transactional
    public ReviewItemDto complete(Long userId, Long reviewId, int quality) {
        if (quality < 0 || quality > 5) {
            throw new BizException("quality 取值范围 0-5");
        }
        ReviewSchedule rs = reviewMapper.selectById(reviewId);
        if (rs == null || !rs.getUserId().equals(userId)) {
            throw new BizException("复习项不存在");
        }
        Sm2Util.Sm2State next = Sm2Util.apply(rs.getEaseFactor(), rs.getIntervalDays(),
                rs.getRepetitions(), quality);
        rs.setEaseFactor(next.easeFactor);
        rs.setIntervalDays(next.nextIntervalDays);
        rs.setRepetitions(next.repetitions);
        rs.setLastReviewDate(LocalDate.now());
        rs.setNextReviewDate(LocalDate.now().plusDays(next.nextIntervalDays));
        reviewMapper.updateById(rs);
        return toDto(List.of(rs)).get(0);
    }

    /**
     * 答错练习时的入队/重置：UPSERT review_schedule
     *
     * @return 下次复习日期
     */
    @Transactional
    public LocalDate enqueueForWrong(Long userId, Exercise ex) {
        ReviewSchedule rs = reviewMapper.selectOne(new LambdaQueryWrapper<ReviewSchedule>()
                .eq(ReviewSchedule::getUserId, userId).eq(ReviewSchedule::getExerciseId, ex.getId()));
        LocalDate next = LocalDate.now().plusDays(1);
        if (rs == null) {
            rs = new ReviewSchedule();
            rs.setUserId(userId);
            rs.setExerciseId(ex.getId());
            rs.setKpId(ex.getKpId());
            rs.setTitle(shorten(ex.getStem()));
            rs.setEaseFactor(new java.math.BigDecimal("2.50"));
            rs.setIntervalDays(1);
            rs.setRepetitions(0);
            rs.setNextReviewDate(next);
            rs.setSource("WRONG");
            rs.setStatus("PENDING");
            reviewMapper.insert(rs);
        } else {
            rs.setRepetitions(0);
            rs.setIntervalDays(1);
            rs.setNextReviewDate(next);
            rs.setStatus("PENDING");
            reviewMapper.updateById(rs);
        }
        return next;
    }

    /**
     * 每日 02:00：为掌握度<40 且无待复习项的知识点补充复习（source=WEAK）
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void supplementWeakKps() {
        List<User> students = userMapper.selectList(new LambdaQueryWrapper<User>()
                .eq(User::getRole, "STUDENT"));
        for (User stu : students) {
            List<Mastery> weak = masteryMapper.selectList(new LambdaQueryWrapper<Mastery>()
                    .eq(Mastery::getUserId, stu.getId()).lt(Mastery::getMasteryValue, 40));
            Set<Long> coveredKps = new HashSet<>(reviewMapper.selectList(new LambdaQueryWrapper<ReviewSchedule>()
                            .eq(ReviewSchedule::getUserId, stu.getId())
                            .eq(ReviewSchedule::getStatus, "PENDING"))
                    .stream().map(ReviewSchedule::getKpId).toList());
            for (Mastery m : weak) {
                if (coveredKps.contains(m.getKpId())) {
                    continue;
                }
                Exercise ex = exerciseMapper.selectOne(new LambdaQueryWrapper<Exercise>()
                        .eq(Exercise::getKpId, m.getKpId()).last("LIMIT 1"));
                if (ex == null) {
                    continue;
                }
                ReviewSchedule rs = new ReviewSchedule();
                rs.setUserId(stu.getId());
                rs.setExerciseId(ex.getId());
                rs.setKpId(m.getKpId());
                rs.setTitle(shorten(ex.getStem()));
                rs.setEaseFactor(new java.math.BigDecimal("2.50"));
                rs.setIntervalDays(1);
                rs.setRepetitions(0);
                rs.setNextReviewDate(LocalDate.now().plusDays(1));
                rs.setSource("WEAK");
                rs.setStatus("PENDING");
                reviewMapper.insert(rs);
                coveredKps.add(m.getKpId());
            }
        }
        log.info("薄弱知识点复习补充完成");
    }

    private List<ReviewItemDto> toDto(List<ReviewSchedule> items) {
        List<ReviewItemDto> result = new ArrayList<>();
        for (ReviewSchedule rs : items) {
            KnowledgePoint kp = kpMapper.selectById(rs.getKpId());
            String due;
            LocalDate next = rs.getNextReviewDate();
            if (next.isBefore(LocalDate.now()) || next.isEqual(LocalDate.now())) {
                due = "今天";
            } else if (next.isEqual(LocalDate.now().plusDays(1))) {
                due = "明天";
            } else {
                due = next.format(DateTimeFormatter.ofPattern("MM-dd"));
            }
            String reason = "WEAK".equals(rs.getSource())
                    ? "掌握度低于 40"
                    : (rs.getRepetitions() > 0 ? "SM-2 第 " + (rs.getRepetitions() + 1) + " 次复习" : "昨日练习错误");
            result.add(new ReviewItemDto(rs.getId(), kp != null ? kp.getKpCode() : null,
                    rs.getTitle(), due, reason, rs.getExerciseId()));
        }
        return result;
    }

    private String shorten(String stem) {
        String one = stem.replaceAll("\\s+", " ").trim();
        return one.length() > 40 ? one.substring(0, 40) + "…" : one;
    }
}
