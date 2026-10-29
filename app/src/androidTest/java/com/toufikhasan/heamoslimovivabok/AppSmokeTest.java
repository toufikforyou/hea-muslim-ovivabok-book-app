package com.toufikhasan.heamoslimovivabok;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Walks through the app the way a reader would: home, open a chapter, move between chapters, other tabs.
 */
@RunWith(AndroidJUnit4.class)
public class AppSmokeTest {

    @Rule
    public final ActivityScenarioRule<MainActivity> rule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void startFresh() {
        Context context = ApplicationProvider.getApplicationContext();
        context.getSharedPreferences("reading_prefs", Context.MODE_PRIVATE).edit().clear().commit();
        rule.getScenario().recreate();
    }

    @Test
    public void homeShowsGreetingProgressAndChapters() {
        onView(withText(R.string.home_greeting)).check(matches(isDisplayed()));
        onView(withId(R.id.continue_button)).check(matches(withText(R.string.home_start_reading)));
        onView(withId(R.id.chapters_list)).check(matches(isDisplayed()));
    }

    @Test
    public void readerOpensAndMovesBetweenChapters() {
        onView(withId(R.id.continue_button)).perform(click());

        onView(withId(R.id.webViewChapter)).check(matches(isDisplayed()));
        onView(withId(R.id.reader_previous)).check(matches(not(isEnabled())));

        onView(withId(R.id.reader_next)).perform(click());
        onView(withId(R.id.reader_previous)).check(matches(isEnabled()));

        onView(withId(R.id.action_reader_settings)).perform(click());
        onView(withText(R.string.reader_text_size)).check(matches(isDisplayed()));
        onView(withId(R.id.text_larger)).perform(click());
        onView(withId(R.id.page_sepia)).perform(click());
        pressBack();

        pressBack();
        onView(withId(R.id.continue_button)).check(matches(withText(R.string.home_continue_reading)));
    }

    @Test
    public void bottomNavigationReachesEveryTab() {
        onView(withId(R.id.nav_writer)).perform(click());
        onView(withId(R.id.writer_name)).check(matches(isDisplayed()));

        onView(withId(R.id.nav_about)).perform(click());
        onView(withId(R.id.about_app_name)).check(matches(isDisplayed()));
        onView(withId(R.id.theme_toggle)).check(matches(isDisplayed()));

        onView(withId(R.id.nav_home)).perform(click());
        onView(withId(R.id.continue_button)).check(matches(isDisplayed()));
    }
}
