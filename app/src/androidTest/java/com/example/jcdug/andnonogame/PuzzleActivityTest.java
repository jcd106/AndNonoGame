package com.example.jcdug.andnonogame;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.support.test.espresso.intent.Checks;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.TextView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by jcdug on 10/27/2016.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class PuzzleActivityTest {
    @Rule
    public ActivityTestRule<MainActivity> mIntentsRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Before
    public void setup() {

        // Navigate to the first 5 by 5 puzzle and reset it before each test
        onView(withId(R.id.play_button_main)).perform(click());
        onView(withId(R.id.normal_puzzles_button)).perform(click());
        onView(withId(R.id.fivebyfive)).perform(click());
        onView(withId(1)).perform(click());
        onView(withId(R.id.reset_puzzle_button)).perform(click());
        onView(withText("Yes")).perform(click());
    }

    @Test
    public void testHasPuzzleFragment() {

        // Check that the fragment containing the puzzle is displayed
        onView(withId(R.id.blank_fragment)).check(matches(isDisplayed()));
    }

    @Test
    public void testBoxClick() {
        // Test box clicks correctly change box color
        onView(withId(3)).perform(click());
        onView(withId(3)).check(matches(withBoxColor(Color.DKGRAY)));
        onView(withId(3)).perform(click());
        onView(withId(3)).check(matches(withBoxColor(Color.WHITE)));
    }

    @Test
    public void testUndo() {
        // Perform a series of clicks
        onView(withId(3)).perform(click());
        onView(withId(3)).perform(click());
        onView(withId(7)).perform(click());
        onView(withId(8)).perform(click());
        onView(withId(9)).perform(click());

        // Undo each move and check that the correct box is set to the correct color
        onView(withId(R.id.undo_button)).perform(click());
        onView(withId(9)).check(matches(withBoxColor(Color.WHITE)));
        onView(withId(R.id.undo_button)).perform(click());
        onView(withId(8)).check(matches(withBoxColor(Color.WHITE)));
        onView(withId(8)).perform(click());
        onView(withId(R.id.undo_button)).perform(click());
        onView(withId(8)).check(matches(withBoxColor(Color.WHITE)));
        onView(withId(R.id.undo_button)).perform(click());
        onView(withId(7)).check(matches(withBoxColor(Color.WHITE)));
        onView(withId(R.id.undo_button)).perform(click());
        onView(withId(3)).check(matches(withBoxColor(Color.DKGRAY)));
        onView(withId(R.id.undo_button)).perform(click());
        onView(withId(3)).check(matches(withBoxColor(Color.WHITE)));
    }

    @Test
    public void testCompletePuzzle() {

        // Perform the necessary box clicks to complete the puzzle
        int[] moves = {3, 7, 8, 9, 11, 12, 13, 14, 15, 16, 17, 19, 20, 21, 22, 23,24,25};
        for(int i = 0; i < moves.length; i++){
            onView(withId(moves[i])).perform(click());
        }

        // Check that the popup for the solved puzzle is displayed
        onView(withText("OK")).check(matches(isDisplayed()));
        onView(withText("OK")).perform(click());
        testHasPuzzleFragment();
    }

    @Test
    public void testSaveState() {
        // Click 1st box
        onView(withId(1)).perform(click());
        pressBack();

        // Select puzzle with id = 1
        onView(withId(1)).perform(click());

        // Check that 1st box is still clicked
        onView(withId(1)).check(matches(withBoxColor(Color.DKGRAY)));
    }

    @Test
    public void testReset() {

        // Perform clicks
        onView(withId(3)).perform(click());
        onView(withId(7)).perform(click());

        // Press reset puzzle and deny confirmation
        onView(withId(R.id.reset_puzzle_button)).perform(click());
        onView(withText("Reset Puzzle!")).check(matches(isDisplayed()));
        onView(withText("No")).perform(click());

        // Check that puzzle was not reset
        onView(withId(3)).check(matches(withBoxColor(Color.DKGRAY)));
        onView(withId(7)).check(matches(withBoxColor(Color.DKGRAY)));

        // Reset puzzle
        onView(withId(R.id.reset_puzzle_button)).perform(click());
        onView(withText("Yes")).perform(click());

        // Check that all boxes have been reset
        for(int i = 1; i <= 25; i++) {
            onView(withId(i)).check(matches(withBoxColor(Color.WHITE)));
        }
    }

    // Matcher which allows the color of boxes to be checked
    public static Matcher<View> withBoxColor(final int color) {
        Checks.checkNotNull(color);
        return new BoundedMatcher<View, TextView>(TextView.class) {
            @Override
            public boolean matchesSafely(TextView box) {
                PorterDuffColorFilter filter = new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY);
                return filter.equals(box.getBackground().getColorFilter());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Color did not match");
            }
        };
    }
}
