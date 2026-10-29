package com.toufikhasan.heamoslimovivabok;

public final class Bangla {

    private static final char[] DIGITS = {'০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯'};

    private Bangla() {
    }

    public static String number(int value) {
        String latin = String.valueOf(value);
        StringBuilder builder = new StringBuilder(latin.length());
        for (char c : latin.toCharArray()) {
            builder.append(c >= '0' && c <= '9' ? DIGITS[c - '0'] : c);
        }
        return builder.toString();
    }
}
