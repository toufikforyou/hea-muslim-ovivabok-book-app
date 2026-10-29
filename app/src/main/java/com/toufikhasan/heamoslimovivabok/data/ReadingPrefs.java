package com.toufikhasan.heamoslimovivabok.data;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

import com.toufikhasan.heamoslimovivabok.util.ReadingProgress;

import java.util.HashSet;
import java.util.Set;

/**
 * Remembers the reader's place and preferences between sessions.
 */
public final class ReadingPrefs {

    public static final int PAGE_LIGHT = 0;
    public static final int PAGE_SEPIA = 1;
    public static final int PAGE_DARK = 2;

    public static final int MIN_TEXT_ZOOM = 80;
    public static final int MAX_TEXT_ZOOM = 180;
    public static final int TEXT_ZOOM_STEP = 10;
    private static final int DEFAULT_TEXT_ZOOM = 100;

    private static final String FILE = "reading_prefs";
    private static final String KEY_LAST_CHAPTER = "last_chapter";
    private static final String KEY_READ_CHAPTERS = "read_chapters";
    private static final String KEY_TEXT_ZOOM = "text_zoom";
    private static final String KEY_PAGE_THEME = "page_theme";
    private static final String KEY_NIGHT_MODE = "night_mode";
    private static final String KEY_SCROLL_PREFIX = "scroll_";

    private final SharedPreferences prefs;

    public ReadingPrefs(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(FILE, Context.MODE_PRIVATE);
    }

    /** @return the last opened chapter id, or 0 if the reader has not started yet. */
    public int getLastChapter() {
        return prefs.getInt(KEY_LAST_CHAPTER, 0);
    }

    public void setLastChapter(int chapterId) {
        prefs.edit().putInt(KEY_LAST_CHAPTER, chapterId).apply();
    }

    public boolean isRead(int chapterId) {
        return readChapters().contains(String.valueOf(chapterId));
    }

    public int readCount() {
        return readChapters().size();
    }

    public void markRead(int chapterId) {
        Set<String> read = readChapters();
        if (read.add(String.valueOf(chapterId))) {
            prefs.edit().putStringSet(KEY_READ_CHAPTERS, read).apply();
        }
    }

    private Set<String> readChapters() {
        // The returned set must not be modified, so always work on a copy.
        return new HashSet<>(prefs.getStringSet(KEY_READ_CHAPTERS, new HashSet<>()));
    }

    public int getTextZoom() {
        return prefs.getInt(KEY_TEXT_ZOOM, DEFAULT_TEXT_ZOOM);
    }

    public int setTextZoom(int zoom) {
        int clamped = ReadingProgress.clamp(zoom, MIN_TEXT_ZOOM, MAX_TEXT_ZOOM);
        prefs.edit().putInt(KEY_TEXT_ZOOM, clamped).apply();
        return clamped;
    }

    /** @param nightUi whether the app is currently showing its dark theme */
    public int getPageTheme(boolean nightUi) {
        return prefs.getInt(KEY_PAGE_THEME, nightUi ? PAGE_DARK : PAGE_LIGHT);
    }

    public void setPageTheme(int theme) {
        prefs.edit().putInt(KEY_PAGE_THEME, theme).apply();
    }

    public float getScrollFraction(int chapterId) {
        return prefs.getFloat(KEY_SCROLL_PREFIX + chapterId, 0f);
    }

    public void setScrollFraction(int chapterId, float fraction) {
        prefs.edit().putFloat(KEY_SCROLL_PREFIX + chapterId, fraction).apply();
    }

    public int getNightMode() {
        return prefs.getInt(KEY_NIGHT_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }

    public void setNightMode(int mode) {
        prefs.edit().putInt(KEY_NIGHT_MODE, mode).apply();
    }
}
