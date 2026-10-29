package com.toufikhasan.heamoslimovivabok;

import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** The table of contents of the book. Chapter n is stored in assets/chapter/n.html */
public final class Book {

    public static final List<ChapterModel> CHAPTERS = Collections.unmodifiableList(Arrays.asList(
            new ChapterModel("লেখকের কথা", "1"),
            new ChapterModel("নিজেদের সংশোধন করুন", "2"),
            new ChapterModel("অভিভাবকদের ব্যয়", "3"),
            new ChapterModel("সন্তানদের চরিত্র গঠনে অভিভাবকদের ভূমিকা", "4"),
            new ChapterModel("অভিভাবকদের অসতর্কতা", "5"),
            new ChapterModel("সন্তানদের প্রতি অভিশাপ", "6"),
            new ChapterModel("অভিভাবকদের অসিয়ত", "7"),
            new ChapterModel("সন্তানদের শিক্ষা ও বিবাহ্", "8"),
            new ChapterModel("সন্তানদের নিয়ে স্বপ্ন", "9"),
            new ChapterModel("এক নজরে", "10")
    ));

    /** Writer introduction, stored in the app_writer_info string instead of an asset. */
    public static final ChapterModel ABOUT_WRITER = new ChapterModel("লেখক পরিচিতি", "writer");

    private Book() {
    }

    /** Index of the chapter in {@link #CHAPTERS}, or -1 for pages outside the chapter list. */
    public static int indexOf(String file) {
        for (int i = 0; i < CHAPTERS.size(); i++) {
            if (CHAPTERS.get(i).getFileName().equals(file)) {
                return i;
            }
        }
        return -1;
    }

    @Nullable
    public static ChapterModel find(@Nullable String file) {
        int index = file == null ? -1 : indexOf(file);
        return index >= 0 ? CHAPTERS.get(index) : null;
    }
}
