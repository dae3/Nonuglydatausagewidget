package com.example.dever.nonUglyDataUsageWidget

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import org.hamcrest.CoreMatchers.allOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented tests for PrePermissionRequestActivity
 */

@RunWith(AndroidJUnit4::class)
@LargeTest
class PrePermissionRequestActivityTest {

    @get:Rule
    var rule = ActivityTestRule(PrePermissionRequestActivity::class.java)
    lateinit var pm: PermissionManager

    @Before
    fun setup() {
        pm = PermissionManager(rule.activity)

        // revoke READ_PHONE_STATE and USAGE_ACCESS permissions


        // navigate to the second page; the first is just text
        onView(withId(R.id.imgPrePermRightArrow)).perform(click())
    }

    @Test
    fun page2HasRequestPermissionButtonIfAppropriate() {
        if (pm.havePhonePermission) {
            onView(withId(R.id.prepermission_granted_text_p2)).check(matches(isDisplayed()))
            onView(withId(R.id.imgPrePermRightArrow)).perform(click())
            onView(withId(R.id.prepermission_granted_text_p3)).check(matches(isDisplayed()))
        } else {
            onView(withId(R.id.prepermission_phone_button)).check(matches(isDisplayed()))
            onView(withId(R.id.imgPrePermRightArrow)).perform(click())
            // continue/cancel without perms dialog
            onView(allOf(
                    withResourceName("alertTitle"),
                    withText(R.string.prepermission_alert_dialog_title)
            )).check(matches(isDisplayed()))
        }
    }

    @Test
    fun page3HasRequestPermissionButtonIfAppropriate() {

        if (pm.havePhonePermission) {
            onView(withId(R.id.imgPrePermRightArrow)).perform(click())

            if (pm.haveUsagePermission)
                onView(withId(R.id.prepermission_granted_text_p3)).check(matches(isDisplayed()))
            else
                onView(withId(R.id.usage_grant_permission_button)).check(matches(isDisplayed()))
        }
    }
}