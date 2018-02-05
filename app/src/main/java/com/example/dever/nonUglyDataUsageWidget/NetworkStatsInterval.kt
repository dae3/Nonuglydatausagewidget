
package com.example.dever.nonUglyDataUsageWidget

import java.util.*

interface NetworkStatsInterval {
    var startDate: Calendar
    var endDate: Calendar
    override fun toString() : String
}

