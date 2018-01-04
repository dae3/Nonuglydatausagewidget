/**
 * Created by dever on 3/01/2018.
 */
package com.example.dever.nonUglyDataUsageWidget

import android.app.usage.NetworkStatsManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager.TYPE_MOBILE
import android.telephony.TelephonyManager

class GetNetworkStats(private var context : Context) {

    private var nsm : NetworkStatsManager = context.getSystemService(NetworkStatsManager::class.java)
    private lateinit var subscriberId : String

    init {
        if (!PermissionChecker.havePhoneStatePermission(context)) context.startActivity(Intent(context, PrePermissionRequestActivity::class.java))

        // courtesy of https://medium.com/@quiro91/build-a-data-usage-manager-in-android-e7991cfe7fe4
        var tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        try {
            subscriberId = tm.subscriberId
        } catch (e : SecurityException) {
            // TODO do something with exception: back to PrePermissionRequestActivitity?
            subscriberId = ""
        }
    }

    fun getNetworkStats() : Long {
        var bucket = nsm.querySummaryForDevice(TYPE_MOBILE, subscriberId, Long.MIN_VALUE, Long.MAX_VALUE)
        return bucket.rxBytes + bucket.txBytes
    }
}