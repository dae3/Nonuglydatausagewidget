package com.example.dever.nonUglyDataUsageWidget

import android.Manifest.permission.PACKAGE_USAGE_STATS
import android.app.AppOpsManager
import android.app.AppOpsManager.*
import android.content.Context
import android.content.Context.APP_OPS_SERVICE
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Process
import android.support.v4.content.ContextCompat

object PermissionChecker {
//    private var havePhoneStatePermission = false

    fun haveUsagePermission(context: Context): Boolean {
        // need to use AppOpsManager for this, checkSelfPermission always returns false
        val aom = context.getSystemService(APP_OPS_SERVICE) as AppOpsManager

        return when (aom.checkOp(OPSTR_GET_USAGE_STATS, Process.myUid(), context.packageName)) {
        // API doco suggests MODE_DEFAULT *can* happen but not clear how or when
            MODE_DEFAULT -> ContextCompat.checkSelfPermission(context, PACKAGE_USAGE_STATS) == PERMISSION_GRANTED
            MODE_ALLOWED -> true
            MODE_ERRORED, MODE_IGNORED -> false
            else -> false
        }
    }

    fun havePhoneStatePermission(context: Context): Boolean {
        return context.checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE) == PERMISSION_GRANTED
    }
}