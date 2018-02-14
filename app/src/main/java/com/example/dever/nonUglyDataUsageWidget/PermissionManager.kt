package com.example.dever.nonUglyDataUsageWidget

import android.Manifest.permission.PACKAGE_USAGE_STATS
import android.app.Activity
import android.app.AppOpsManager
import android.app.AppOpsManager.*
import android.content.Context.APP_OPS_SERVICE
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Process
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.startActivity

/**
 * Encapsulate requesting and checking for the permissions we require
 * READ_PHONE_STATE is a normal, dangerous permission, request and check for using the normal API
 * PACKAGE_USAGE_STATS is special, request by launching the system Settings activity, and check for
 *  using AppOpsManager
 *  @param activity  the Activity (not just a Context) requesting or checking
 */
class PermissionManager(private val activity: Activity) : ActivityCompat.OnRequestPermissionsResultCallback {
    //    private var havePhoneStatePermission = false
    private val MYPERMREQREADPHONESTATE = 1

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
     */
    val havePhoneStatePermission: Boolean
        get() {
            return activity.checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE) == PERMISSION_GRANTED
        }

    /**
     * Request PACKAGE_USAGE_PERMISSION (by starting the system AppOps settings Activity
     *  Does *not* check whether permission's already granted (or previously refused)
     */
    fun requestUsagePermission() = startActivity(activity, Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), null)

    /**
     * Request READ_PHONE_STATE permission
     *  Does *not* check whether permission's already granted (or previously refused)
     */
    fun requestPhoneStatePermission() = activity.requestPermissions(arrayOf(android.Manifest.permission.READ_PHONE_STATE), MYPERMREQREADPHONESTATE)

    /**
     * Callback for Activity.requestPermissions
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == MYPERMREQREADPHONESTATE && grantResults.isNotEmpty()) {      // it's probably us
             TODO("do something here when we remember previous permission grant results")
        }
    }
}