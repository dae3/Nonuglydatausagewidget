package com.example.dever.nonUglyDataUsageWidget

import android.app.AppOpsManager
import android.content.Context.APP_OPS_SERVICE
import android.content.Intent
import junit.framework.Assert.assertNull
import org.hamcrest.CoreMatchers.equalTo
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

    lateinit var ac : ActivityController<MainActivity>
    lateinit var ma: MainActivity

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
                Shadows.shadowOf(ma.application).nextStartedActivity?.javaClass,
                equalTo(Intent(ma, FirstRunPreferenceResult::class.java).javaClass)
        )
    }

    @Test
    fun displaysFirstRunActivityOnlyOnFirstRun() {
        assertThat(
                Shadows.shadowOf(ma.application).nextStartedActivity?.javaClass,
                equalTo(Intent(ma, FirstRunPreferenceResult::class.java).javaClass)
        )

        ac.stop().destroy()
        setUp()

        assertNull(Shadows.shadowOf(ma.application).nextStartedActivity)
    }

}