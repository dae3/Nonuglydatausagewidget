package com.example.dever.nonUglyDataUsageWidget


import android.widget.Button
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows

/**
 * Unit tests for FirstRunPreferenceActivity
 */
@RunWith(RobolectricTestRunner::class)
class FirstRunPreferenceCaptureActivityTest {

    private val requestCode = 0
    private lateinit var fpActivity: FirstRunPreferenceCaptureActivity

    @Before
    fun setup() {
        fpActivity = Robolectric.setupActivity(FirstRunPreferenceCaptureActivity::class.java)
    }

    @Test
    fun returnsContinueWhenContinueClicked() {
        fpActivity.findViewById<Button>(R.id.button_done).performClick()
        assertThat(Shadows.shadowOf(fpActivity).resultCode, equalTo(FirstRunPreferenceResult.Continue.ordinal))
    }

    @Test
    fun returnsOPWhenOPClicked() {
        fpActivity.findViewById<Button>(R.id.button_changesettings).performClick()
        assertThat(Shadows.shadowOf(fpActivity).resultCode, equalTo(FirstRunPreferenceResult.ChangePreferences.ordinal))
    }

}