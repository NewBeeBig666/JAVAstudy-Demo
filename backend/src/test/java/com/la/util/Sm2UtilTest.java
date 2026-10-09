package com.la.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SM-2 间隔重复算法单元测试
 */
class Sm2UtilTest {

    @Test
    void quality5_firstRepetition_intervalBecomes1() {
        Sm2Util.Sm2State s = Sm2Util.apply(new BigDecimal("2.50"), 1, 0, 5);
        assertEquals(1, s.nextIntervalDays);
        assertEquals(1, s.repetitions);
        assertEquals(2.60, s.easeFactor.doubleValue(), 0.001);
    }

    @Test
    void quality5_secondRepetition_intervalBecomes6() {
        Sm2Util.Sm2State s = Sm2Util.apply(new BigDecimal("2.60"), 1, 1, 5);
        assertEquals(6, s.nextIntervalDays);
        assertEquals(2, s.repetitions);
    }

    @Test
    void quality5_thirdRepetition_intervalMultipliedByEf() {
        // interval 6 × EF 2.7 ≈ 16
        Sm2Util.Sm2State s = Sm2Util.apply(new BigDecimal("2.70"), 6, 2, 5);
        assertEquals(16, s.nextIntervalDays);
        assertEquals(3, s.repetitions);
    }

    @Test
    void quality3_remembered_efDecreases() {
        Sm2Util.Sm2State s = Sm2Util.apply(new BigDecimal("2.50"), 1, 1, 3);
        // EF' = 2.5 + (0.1 - 2*(0.08+2*0.02)) = 2.5 - 0.14 = 2.36
        assertEquals(2.36, s.easeFactor.doubleValue(), 0.001);
        assertEquals(6, s.nextIntervalDays);
    }

    @Test
    void quality0_forgotten_resets() {
        Sm2Util.Sm2State s = Sm2Util.apply(new BigDecimal("2.50"), 6, 3, 0);
        assertEquals(1, s.nextIntervalDays);
        assertEquals(0, s.repetitions);
    }

    @Test
    void efNeverBelow130() {
        // 极低 quality 连续调用也不低于 1.3
        Sm2Util.Sm2State s = Sm2Util.apply(new BigDecimal("1.30"), 1, 1, 0);
        assertTrue(s.easeFactor.doubleValue() >= 1.3);
    }
}
