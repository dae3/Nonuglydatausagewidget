package com.example.dever.nonUglyDataUsageWidget

import android.content.Context
import android.preference.PreferenceManager
import java.util.*

/**
 * Factory class for NetworkStatsInterval
 */
object NetworkStatsIntervalFactory {

    fun getInterval(context : Context) : NetworkStatsInterval {
        // for now just the one
        return DayNOfMonthNetworkStatsInterval(
                GregorianCalendar(),
                PreferenceManager.getDefaultSharedPreferences(context).getInt(
                        context.resources.getString(R.string.prefs_key_billingcycle_startday),
                        0
                )
        )
    }
}