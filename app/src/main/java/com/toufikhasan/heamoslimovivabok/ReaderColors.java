package com.toufikhasan.heamoslimovivabok;

import android.content.Context;
import android.content.res.Configuration;

import androidx.core.content.ContextCompat;

/** Resolves the colors of the chosen reading theme (paper, sepia or night). */
public final class ReaderColors {

    public final int background;
    public final int text;
    public final int secondaryText;
    public final boolean isDark;

    private ReaderColors(Context context, int bg, int fg, int secondary, boolean isDark) {
        this.background = ContextCompat.getColor(context, bg);
        this.text = ContextCompat.getColor(context, fg);
        this.secondaryText = ContextCompat.getColor(context, secondary);
        this.isDark = isDark;
    }

    public static ReaderColors resolve(Context context, int theme) {
        if (theme == ReadingPrefs.THEME_AUTO) {
            int uiMode = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            theme = uiMode == Configuration.UI_MODE_NIGHT_YES ? ReadingPrefs.THEME_NIGHT : ReadingPrefs.THEME_PAPER;
        }
        switch (theme) {
            case ReadingPrefs.THEME_SEPIA:
                return new ReaderColors(context, R.color.reader_sepia_bg, R.color.reader_sepia_text, R.color.reader_sepia_secondary, false);
            case ReadingPrefs.THEME_NIGHT:
                return new ReaderColors(context, R.color.reader_night_bg, R.color.reader_night_text, R.color.reader_night_secondary, true);
            default:
                return new ReaderColors(context, R.color.reader_paper_bg, R.color.reader_paper_text, R.color.reader_paper_secondary, false);
        }
    }
}
