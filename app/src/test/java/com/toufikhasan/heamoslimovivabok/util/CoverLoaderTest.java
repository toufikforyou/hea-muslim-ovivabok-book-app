package com.toufikhasan.heamoslimovivabok.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CoverLoaderTest {

    @Test
    public void keepsSmallImagesAtFullSize() {
        assertEquals(1, CoverLoader.sampleSize(200, 150, 180));
    }

    @Test
    public void downsamplesLargeImagesWithoutGoingBelowTarget() {
        // 960x1280 for a 180px thumbnail: 960/4 = 240 >= 180, 960/8 = 120 < 180.
        assertEquals(4, CoverLoader.sampleSize(960, 1280, 180));
    }

    @Test
    public void ignoresInvalidTarget() {
        assertEquals(1, CoverLoader.sampleSize(960, 1280, 0));
    }
}
