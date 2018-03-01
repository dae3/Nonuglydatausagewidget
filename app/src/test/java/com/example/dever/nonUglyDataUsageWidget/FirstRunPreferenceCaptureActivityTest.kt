package com.example.dever.nonUglyDataUsageWidget


import android.content.SharedPreferences
import junit.framework.TestCase.assertTrue
import kotlinx.android.synthetic.main.activity_first_run_preferences.*
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.shadows.ShadowActivity

/**
 * Unit tests for FirstRunPreferenceActivity
 */
@RunWith(RobolectricTestRunner::class)
class FirstRunPreferenceCaptureActivityTest {

    private val requestCode = 0
    private lateinit var fpActivity: FirstRunPreferenceCaptureActivity
    private lateinit var sp: SharedPreferences
    private lateinit var shadowActivity: ShadowActivity

    @Before
    fun setup() {
        fpActivity = Robolectric.setupActivity(FirstRunPreferenceCaptureActivity::class.java)
        shadowActivity = Shadows.shadowOf(fpActivity)
    }

    @Test
    fun returnsContinueWhenContinueClicked() {
        fpActivity.button_done.performClick()
        assertThat(shadowActivity.resultCode, equalTo(FirstRunPreferenceResult.Continue.ordinal))
        assertTrue(shadowActivity.isFinishing)
    }

    @Test
    fun returnsOPWhenOPClicked() {
        fpActivity.button_changesettings.performClick()
        assertThat(shadowActivity.resultCode, equalTo(FirstRunPreferenceResult.ChangePreferences.ordinal))
        assertTrue(shadowActivity.isFinishing)
    }

    @Test
    fun textHasSameCaptionsAsButtons() {

        assertThat(
                fpActivity.text_blurb.text as String,
                containsString(fpActivity.button_changesettings.text as String)
        )

        assertThat(
                fpActivity.text_blurb.text as String,
                containsString(fpActivity.button_done.text as String)
        )
    }

    @Test
    fun textHasNumbersFromDefaultPreferences() {
        assertThat(
                fpActivity.text_blurb.text as String,
                containsString("${fpActivity.resources.getInteger(R.integer.default_maxdata)} Gb")
        )

        assertThat(
                fpActivity.text_blurb.text as String,
                containsString("day ${fpActivity.resources.getInteger(R.integer.default_interval_startday)} of the month")
        )
    }
}