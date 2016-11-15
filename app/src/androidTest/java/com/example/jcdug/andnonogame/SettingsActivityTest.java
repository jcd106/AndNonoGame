package com.example.jcdug.andnonogame;

import android.graphics.Color;
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
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.example.jcdug.andnonogame.PuzzleActivityTest.withBoxColor;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.StringContains.containsString;

/**
 * Created by jcdug on 10/27/2016.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SettingsActivityTest {
    @Rule
    public ActivityTestRule<MainActivity> mIntentsRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Before
    public void setup() {
        onView(withId(R.id.settings_button_main)).perform(click());
    }

    @Test
    public void testOnClick() {
        onView(withId(R.id.instructions_button)).perform(click());
        onView(withId(R.id.activity_tutorial)).check(matches(isDisplayed()));

        pressBack();

        onView(withId(R.id.reset_all_button)).perform(click());
        onView(withText("Reset All Puzzles!")).check(matches(isDisplayed()));
        onView(withText("No")).perform(click());
        onView(withId(R.id.activity_settings)).check(matches(isDisplayed()));

        onView(withId(R.id.reset_all_button)).perform(click());
        onView(withText("Reset All Puzzles!")).check(matches(isDisplayed()));
        onView(withText("Yes")).perform(click());
        onView(withId(R.id.activity_settings)).check(matches(isDisplayed()));
    }

    @Test
    public void testSpinner() {
        String colorText1 = "Black and White";
        String colorText2 = "Red and White";
        String colorText3 = "Red and Blue";

        onView(withId(R.id.spinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is(colorText1))).perform(click());
        onView(withId(R.id.spinner)).check(matches(withSpinnerText(containsString(colorText1))));

        onView(withId(R.id.spinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is(colorText2))).perform(click());
        onView(withId(R.id.spinner)).check(matches(withSpinnerText(containsString(colorText2))));

        onView(withId(R.id.spinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is(colorText3))).perform(click());
        onView(withId(R.id.spinner)).check(matches(withSpinnerText(containsString(colorText3))));

        onView(withId(R.id.spinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is(colorText1))).perform(click());
    }

    @Test
    public void testColorSchemeFromPuzzle() {
        String colorText1 = "Black and White";
        String colorText2 = "Red and White";
        String colorText3 = "Red and Blue";

        onView(withId(R.id.spinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is(colorText1))).perform(click());
        pressBack();

        onView(withId(R.id.play_button_main)).perform(click());
        onView(withId(R.id.fivebyfive)).perform(click());
        onView(withId(1)).perform(click());
        onView(withId(5)).perform(click());
        onView(withId(5)).check(matches(withBoxColor(Color.DKGRAY)));
        onView(withId(1)).check(matches(withBoxColor(Color.WHITE)));
        onView(withId(R.id.settings_button_bar)).perform(click());

        onView(withId(R.id.spinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is(colorText2))).perform(click());

        pressBack();
        onView(withId(5)).check(matches(withBoxColor(Color.RED)));
        onView(withId(1)).check(matches(withBoxColor(Color.WHITE)));
        onView(withId(R.id.settings_button_bar)).perform(click());

        onView(withId(R.id.spinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is(colorText3))).perform(click());

        pressBack();
        onView(withId(5)).check(matches(withBoxColor(Color.RED)));
        onView(withId(1)).check(matches(withBoxColor(Color.BLUE)));

        onView(withId(R.id.reset_puzzle_button)).perform(click());
        onView(withText("Yes")).perform(click());

        onView(withId(R.id.settings_button_bar)).perform(click());

        onView(withId(R.id.spinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is(colorText1))).perform(click());
    }

    @Test
    public void testResetAll() {

        pressBack();
        onView(withId(R.id.play_button_main)).perform(click());
        onView(withId(R.id.fivebyfive)).perform(click());
        onView(withId(1)).perform(click());


        int[] moves5x5 = {3, 7, 8, 9, 11, 12, 13, 14, 15, 16, 17, 19, 20, 21, 22, 23,24,25};
        for(int i = 0; i < moves5x5.length; i++){
            onView(withId(moves5x5[i])).perform(click());
        }

        onView(withText("OK")).perform(click());

        pressBack();


        onView(withId(3)).perform(click());
        int[] moves5x5two = {3, 13, 23};
        for(int i = 0; i < moves5x5two.length; i++){
            onView(withId(moves5x5two[i])).perform(click());
        }

        pressBack();

        pressBack();
        onView(withId(R.id.tenbyten)).perform(click());
        onView(withId(2)).perform(click());

        int[] moves10x10 = {6, 7, 8, 9, 16, 19, 22, 24, 25, 26, 27, 29, 31, 32, 33, 34, 37, 38,
        39, 40, 41, 44, 45, 46, 47, 48, 50, 51, 52, 53, 54, 57, 58, 59, 60, 61, 62, 63, 65, 66, 68, 69, 70,
        71, 72, 73, 75, 76, 78, 79, 80, 81, 82, 83, 84, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100};
        for(int i = 0; i < moves10x10.length; i++){
            onView(withId(moves10x10[i])).perform(click());
        }

        onView(withText("OK")).perform(click());

        pressBack();
        pressBack();
        onView(withId(R.id.tenbyfive)).perform(click());
        onView(withId(4)).perform(click());

        int[] moves10x5 = {3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
        21, 23, 24, 25, 26, 27, 28, 29, 30, 32, 33, 34, 35, 36, 37, 38, 39, 45, 46, 47, 48};
        for(int i = 0; i < moves10x5.length; i++){
            onView(withId(moves10x5[i])).perform(click());
        }

        onView(withText("OK")).perform(click());

        onView(withId(R.id.settings_button_bar)).perform(click());
        onView(withId(R.id.reset_all_button)).perform(click());
        onView(withText("Yes")).perform(click());

        pressBack();

        for(int i = 1; i <= 50; i++) {
            onView(withId(i)).check(matches(withBoxColor(Color.WHITE)));
        }

        pressBack();
        pressBack();

        onView(withId(R.id.tenbyten)).perform(click());
        onView(withId(2)).perform(click());

        for(int i = 1; i <= 100; i++) {
            onView(withId(i)).check(matches(withBoxColor(Color.WHITE)));
        }

        pressBack();
        pressBack();

        onView(withId(R.id.fivebyfive)).perform(click());
        onView(withId(1)).perform(click());

        for(int i = 1; i <= 25; i++) {
            onView(withId(i)).check(matches(withBoxColor(Color.WHITE)));
        }

        pressBack();

        onView(withId(3)).perform(click());

        for(int i = 1; i <= 25; i++) {
            onView(withId(i)).check(matches(withBoxColor(Color.WHITE)));
        }
    }
}
