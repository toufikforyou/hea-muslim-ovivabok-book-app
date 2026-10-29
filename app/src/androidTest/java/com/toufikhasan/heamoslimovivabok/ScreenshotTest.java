package com.toufikhasan.heamoslimovivabok;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Smoke test that walks through the main screens and saves screenshots to
 * files/screens (pulled by CI with run-as) so the UI can be reviewed.
 */
@RunWith(AndroidJUnit4.class)
public class ScreenshotTest {

    private final Context context = ApplicationProvider.getApplicationContext();

    @Test
    public void walkThroughScreens() throws Exception {
        context.getSharedPreferences("reading", Context.MODE_PRIVATE).edit().clear().commit();
        String title = Book.CHAPTERS.get(Book.indexOf("4")).getTitle();

        try (ActivityScenario<MainActivity> ignored = ActivityScenario.launch(MainActivity.class)) {
            capture("01_home_greeting");
            pressBack();
            capture("02_home");
        }

        try (ActivityScenario<ChapterViewActivity> reader = ActivityScenario.launch(ChapterViewActivity.intent(context, "4", title))) {
            capture("03_reader");
            onView(withId(R.id.action_text_settings)).perform(click());
            capture("04_reader_settings");
            pressBack();
            onView(withId(R.id.recyclerView)).perform(androidx.test.espresso.action.ViewActions.swipeUp());
            onView(withId(R.id.recyclerView)).perform(androidx.test.espresso.action.ViewActions.swipeUp());
            capture("05_reader_scrolled");
        }

        try (ActivityScenario<MainActivity> ignored = ActivityScenario.launch(MainActivity.class)) {
            capture("06_home_continue");
        }

        new ReadingPrefs(context).setReaderTheme(ReadingPrefs.THEME_SEPIA);
        try (ActivityScenario<ChapterViewActivity> ignored = ActivityScenario.launch(ChapterViewActivity.intent(context, "4", title))) {
            capture("07_reader_sepia");
        }

        InstrumentationRegistry.getInstrumentation().runOnMainSync(() ->
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES));
        new ReadingPrefs(context).setReaderTheme(ReadingPrefs.THEME_AUTO);
        try (ActivityScenario<MainActivity> ignored = ActivityScenario.launch(MainActivity.class)) {
            capture("08_home_dark");
        }
        try (ActivityScenario<ChapterViewActivity> ignored = ActivityScenario.launch(ChapterViewActivity.intent(context, "4", title))) {
            capture("09_reader_dark");
        }
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() ->
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM));
    }

    private void capture(String name) throws InterruptedException, IOException {
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        Thread.sleep(1500);
        Bitmap bitmap = InstrumentationRegistry.getInstrumentation().getUiAutomation().takeScreenshot();
        File dir = new File(context.getFilesDir(), "screens");
        if (!dir.isDirectory() && !dir.mkdirs()) {
            throw new IOException("Cannot create " + dir);
        }
        try (FileOutputStream out = new FileOutputStream(new File(dir, name + ".png"))) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        }
    }
}
