package com.example.dever.nonUglyDataUsageWidget

import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Rule

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @Rule
    var myRule : ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java);

    @Test
    fun itShouldOpenPermissionActivityWhenLackingPermissions() {
        var theActivity : MainActivity = myRule.activity
        theActivity.
        assertTrue(false);
    }

}