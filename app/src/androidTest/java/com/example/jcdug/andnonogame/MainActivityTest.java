package com.example.jcdug.andnonogame;

import android.support.test.espresso.intent.rule.IntentsTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;

/**
 * Created by jcdug on 10/27/2016.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mIntentsRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Test
    public void testOnClick() {
        onView(withId(R.id.play_button_main)).perform(click());
        onView(withId(R.id.activity_size_select)).check(matches(isDisplayed()));

        pressBack();
        onView(withId(R.id.activity_main)).check(matches(isDisplayed()));

        onView(withId(R.id.settings_button_main)).perform(click());
        onView(withId(R.id.activity_settings)).check(matches(isDisplayed()));

        pressBack();
        onView(withId(R.id.activity_main)).check(matches(isDisplayed()));

        onView(withId(R.id.tutorial_button)).perform(click());
        onView(withId(R.id.activity_tutorial)).check(matches(isDisplayed()));

        pressBack();
        onView(withId(R.id.activity_main)).check(matches(isDisplayed()));
    }
}