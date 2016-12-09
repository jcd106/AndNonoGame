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
        //1. Test play button
        onView(withId(R.id.play_button_main)).perform(click());
        onView(withId(R.id.activity_menu)).check(matches(isDisplayed()));

        //2. Test the hardware back button works properly
        pressBack();
        onView(withId(R.id.activity_main)).check(matches(isDisplayed()));

        //3. Test settings button
        onView(withId(R.id.settings_button_main)).perform(click());
        onView(withId(R.id.activity_settings)).check(matches(isDisplayed()));

        //4. Same as 2 from different screen
        pressBack();
        onView(withId(R.id.activity_main)).check(matches(isDisplayed()));

        //5. Test tutorial button
        onView(withId(R.id.tutorial_button)).perform(click());
        onView(withId(R.id.activity_tutorial)).check(matches(isDisplayed()));

        //6. Same as 2 and 4 from different screen
        pressBack();
        onView(withId(R.id.activity_main)).check(matches(isDisplayed()));
    }
}