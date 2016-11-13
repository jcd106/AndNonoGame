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
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
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

        onView(withId(R.id.reset_all_button)).perform(click());
        onView(withText("No")).check(matches(isDisplayed()));
        onView(withText("No")).perform(click());
        onView(withId(R.id.activity_settings)).check(matches(isDisplayed()));

        onView(withId(R.id.reset_all_button)).perform(click());
        onView(withText("Yes")).check(matches(isDisplayed()));
        onView(withText("Yes")).perform(click());
        onView(withId(R.id.activity_settings)).check(matches(isDisplayed()));
    }
}
