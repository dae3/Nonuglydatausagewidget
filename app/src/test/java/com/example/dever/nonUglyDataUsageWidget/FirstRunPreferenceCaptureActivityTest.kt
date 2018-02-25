package com.example.dever.nonUglyDataUsageWidget

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.test.suitebuilder.annotation.SmallTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.robolectric.RobolectricTestRunner


/**
 * Unit tests for FirstRunPreferenceActivity
 */
@RunWith(RobolectricTestRunner::class)
@SmallTest
class FirstRunPreferenceCaptureActivityTest {

//    @get:Rule
//    val rule = ActivityTestRule(FirstRunPreferenceCaptureActivity::class.java, )
        var mockMain: mockActivity = Mockito.mock(mockActivity::class.java)
        private val requestCode = 0


    @Before
    fun setUp() {
        `when`(mockMain.onActivityResult(eq(requestCode), ArgumentMatchers.anyInt(), ArgumentMatchers.any(Intent::class.java)))
                .someVoid



        // no need to do anything else - Activity has no knowledge of 1st run state, that's
        // MainActivity's problem
    }

    @Test
    fun returnsSuccessCodeIfContinuePressed() {
        mockMain.startActivityForResult(
                Intent(mockMain, FirstRunPreferenceCaptureActivity::class.java),
                requestCode
        )

        verify(
                mockMain.onActivityResult(eq(requestCode), eq(RESULT_OK), ArgumentMatchers.any(Intent::class.java))
        )
    }

    // jank to get around protected visibility in base class
    open inner class mockActivity : Activity() {
        public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

}