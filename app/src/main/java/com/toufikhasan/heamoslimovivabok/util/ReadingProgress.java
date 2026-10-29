package com.toufikhasan.heamoslimovivabok.util;

/**
 * Pure helpers for turning scroll positions into reading progress.
 */
public final class ReadingProgress {

    /** A chapter counts as read once the reader gets this far through it. */
    public static final int READ_THRESHOLD_PERCENT = 95;

    private ReadingProgress() {
    }

    /**
     * @param scrollY       current vertical scroll offset, in pixels
     * @param contentHeight total content height, in pixels
     * @param viewHeight    visible height, in pixels
     * @return progress through the content, from 0 to 100
     */
    public static int percent(int scrollY, int contentHeight, int viewHeight) {
        int range = contentHeight - viewHeight;
        if (contentHeight <= 0) {
            return 0;
        }
        if (range <= 0) {
            return 100;
        }
        return clamp(Math.round(scrollY * 100f / range), 0, 100);
    }

    /** Fraction (0..1) of the scrollable range that has been scrolled past. */
    public static float fraction(int scrollY, int contentHeight, int viewHeight) {
        int range = contentHeight - viewHeight;
        if (range <= 0) {
            return 0f;
        }
        return Math.max(0f, Math.min(1f, scrollY / (float) range));
    }

    public static boolean isFinished(int percent) {
        return percent >= READ_THRESHOLD_PERCENT;
    }

    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
