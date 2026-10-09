package com.la.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.la.entity.Message;
import com.la.entity.SessionEntity;
import com.la.entity.SessionStep;
import com.la.mapper.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 清空会话功能单元测试：验证删除范围仅限 session / session_step / message，
 * 且无会话时不执行任何删除。
 */
@ExtendWith(MockitoExtension.class)
class SessionServiceClearTest {

    @Mock private SessionMapper sessionMapper;
    @Mock private SessionStepMapper stepMapper;
    @Mock private MessageMapper messageMapper;
    @Mock private KnowledgePointMapper kpMapper;
    @Mock private KnowledgeService knowledgeService;
    @Mock private PathPlanService pathPlanService;
    @Mock private ExerciseMapper exerciseMapper;
    @Mock private ExerciseService exerciseService;
    @Mock private ObjectMapper objectMapper;

    @InjectMocks private SessionService service;

    private SessionEntity session(long id) {
        SessionEntity s = new SessionEntity();
        s.setId(id);
        s.setUserId(15L);
        return s;
    }

    @Test
    void clearAll_deletesSessionsStepsAndMessages() {
        when(sessionMapper.selectList(any())).thenReturn(List.of(session(1L), session(2L)));

        int n = service.clearAll(15L);

        assertEquals(2, n);
        verify(messageMapper, times(1)).delete(any());
        verify(stepMapper, times(1)).delete(any());
        verify(sessionMapper, times(1)).delete(any());
    }

    @Test
    void clearAll_noSessions_isNoop() {
        when(sessionMapper.selectList(any())).thenReturn(List.of());

        int n = service.clearAll(15L);

        assertEquals(0, n);
        verify(messageMapper, never()).delete(any());
        verify(stepMapper, never()).delete(any());
        verify(sessionMapper, never()).delete(any());
    }
}
