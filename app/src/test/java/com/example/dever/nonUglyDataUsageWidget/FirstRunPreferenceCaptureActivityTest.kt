package com.example.dever.nonUglyDataUsageWidget

import android.content.Intent
import android.test.suitebuilder.annotation.SmallTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.mockito

/**
 * Unit tests for FirstRunPreferenceActivity
 */
@RunWith(RobolectricTestRunner::class)
@SmallTest
class FirstRunPreferenceCaptureActivityTest {

//    @get:Rule
//    val rule = ActivityTestRule(FirstRunPreferenceCaptureActivity::class.java, )
        var mockMain: MainActivity = Mockito.


    @Before
    fun setUp() {
        mockMain = Robolectric.setupActivity(MainActivity::class.java)

        // no need to do anything else - Activity has no knowledge of 1st run state, that's
        // MainActivity's problem
    }

    @Test
    fun returnsSuccessCodeIfContinuePressed() {
        mockMain.startActivityForResult(
                Intent(mockMain, FirstRunPreferenceCaptureActivity::class.java),
                0
        )
    }

}