
package com.example.dever.nonUglyDataUsageWidget

import java.util.*

/**
 * Interface representing a billing interval
 */
interface NetworkStatsInterval {
    var startDate: Calendar
    var endDate: Calendar
    override fun toString() : String
}

