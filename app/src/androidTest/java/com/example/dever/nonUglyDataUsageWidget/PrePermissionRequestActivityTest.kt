package com.example.dever.nonUglyDataUsageWidget

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by dever on 31/12/2017.
 */

@RunWith(AndroidJUnit4::class)
class PrePermissionRequestActivityTest {
    @Rule
    var myRule : ActivityTestRule<PrePermissionRequestActivity> = ActivityTestRule(PrePermissionRequestActivity::class.java)

    @Test
    fun itShouldOpenSystemSettingsDialogWhenButtonClicked {
        var act = myRule.activity  // TODO do I need this?
        onView(withId(R.id.button)).perform(ViewActions.click())
    }

}