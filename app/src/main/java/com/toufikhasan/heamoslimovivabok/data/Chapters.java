package com.toufikhasan.heamoslimovivabok.data;

import android.content.Context;

import com.toufikhasan.heamoslimovivabok.R;

/**
 * The book's table of contents. Chapter ids are 1-based and match the asset file names.
 */
public final class Chapters {

    private Chapters() {
    }

    public static String[] titles(Context context) {
        return context.getResources().getStringArray(R.array.chapter_lists_array);
    }

    public static int count(Context context) {
        return titles(context).length;
    }

    public static String title(Context context, int chapterId) {
        String[] titles = titles(context);
        return isValid(chapterId, titles.length) ? titles[chapterId - 1] : "";
    }

    public static boolean isValid(int chapterId, int count) {
        return chapterId >= 1 && chapterId <= count;
    }

    public static String htmlAsset(int chapterId) {
        return "chapter/" + chapterId + ".html";
    }

    public static String coverAsset(int chapterId) {
        return "images/" + chapterId + ".jpg";
    }
}
