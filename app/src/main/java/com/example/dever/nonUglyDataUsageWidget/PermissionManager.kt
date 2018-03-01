package com.example.dever.nonUglyDataUsageWidget

import android.Manifest.permission.PACKAGE_USAGE_STATS
import android.Manifest.permission.READ_PHONE_STATE
import android.app.Activity
import android.app.AppOpsManager
import android.app.AppOpsManager.*
import android.content.Context.APP_OPS_SERVICE
import android.content.pm.PackageManager.PERMISSION_DENIED
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Process
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat

/**
 * Encapsulate checking for the permissions we require
 * READ_PHONE_STATE is a normal, dangerous permission, check for using the normal API
 * PACKAGE_USAGE_STATS is special check for using AppOpsManager
 *  @param activity  the Activity (not just a Context) checking
 */
class PermissionManager(private val activity: Activity) {
    private val prefs = PreferenceManager.getDefaultSharedPreferences(activity)

    /**
     * "state" of READ_PHONE permission, used to determine permission UI behaviour
     * @return is self explanatory from the enum names
     *  ignore the static initialiser, it's jst compiler sugar
     */
    var phonePermissionState = PhonePermissionState.NeverRequested
        private set(value) {
            field = value
            prefs.edit()
                    .putInt(activity.resources.getString(R.string.prefs_itemkey_phonepermstate), field.ordinal)
                    .apply()
        }

    init {
        loadStateFromSharedPreferences()
    }

    fun refresh() = loadStateFromSharedPreferences()

    /**
     * Load the state of READ_PHONE_STATE permission from sharedprefernces,
     * then sanity check by asking the system as well.
     */
    private fun loadStateFromSharedPreferences() {
        val ppsFromPrefs = prefs.getInt(
                activity.resources.getString(R.string.prefs_itemkey_phonepermstate),
                PhonePermissionState.NeverRequested.ordinal
        )
        phonePermissionState =
                when (ppsFromPrefs) {
                    PhonePermissionState.NeverRequested.ordinal -> PhonePermissionState.NeverRequested
                    PhonePermissionState.Granted.ordinal -> PhonePermissionState.Granted
                    PhonePermissionState.Denied.ordinal -> PhonePermissionState.Denied
                    PhonePermissionState.DeniedNeverAskAgain.ordinal -> PhonePermissionState.DeniedNeverAskAgain
                    else -> throw IllegalStateException("Unknown PhonePermissionState returned from SharedPreferences $ppsFromPrefs")
                }

        // sanity check that it hasn't changed behind our backs
        phonePermissionState = when {
            haveUsagePermission && havePhonePermission -> PhonePermissionState.Granted
            !(haveUsagePermission && havePhonePermission)
                    && phonePermissionState != PhonePermissionState.DeniedNeverAskAgain
            -> PhonePermissionState.Denied  // not done by us so it must be regular denied
            else -> phonePermissionState
        }
    }

    /**
     * @return Do we have PACKAGE_USAGE_STATS?
     */
    val haveUsagePermission: Boolean
        get() {
            // need to use AppOpsManager for this, checkSelfPermission always returns false
            val aom = activity.getSystemService(APP_OPS_SERVICE) as AppOpsManager

            return when (aom.checkOp(OPSTR_GET_USAGE_STATS, Process.myUid(), activity.packageName)) {
            // API doco suggests MODE_DEFAULT *can* happen but not clear how or when
                MODE_DEFAULT -> ContextCompat.checkSelfPermission(activity, PACKAGE_USAGE_STATS) == PERMISSION_GRANTED
                MODE_ALLOWED -> true
                MODE_ERRORED, MODE_IGNORED -> false
                else -> false
            }
        }

    /**
     * @return Do we have PHONE_STATE_PERMISSION?
     *  Check every time because user could have revoked permissions since we requested and received them
     */
    val havePhonePermission: Boolean
        get() {
            return activity.checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE) == PERMISSION_GRANTED
        }

    /**
     * Callback for Activity.requestPermissions. Updates phonePermissionState
     */
    @Suppress("UNUSED_PARAMETER")
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        if (grantResults.isNotEmpty() && permissions[0] == READ_PHONE_STATE)
            phonePermissionState = when {
                grantResults[0] == PERMISSION_GRANTED -> PhonePermissionState.Granted
                grantResults[0] == PERMISSION_DENIED
                        && activity.shouldShowRequestPermissionRationale(READ_PHONE_STATE) -> PhonePermissionState.Denied
                else -> PhonePermissionState.DeniedNeverAskAgain
            }
    }

    enum class PhonePermissionState {
        NeverRequested, Granted, Denied, DeniedNeverAskAgain
    }
}