package ch.epfl.sdp.appart;

import android.app.Activity;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.sdp.appart.virtualtour.VirtualTourActivity;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;

@HiltAndroidTest
public class VirtualTourUITest {

    @Rule(order = 0)
    public HiltAndroidRule hiltRule  = new HiltAndroidRule(this);

    @Rule(order = 1)
    public ActivityScenarioRule vtourActivityRule =
            new ActivityScenarioRule<>(VirtualTourActivity.class);

    @Before
    public void init() {
        hiltRule.inject();
    }

    @Test
    public void backButtonClosesActivity(){
        onView(withId(R.id.tourBackButton)).perform(click());
        assertEquals(Activity.RESULT_CANCELED, vtourActivityRule.getScenario().getResult()
                .getResultCode());
    }
}
