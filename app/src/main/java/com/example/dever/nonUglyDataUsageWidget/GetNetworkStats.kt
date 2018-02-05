package com.example.dever.nonUglyDataUsageWidget

import android.annotation.SuppressLint
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager.TYPE_MOBILE
import android.preference.PreferenceManager
import android.telephony.TelephonyManager

class GetNetworkStats(private val context: Context, private var interval: NetworkStatsInterval) {

    private var nsm: NetworkStatsManager = context.getSystemService(NetworkStatsManager::class.java)
    private var subscriberId: String

    init {
        if (!PermissionChecker.havePhoneStatePermission(context)) context.startActivity(Intent(context, PrePermissionRequestActivity::class.java))

        // courtesy of https://medium.com/@quiro91/build-a-data-usage-manager-in-android-e7991cfe7fe4
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        try {
            @SuppressLint("HardwareIds") // subscriberID is required to call querySummaryForDevice later
            subscriberId = tm.subscriberId
        } catch (e: SecurityException) {
            // TODO do something with exception: back to PrePermissionRequestActivitity?
            subscriberId = ""
        }
    }

    var actualData: Long = 0L
        get() {
            val bucket = nsm.querySummaryForDevice(TYPE_MOBILE, subscriberId, interval.startDate.timeInMillis, interval.endDate.timeInMillis)
            return bucket.rxBytes + bucket.txBytes
        }

    var maxData: Long = 0L
        get() = PreferenceManager.getDefaultSharedPreferences(context).getLong(
                context.resources.getString(R.string.prefs_key_maxdata),
                1
        )
    // TODO: remove hard-coded default after https://trello.com/c/6BzECuM6 fixed
}