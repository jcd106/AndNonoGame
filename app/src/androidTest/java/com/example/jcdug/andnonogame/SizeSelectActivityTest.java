package com.example.jcdug.andnonogame;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.AllOf.allOf;

/**
 * Created by jcdug on 10/27/2016.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SizeSelectActivityTest {
    @Rule
    public ActivityTestRule<MainActivity> mIntentsRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Before
    public void setup() {
        onView(withId(R.id.play_button_main)).perform(click());
        onView(withId(R.id.normal_puzzles_button)).perform(click());
    }

    @Test
    public void testOnClick() {
        onView(withId(R.id.fivebyfive)).perform(click());
        onView(withId(R.id.activity_puzzle_select)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.puzzle_select_size), withText("5x5"))).check(matches(isDisplayed()));

        pressBack();

        onView(withId(R.id.tenbyten)).perform(click());
        onView(withId(R.id.activity_puzzle_select)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.puzzle_select_size), withText("10x10"))).check(matches(isDisplayed()));

        pressBack();

        onView(withId(R.id.tenbyfive)).perform(click());
        onView(withId(R.id.activity_puzzle_select)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.puzzle_select_size), withText("10x5"))).check(matches(isDisplayed()));
    }
}
