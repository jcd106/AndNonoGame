package com.example.jcdug.andnonogame;

import android.support.test.espresso.NoMatchingViewException;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.StringContains.containsString;

/**
 * Created by jcdug on 12/9/2016.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class CreateOwnTest {
    @Rule
    public ActivityTestRule<MainActivity> mIntentsRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Before
    public void setup() {

        // Navigate to the first 5 by 5 puzzle and reset it before each test
        onView(withId(R.id.play_button_main)).perform(click());
    }

    @Test
    public void testNavigation() {
        onView(withId(R.id.create_own_button)).perform(click());
        onView(withId(R.id.activity_create_own)).check(matches(isDisplayed()));
        onView(withId(R.id.goto_create)).perform(click());
        onView(withId(R.id.activity_create_puzzle)).check(matches(isDisplayed()));
        onView(withId(R.id.back_button_create)).perform(click());
        onView(withId(R.id.activity_create_own)).check(matches(isDisplayed()));
        onView(withId(R.id.cancel_create)).perform(click());
        onView(withId(R.id.activity_menu)).check(matches(isDisplayed()));
        onView(withId(R.id.create_own_button)).perform(click());
        onView(withId(R.id.back_button_create)).perform(click());
        onView(withId(R.id.activity_menu)).check(matches(isDisplayed()));
    }

    @Test
    public void testSpinner() {
        String size1 = "5x5";
        String size2 = "5x10";
        String size3 = "10x5";
        String size4 = "10x10";
        onView(withId(R.id.create_own_button)).perform(click());

        onView(withId(R.id.choose_puzzle_size)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is(size1))).perform(click());
        onView(withId(R.id.choose_puzzle_size)).check(matches(withSpinnerText(containsString(size1))));

        onView(withId(R.id.goto_create)).perform(click());
        onView(withId(26)).check(doesNotExist());
        onView(withId(R.id.back_button_create)).perform(click());

        onView(withId(R.id.choose_puzzle_size)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is(size2))).perform(click());
        onView(withId(R.id.choose_puzzle_size)).check(matches(withSpinnerText(containsString(size2))));

        onView(withId(R.id.goto_create)).perform(click());
        onView(withId(26)).check(matches(isDisplayed()));
        onView(withId(51)).check(doesNotExist());
        onView(withId(R.id.back_button_create)).perform(click());

        onView(withId(R.id.choose_puzzle_size)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is(size3))).perform(click());
        onView(withId(R.id.choose_puzzle_size)).check(matches(withSpinnerText(containsString(size3))));

        onView(withId(R.id.goto_create)).perform(click());
        onView(withId(26)).check(matches(isDisplayed()));
        onView(withId(51)).check(doesNotExist());
        onView(withId(R.id.back_button_create)).perform(click());

        onView(withId(R.id.choose_puzzle_size)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is(size4))).perform(click());
        onView(withId(R.id.choose_puzzle_size)).check(matches(withSpinnerText(containsString(size4))));

        onView(withId(R.id.goto_create)).perform(click());
        onView(withId(51)).check(matches(isDisplayed()));
        onView(withId(101)).check(doesNotExist());
    }

    @Test
    public void testCreate() {
        String[] sizes = {"5x5","5x10","10x5","10x10"};
        for(int i=0; i<4; i++) {
            onView(withId(R.id.create_own_button)).perform(click());

            onView(withId(R.id.choose_puzzle_size)).perform(click());
            onData(allOf(is(instanceOf(String.class)), is(sizes[i]))).perform(click());
            onView(withId(R.id.choose_puzzle_size)).check(matches(withSpinnerText(containsString(sizes[i]))));
            onView(withId(R.id.goto_create)).perform(click());

            onView(withId(R.id.save_puzzle_button)).perform(click());
            onView(withText("Nothing to Save")).check(matches(isDisplayed()));
            onView(withText("OK")).perform(click());

            onView(withId(i + 1)).perform(click());
            onView(withId(R.id.save_puzzle_button)).perform(click());

            onView(withId(R.id.activity_create_own)).check(matches(isDisplayed()));
            pressBack();

            onView(withId(R.id.normal_puzzles_button)).perform(click());
            onView(withId(R.id.yourpuzzles)).perform(click());
            onView(withId(i + 1)).perform(click());
            onView(withId(i + 1)).perform(click());
            onView(withText("OK")).perform(click());
            pressBack();
            pressBack();
            pressBack();
        }
    }
}
