package ch.epfl.sdp.appart;

import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.sdp.appart.ad.Ad;
import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.database.MockDatabaseService;
import ch.epfl.sdp.appart.hilt.DatabaseModule;
import ch.epfl.sdp.appart.hilt.LoginModule;
import ch.epfl.sdp.appart.login.LoginService;
import ch.epfl.sdp.appart.login.MockLoginService;
import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;

import static android.app.Activity.RESULT_CANCELED;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;

@UninstallModules({DatabaseModule.class, LoginModule.class})
@HiltAndroidTest
public class AdUITest {

    static final String cardID = "1PoUWbeNHvMNotxwAui5";
    static final Intent intent;

    static {
        intent = new Intent(ApplicationProvider.getApplicationContext(), AdActivity.class);
        intent.putExtra("cardID", cardID);
    }

    @BindValue
    final
    DatabaseService database = new MockDatabaseService();
    @BindValue
    final
    LoginService login = new MockLoginService();
    @Rule(order = 0)
    public final HiltAndroidRule hiltRule = new HiltAndroidRule(this);
    @Rule(order = 1)
    public ActivityScenarioRule<AdActivity> adActivityRule = new ActivityScenarioRule<>(intent);

    @Before
    public void init() {
        Intents.init();
        hiltRule.inject();
    }

    @Test
    public void clickOnVTourOpensVTourActivity() {
        onView(withId(R.id.vtour_Ad_button)).perform(scrollTo(), click());
        intended(hasComponent(PanoramaActivity.class.getName()));
    }

    @Test
    public void clickOnContactInfoOpensUserPage() {
        onView(withId(R.id.contact_info_Ad_button)).perform(scrollTo(), click());
        intended(hasComponent(SimpleUserProfileActivity.class.getName()));
    }

    @Test
    public void clickOnGoBackFinishes() {
        onView(withId(R.id.back_Ad_button)).perform(click());
        assertEquals(adActivityRule.getScenario().getResult().getResultCode(), RESULT_CANCELED);
    }

    @Test
    public void displayAdInfoTest() {
        Ad testAd = database.getAd(cardID).join();

        onView(withId(R.id.title_Ad_textView)).check(matches(withText(testAd.getTitle())));
        onView(withId(R.id.address_field_Ad_textView)).check(matches(withText(
                testAd.getStreet() + ", " + testAd.getCity())));
        onView(withId(R.id.price_field_Ad_textView)).check(matches(withText(
                testAd.getPrice() + " / " + testAd.getPricePeriod().toString())));
        onView(withId(R.id.description_field_Ad_textView)).check(matches(withText(testAd.getDescription())));
    }

    @After
    public void release() {
        Intents.release();
    }
}
