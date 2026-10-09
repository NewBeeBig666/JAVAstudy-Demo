package com.la.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * SM-2 间隔重复算法（SuperMemo-2）
 */
public final class Sm2Util {

    private Sm2Util() {
    }

    public static class Sm2State {
        public final BigDecimal easeFactor;
        public final int intervalDays;
        public final int repetitions;
        public final int nextIntervalDays;

        public Sm2State(BigDecimal easeFactor, int intervalDays, int repetitions, int nextIntervalDays) {
            this.easeFactor = easeFactor;
            this.intervalDays = intervalDays;
            this.repetitions = repetitions;
            this.nextIntervalDays = nextIntervalDays;
        }
    }

    /**
     * @param quality 回忆质量 0-5（5=轻松记起，3=勉强想起，<3=遗忘）
     */
    public static Sm2State apply(BigDecimal easeFactor, int intervalDays, int repetitions, int quality) {
        BigDecimal ef = easeFactor;
        int rep = repetitions;
        int interval;

        if (quality >= 3) {
            // 回忆成功：按次数推进间隔
            if (rep == 0) {
                interval = 1;
            } else if (rep == 1) {
                interval = 6;
            } else {
                interval = Math.max(1, BigDecimal.valueOf(intervalDays)
                        .multiply(ef).setScale(0, RoundingMode.HALF_UP).intValue());
            }
            rep += 1;
        } else {
            // 遗忘：重置
            interval = 1;
            rep = 0;
        }

        // EF 更新公式，下限 1.3
        double q = quality;
        double newEf = ef.doubleValue() + (0.1 - (5 - q) * (0.08 + (5 - q) * 0.02));
        ef = BigDecimal.valueOf(Math.max(1.3, newEf)).setScale(2, RoundingMode.HALF_UP);

        return new Sm2State(ef, interval, rep, interval);
    }
}
