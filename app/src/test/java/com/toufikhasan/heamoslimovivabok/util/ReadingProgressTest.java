package com.toufikhasan.heamoslimovivabok.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ReadingProgressTest {

    @Test
    public void percentAtTopMiddleAndBottom() {
        assertEquals(0, ReadingProgress.percent(0, 3000, 1000));
        assertEquals(50, ReadingProgress.percent(1000, 3000, 1000));
        assertEquals(100, ReadingProgress.percent(2000, 3000, 1000));
    }

    @Test
    public void percentIsClamped() {
        assertEquals(100, ReadingProgress.percent(2500, 3000, 1000));
        assertEquals(0, ReadingProgress.percent(-20, 3000, 1000));
    }

    @Test
    public void shortPageIsFullyRead() {
        assertEquals(100, ReadingProgress.percent(0, 800, 1000));
    }

    @Test
    public void unloadedPageHasNoProgress() {
        assertEquals(0, ReadingProgress.percent(0, 0, 1000));
    }

    @Test
    public void fractionMatchesScrollableRange() {
        assertEquals(0.25f, ReadingProgress.fraction(500, 3000, 1000), 0.0001f);
        assertEquals(0f, ReadingProgress.fraction(100, 800, 1000), 0.0001f);
        assertEquals(1f, ReadingProgress.fraction(5000, 3000, 1000), 0.0001f);
    }

    @Test
    public void finishedThreshold() {
        assertFalse(ReadingProgress.isFinished(94));
        assertTrue(ReadingProgress.isFinished(95));
        assertTrue(ReadingProgress.isFinished(100));
    }
}
