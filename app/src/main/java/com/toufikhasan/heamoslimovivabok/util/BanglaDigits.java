package com.toufikhasan.heamoslimovivabok.util;

/**
 * Converts Western digits to Bangla digits so numbers read naturally next to Bangla text.
 */
public final class BanglaDigits {

    private static final char[] DIGITS = {'০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯'};

    private BanglaDigits() {
    }

    public static String of(int number) {
        return of(String.valueOf(number));
    }

    public static String of(String text) {
        if (text == null) {
            return "";
        }
        StringBuilder out = new StringBuilder(text.length());
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            out.append(c >= '0' && c <= '9' ? DIGITS[c - '0'] : c);
        }
        return out.toString();
    }
}
