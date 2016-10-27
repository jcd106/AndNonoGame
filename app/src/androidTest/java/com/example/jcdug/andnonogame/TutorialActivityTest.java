package com.example.jcdug.andnonogame;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by jcdug on 10/27/2016.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TutorialActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mIntentsRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Before
    public void setup() {
        onView(withId(R.id.tutorial_button)).perform(click());
    }

    @Test
    public void testOnClick() {
        onView(withId(R.id.back_button_tutorial)).perform(click());
        onView(withId(R.id.activity_main)).check(matches(isDisplayed()));

        onView(withId(R.id.settings_button_main)).perform(click());
        onView(withId(R.id.instructions_button)).perform(click());
        onView(withId(R.id.back_button_tutorial)).perform(click());
        onView(withId(R.id.activity_settings)).check(matches(isDisplayed()));
    }

}
