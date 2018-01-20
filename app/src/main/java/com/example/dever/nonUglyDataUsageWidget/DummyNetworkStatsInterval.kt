package com.example.dever.nonUglyDataUsageWidget

import java.text.DateFormat.getDateInstance
import java.util.*

class DummyNetworkStatsInterval : NetworkStatsInterval {
    override var startDate: Calendar = GregorianCalendar()
        get() = genFirstOfMonth()

    private fun genFirstOfMonth() : Calendar {
        var startdate = GregorianCalendar()
        with(startdate) {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return startdate
    }
    override var endDate: Calendar = GregorianCalendar()
            get() = genDatePlus30(genFirstOfMonth())

    private fun genDatePlus30(d : Calendar) : Calendar {
        d.add(Calendar.DAY_OF_MONTH, 30)
        return d
    }

    override fun toString(): String {
        val df = getDateInstance()
        return "From ${df.format(startDate)} to ${df.format(endDate)}"
    }
}

