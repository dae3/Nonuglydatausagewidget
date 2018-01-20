/**
 * Created by dever on 3/01/2018.
 */
package com.example.dever.nonUglyDataUsageWidget

import android.annotation.SuppressLint
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager.TYPE_MOBILE
import android.preference.PreferenceManager
import android.telephony.TelephonyManager
import android.util.Log
import java.text.DateFormat.getDateInstance

class GetNetworkStats(val interval: NetworkStatsInterval, private var context: Context) {

    private var nsm : NetworkStatsManager = context.getSystemService(NetworkStatsManager::class.java)
    private var subscriberId : String
    private var prefs = PreferenceManager.getDefaultSharedPreferences(context)


    init {
        if (!PermissionChecker.havePhoneStatePermission(context)) context.startActivity(Intent(context, PrePermissionRequestActivity::class.java))

        // courtesy of https://medium.com/@quiro91/build-a-data-usage-manager-in-android-e7991cfe7fe4
        var tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        try {
            @SuppressLint("HardwareIds") // subscriberID is required to call querySummaryForDevice later
            subscriberId = tm.subscriberId
        } catch (e : SecurityException) {
            // TODO do something with exception: back to PrePermissionRequestActivitity?
            subscriberId = ""
        }
    }

    fun getNetworkStats() : Long {
        val df = getDateInstance()
        Log.d(context.packageName, interval.toString())

        val bucket = nsm.querySummaryForDevice(TYPE_MOBILE, subscriberId, interval.startDate.timeInMillis, interval.endDate.timeInMillis)
        return bucket.rxBytes + bucket.txBytes
    }
}