package com.toufikhasan.heamoslimovivabok;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;

/**
 * Small wrapper around SharedPreferences for everything the reader remembers:
 * app theme, text size, reading theme and where the user stopped in each chapter.
 */
public class ReadingPrefs {

    public static final int THEME_AUTO = 0;
    public static final int THEME_PAPER = 1;
    public static final int THEME_SEPIA = 2;
    public static final int THEME_NIGHT = 3;

    public static final float DEFAULT_TEXT_SIZE = 19f;

    private final SharedPreferences prefs;

    public ReadingPrefs(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences("reading", Context.MODE_PRIVATE);
    }

    public int getNightMode() {
        return prefs.getInt("night_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }

    public void setNightMode(int mode) {
        prefs.edit().putInt("night_mode", mode).apply();
    }

    public float getTextSize() {
        return prefs.getFloat("text_size", DEFAULT_TEXT_SIZE);
    }

    public void setTextSize(float size) {
        prefs.edit().putFloat("text_size", size).apply();
    }

    public int getReaderTheme() {
        return prefs.getInt("reader_theme", THEME_AUTO);
    }

    public void setReaderTheme(int theme) {
        prefs.edit().putInt("reader_theme", theme).apply();
    }

    public boolean getKeepScreenOn() {
        return prefs.getBoolean("keep_screen_on", true);
    }

    public void setKeepScreenOn(boolean keepOn) {
        prefs.edit().putBoolean("keep_screen_on", keepOn).apply();
    }

    @Nullable
    public String getLastFile() {
        return prefs.getString("last_file", null);
    }

    public int getPosition(String file) {
        return prefs.getInt("pos_" + file, 0);
    }

    public int getOffset(String file) {
        return prefs.getInt("offset_" + file, 0);
    }

    /** Reading progress of a chapter in percent (0-100). */
    public int getProgress(String file) {
        return prefs.getInt("progress_" + file, 0);
    }

    public void saveReading(String file, int position, int offset, int progress, boolean isChapter) {
        SharedPreferences.Editor editor = prefs.edit()
                .putInt("pos_" + file, position)
                .putInt("offset_" + file, offset)
                .putInt("progress_" + file, progress);
        if (isChapter) {
            editor.putString("last_file", file);
        }
        editor.apply();
    }

    /** Returns true only the first time it is called on a given day. */
    public boolean shouldGreetToday(long dayNumber) {
        if (prefs.getLong("greeted_day", -1) == dayNumber) {
            return false;
        }
        prefs.edit().putLong("greeted_day", dayNumber).apply();
        return true;
    }
}
