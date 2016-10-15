package rorchackh.maradio.activities;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import rorchackh.maradio.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class AllRadiosActivityTest {

    @Rule
    public ActivityTestRule<AllRadiosActivity> mActivityRule = new ActivityTestRule<>(AllRadiosActivity.class);

    @Test
    public void shouldBeAbleToLunchActivity() {
        onView(withId(R.id.container)).check(matches(isDisplayed()));
    }

    public void shouldContainAListOfRadios() {
        onView(withId(R.id.radio_list)).check(matches(isDisplayed()));
    }
}
