package com.github.dae3.datadial

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.containsString
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ByteArrayOutputStream

/**
 * Instrumented tests for AboutFragment
 */

@RunWith(AndroidJUnit4::class)
@LargeTest
class AboutFragmentTest {

    @Suppress("MemberVisibilityCanBePrivate")
    @get:Rule
    var rule = ActivityTestRule(MainActivity::class.java)


    @Before
    fun setup() {
        onView(withId(R.id.bottomnav_about)).perform(click())
    }

    @Test
    fun clickingVersionDoesNothing() {
        onView(withText(R.string.about_headline_version)).perform(click())
        onView(withId(R.id.about_listview)).check(matches(isDisplayed()))
    }

    @Test
    fun clickingAuthorDoesNothing() {
        onView(withText(R.string.about_headline_author)).perform(click())
        onView(withId(R.id.about_listview)).check(matches(isDisplayed()))
    }

    @Test
    fun clickingLicenseOpensLicenseActivity() {
        onView(withText(R.string.about_headline_license)).perform(click())
        onView(allOf(withId(R.id.action_bar), withChild(withText(R.string.about_headline_license)))).check(matches(isDisplayed()))
    }

    @Test
    fun clickingBackClosesLicenseActivity() {
        onView(withText(R.string.about_headline_license)).perform(click())
        onView(allOf(withId(R.id.action_bar), withChild(withText(R.string.about_headline_license)))).check(matches(isDisplayed()))
        onView(withClassName(containsString("ImageButton"))).perform(click())
        onView(withId(R.id.about_listview)).check(matches(isDisplayed()))
    }

    @Test
    fun licenseActivityContainsLicenseText() {
        onView(withText(R.string.about_headline_license)).perform(click())
        onView(allOf(withClassName(containsString("TextView")), withText(containsString(getTextFromRawResource(R.raw.license))))).check(matches(isDisplayed()))
    }

    private fun getTextFromRawResource(id : Int): String {
        val ris = rule.activity.resources.openRawResource(id)
        val sos = ByteArrayOutputStream()
        ris.copyTo(sos)
        return sos.toString()
    }

    @Test
    fun clickingPrivacyPolicyOpensPrivacyPolicyActivity() {
        onView(withText(R.string.about_headline_privacypolicy)).perform(click())
        onView(allOf(withId(R.id.action_bar), withChild(withText(R.string.about_headline_privacypolicy)))).check(matches(isDisplayed()))
    }

    @Test
    fun clickingBackClosesPrivacyPolicyActivity() {
        onView(withText(R.string.about_headline_privacypolicy)).perform(click())
        onView(withClassName(containsString("ImageButton"))).perform(click())
        onView(withId(R.id.about_listview)).check(matches(isDisplayed()))
    }

    @Test
    fun privacyPolicyActivityContainsPrivacyText() {
        onView(withText(R.string.about_headline_privacypolicy)).perform(click())
        onView(allOf(withClassName(containsString("TextView")), withText(containsString(getTextFromRawResource(R.raw.privacypolicy))))).check(matches(isDisplayed()))
    }

}
