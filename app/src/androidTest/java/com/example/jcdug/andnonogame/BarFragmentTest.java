package com.example.jcdug.andnonogame;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.intent.rule.IntentsTestRule;

import org.junit.Before;
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
import static org.hamcrest.core.AllOf.allOf;

/**
 * Created by jcdug on 10/26/2016.
 */


@RunWith(AndroidJUnit4.class)
@LargeTest
public class BarFragmentTest {

    @Rule
    public ActivityTestRule<MainActivity> mIntentsRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Before
    public void setup() {
        onView(withId(R.id.play_button_main)).perform(click());
        onView(withId(R.id.normal_puzzles_button)).perform(click());
    }

    @Test
    public void testOnClick() {
        onView(allOf(withId(R.id.settings_button_bar), withParent(withId(R.id.fragment_bar_size_select)))).perform(click());
        onView(withId(R.id.activity_settings)).check(matches(isDisplayed()));

        pressBack();

        onView(allOf(withId(R.id.back_button_bar), withParent(withId(R.id.fragment_bar_size_select)))).perform(click());
        onView(withId(R.id.activity_menu)).check(matches(isDisplayed()));

    }
}
