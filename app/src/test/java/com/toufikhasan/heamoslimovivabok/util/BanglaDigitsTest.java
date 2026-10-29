package com.toufikhasan.heamoslimovivabok.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BanglaDigitsTest {

    @Test
    public void convertsEveryDigit() {
        assertEquals("০১২৩৪৫৬৭৮৯", BanglaDigits.of("0123456789"));
    }

    @Test
    public void convertsNumbers() {
        assertEquals("১০", BanglaDigits.of(10));
        assertEquals("০", BanglaDigits.of(0));
    }

    @Test
    public void keepsOtherCharacters() {
        assertEquals("২.০.০", BanglaDigits.of("2.0.0"));
        assertEquals("১০০%", BanglaDigits.of("100%"));
        assertEquals("অধ্যায় ৩", BanglaDigits.of("অধ্যায় 3"));
    }

    @Test
    public void handlesNull() {
        assertEquals("", BanglaDigits.of(null));
    }
}
