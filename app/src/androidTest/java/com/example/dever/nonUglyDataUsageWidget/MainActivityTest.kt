package com.example.dever.nonUglyDataUsageWidget

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {
    @get:Rule
    var rule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun mainHasBottomNav() {
        onView(withId(R.id.main_coord_layout))
                .check(matches(hasDescendant(withId(R.id.bottom_navbar))))
                .check(matches(hasDescendant(withId(R.id.main_activity_body))))
    }

    @Test
    fun navBarCanOpenAbout() {
        onView(withId(R.id.bottomnav_about)).perform(click())
        onView(withId(R.id.about_listview)).check(matches(isDisplayed()))
    }

    @Test
    fun navBarCanOpenSettings() {
        onView(withId(R.id.bottomnav_settings)).perform(click())
        onView(withText(R.string.prefs_itemtitle_intervaltype)).check(matches(isDisplayed()))
    }


}