package com.github.dae3.datadial

import android.app.AppOpsManager
import android.content.Context.APP_OPS_SERVICE
import android.content.Intent
import com.github.dae3.datadial.IntentMatcher.Companion.equalToIntent
import junit.framework.Assert.assertNull
import kotlinx.android.synthetic.main.activity_main.*
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.Description
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.android.controller.ActivityController

/**
 * Non-instrumented unit test for MainActivity
 */
@RunWith(RobolectricTestRunner::class)
class MainActivityUnitTest {

    private lateinit var ac: ActivityController<MainActivity>
    private lateinit var ma: MainActivity

    @Suppress("MemberVisibilityCanBePrivate")
    @Before
    fun setUp() {

        ac = Robolectric.buildActivity(MainActivity::class.java)
        ma = ac.get()

        // plug in a mock AppOpsManager that grants all permissions - this is tested elsewhere
        val aom = Mockito.mock(AppOpsManager::class.java)
        Mockito.`when`(aom.checkOp(Mockito.anyString(), Mockito.anyInt(), Mockito.anyString())).thenReturn(AppOpsManager.MODE_ALLOWED)
        Shadows.shadowOf(ma.application).setSystemService(APP_OPS_SERVICE, aom)


        ac.create().start()
    }

    @After
    fun tearDown() {
    }

    @Test
    fun displaysFirstRunActivityOnFirstRun() {

        assertThat(
                Shadows.shadowOf(ma.application).nextStartedActivity,
                equalToIntent(Intent(ma, FirstRunPreferenceCaptureActivity::class.java))
        )
    }

    @Test
    fun displaysFirstRunActivityOnlyOnFirstRun() {
        assertThat(
                Shadows.shadowOf(ma.application).nextStartedActivity,
                equalToIntent(Intent(ma, FirstRunPreferenceCaptureActivity::class.java))
        )

        ac.stop().destroy()
        setUp()

        assertNull(Shadows.shadowOf(ma.application).nextStartedActivity)
    }

    @Test
    fun openSettingsIfFirstRunActivityAsksForThem() {

        Shadows.shadowOf(ma).receiveResult(
                Intent(ma, FirstRunPreferenceCaptureActivity::class.java),
                FirstRunPreferenceResult.ChangePreferences.ordinal,
                Intent()
        )

        assertThat(ma.bottom_navbar.selectedItemId, equalTo(R.id.bottomnav_settings))
    }

    @Test
    fun dontOpenSettingsIfNotAskedForThem() {

        Shadows.shadowOf(ma).receiveResult(
                Intent(ma, FirstRunPreferenceCaptureActivity::class.java),
                FirstRunPreferenceResult.Continue.ordinal,
                Intent()
        )

        assertThat(ma.bottom_navbar.selectedItemId, equalTo(R.id.bottomnav_home))
    }

}

private class IntentMatcher(private var actual: Intent) : org.hamcrest.TypeSafeMatcher<Intent>() {

    companion object {
        fun equalToIntent(actual: Intent): IntentMatcher {
            return IntentMatcher(actual)
        }
    }

    override fun describeTo(description: Description?) {
        description?.appendValue(actual)
    }

    override fun matchesSafely(expected: Intent?): Boolean {
        return actual.component == expected?.component && actual.data == expected?.data
    }
}
