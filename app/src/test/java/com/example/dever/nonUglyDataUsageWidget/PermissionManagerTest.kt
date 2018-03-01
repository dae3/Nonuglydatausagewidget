package com.example.dever.nonUglyDataUsageWidget

import android.app.Activity
import android.app.AppOpsManager
import android.content.Context.APP_OPS_SERVICE
import android.test.suitebuilder.annotation.SmallTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.shadows.ShadowApplication

/**
 * Unit tests for PermissionManager
 */
const val PHONE_PERM: String = "android.permission.READ_PHONE_STATE"

@RunWith(RobolectricTestRunner::class)
@SmallTest
class PermissionManagerTest {

    private var aomGranted = Mockito.mock(AppOpsManager::class.java)
    private var aomNotGranted = Mockito.mock(AppOpsManager::class.java)
    private var main: Activity = Robolectric.buildActivity(MainActivity::class.java).get()
    private var app: ShadowApplication = Shadows.shadowOf(main.application)
    private lateinit var pm: PermissionManager

    @Before
    fun setup() {
        `when`(aomGranted.checkOp(eq(AppOpsManager.OPSTR_GET_USAGE_STATS), anyInt(), anyString()))
                .thenReturn(android.app.AppOpsManager.MODE_ALLOWED)
        `when`(aomNotGranted.checkOp(eq(AppOpsManager.OPSTR_GET_USAGE_STATS), anyInt(), anyString()))
                .thenReturn(android.app.AppOpsManager.MODE_ERRORED)

        // PermissionManager constructor will call AOM
        app.setSystemService(APP_OPS_SERVICE, aomNotGranted)
        pm = PermissionManager(main)
    }


    @Test
    fun phonePermissionCorrect() {
        app.grantPermissions(PHONE_PERM)
        assertEquals(true, pm.havePhonePermission)

        app.denyPermissions(PHONE_PERM)
        assertEquals(false, pm.havePhonePermission)
    }

    @Test
    fun usagePermissionCorrect() {
        appHasUsagePermission(true)
        assertEquals(true, pm.haveUsagePermission)

        appHasUsagePermission(false)
        assertEquals(false, pm.haveUsagePermission)
    }

    @Test
    fun initialPhonePermissionStateIsNeverGranted() {
        appHasUsagePermission(false)
        assertEquals(PermissionManager.PhonePermissionState.NeverRequested, pm.phonePermissionState)
    }

    @Test
    fun phonePermissionStateCorrectOnGranted() {
        app.grantPermissions(PHONE_PERM)
        appHasUsagePermission(true)
        assertEquals(PermissionManager.PhonePermissionState.Granted, pm.phonePermissionState)

        app.grantPermissions(PHONE_PERM)
        appHasUsagePermission(false)
        assertEquals(PermissionManager.PhonePermissionState.Granted, pm.phonePermissionState)
    }

    @Test
    fun phonePermissionStateCorrectOnDenied() {
        appHasUsagePermission(true)
        app.denyPermissions(PHONE_PERM)
        assertEquals(PermissionManager.PhonePermissionState.Denied, pm.phonePermissionState)

        appHasUsagePermission(false)
        app.denyPermissions(PHONE_PERM)
        assertEquals(PermissionManager.PhonePermissionState.Denied, pm.phonePermissionState)
    }


    private fun appHasUsagePermission(has: Boolean) =
            app.setSystemService(APP_OPS_SERVICE, if (has) aomGranted else aomNotGranted)
}

