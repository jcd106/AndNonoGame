package com.example.jcdug.andnonogame;

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
public class PuzzleSelectActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mIntentsRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Before
    public void setup() {
        onView(withId(R.id.play_button_main)).perform(click());
        onView(withId(R.id.fivebyfive)).perform(click());
    }

    @Test
    public void testOnClick() {
        onView(withId(1)).perform(click());
        onView(withId(R.id.activity_puzzle)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.puzzle_id_activity),withText("Puzzle: 1"))).check(matches(isDisplayed()));

        pressBack();

        onView(withId(3)).perform(click());
        onView(withId(R.id.activity_puzzle)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.puzzle_id_activity),withText("Puzzle: 3"))).check(matches(isDisplayed()));

        pressBack();
        pressBack();
        onView(withId(R.id.tenbyten)).perform(click());

        onView(withId(2)).perform(click());
        onView(withId(R.id.activity_puzzle)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.puzzle_id_activity),withText("Puzzle: 2"))).check(matches(isDisplayed()));

        pressBack();
        pressBack();
        onView(withId(R.id.tenbyfive)).perform(click());

        onView(withId(4)).perform(click());
        onView(withId(R.id.activity_puzzle)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.puzzle_id_activity),withText("Puzzle: 4"))).check(matches(isDisplayed()));
    }

}
