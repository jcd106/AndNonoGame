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

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withTagKey;
import static android.support.test.espresso.matcher.ViewMatchers.withTagValue;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by jcdug on 12/9/2016.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ColorPuzzleTest {

    @Rule
    public ActivityTestRule<MainActivity> mIntentsRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Before
    public void setup() {
        onView(withId(R.id.play_button_main)).perform(click());
        onView(withId(R.id.color_puzzles_button)).perform(click());
        onView(withId(R.id.fivebyfive)).perform(click());
        onView(withId(2)).perform(click());
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
        onView(withId(-1*Color.BLUE)).perform(click());
        onView(withId(3)).perform(click());
        onView(withId(3)).check(matches(withBoxColor(Color.BLUE)));
        onView(withId(3)).perform(click());
        onView(withId(3)).check(matches(withBoxColor(Color.WHITE)));

        onView(withId(-1*Color.GREEN)).perform(click());
        onView(withId(3)).perform(click());
        onView(withId(3)).check(matches(withBoxColor(Color.GREEN)));
        onView(withId(3)).perform(click());
        onView(withId(3)).check(matches(withBoxColor(Color.WHITE)));

        onView(withId(-1*Color.DKGRAY)).perform(click());
        onView(withId(3)).perform(click());
        onView(withId(3)).check(matches(withBoxColor(Color.DKGRAY)));
        onView(withId(3)).perform(click());
        onView(withId(3)).check(matches(withBoxColor(Color.WHITE)));
    }

    @Test
    public void testUndo() {
        // Perform a series of clicks
        onView(withId(-1*Color.BLUE)).perform(click());
        onView(withId(3)).perform(click());
        onView(withId(3)).perform(click());
        onView(withId(7)).perform(click());
        onView(withId(8)).perform(click());
        onView(withId(9)).perform(click());
        onView(withId(-1*Color.GREEN)).perform(click());
        onView(withId(9)).perform(click());
        onView(withId(9)).perform(click());


        // Undo each move and check that the correct box is set to the correct color
        onView(withId(9)).check(matches(withBoxColor(Color.WHITE)));
        onView(withId(R.id.undo_button)).perform(click());
        onView(withId(9)).check(matches(withBoxColor(Color.GREEN)));
        onView(withId(R.id.undo_button)).perform(click());
        onView(withId(9)).check(matches(withBoxColor(Color.BLUE)));
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
        onView(withId(3)).check(matches(withBoxColor(Color.BLUE)));
        onView(withId(R.id.undo_button)).perform(click());
        onView(withId(3)).check(matches(withBoxColor(Color.WHITE)));
    }

    @Test
    public void testCompletePuzzle() {

        // Perform the necessary box clicks to complete the puzzle
        onView(withId(-1*Color.GREEN)).perform(click());
        int[] movesGreen = {4, 6, 9, 10, 11};
        for(int i = 0; i < movesGreen.length; i++){
            onView(withId(movesGreen[i])).perform(click());
        }

        onView(withId(-1*Color.BLUE)).perform(click());
        int[] movesBlue = {12, 13, 17, 18, 21, 22};
        for(int i = 0; i < movesBlue.length; i++){
            onView(withId(movesBlue[i])).perform(click());
        }

        onView(withId(-1*Color.DKGRAY)).perform(click());
        onView(withId(16)).perform(click());


        // Check that the popup for the solved puzzle is displayed
        onView(withText("OK")).check(matches(isDisplayed()));
        onView(withText("OK")).perform(click());
        testHasPuzzleFragment();
    }

    @Test
    public void testSaveState() {
        // Click 1st box
        onView(withId(-1*Color.BLUE)).perform(click());
        onView(withId(1)).perform(click());
        pressBack();

        // Select puzzle with id = 2
        onView(withId(2)).perform(click());

        // Check that 1st box is still clicked
        onView(withId(1)).check(matches(withBoxColor(Color.BLUE)));
    }

    @Test
    public void testReset() {

        // Perform clicks
        onView(withId(-1*Color.BLUE)).perform(click());
        onView(withId(3)).perform(click());
        onView(withId(7)).perform(click());

        // Press reset puzzle and deny confirmation
        onView(withId(R.id.reset_puzzle_button)).perform(click());
        onView(withText("Reset Puzzle!")).check(matches(isDisplayed()));
        onView(withText("No")).perform(click());

        // Check that puzzle was not reset
        onView(withId(3)).check(matches(withBoxColor(Color.BLUE)));
        onView(withId(7)).check(matches(withBoxColor(Color.BLUE)));

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
