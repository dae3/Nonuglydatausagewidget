/*
Network statistics interval that restarts on the N-th day of every month
Doesn't yet deal with degenerate cases of day 29, 30, 31
 */

package com.example.dever.nonUglyDataUsageWidget

import java.text.SimpleDateFormat
import java.util.*

class DayNOfMonthNetworkStatsInterval(private val today: Calendar, private val dayOfMonth: Int) : NetworkStatsInterval {
    private var mStartDate : Calendar = GregorianCalendar()
    private var mEndDate : Calendar

    init {
        with(mStartDate) {
            set(Calendar.DAY_OF_MONTH, dayOfMonth)
            set(Calendar.YEAR, today.get(Calendar.YEAR))
            set(Calendar.MONTH, today.get(Calendar.MONTH))
            set(Calendar.HOUR, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        mEndDate = mStartDate.clone() as Calendar

        with(mEndDate) {
            roll(Calendar.MONTH, 1)
            set(Calendar.DAY_OF_MONTH,
                    if (dayOfMonth == mEndDate.getGreatestMinimum(Calendar.DAY_OF_MONTH))
                        mEndDate.getLeastMaximum(Calendar.DAY_OF_MONTH)
                    else
                        dayOfMonth - 1
            )
        }
    }

    override var startDate: Calendar = GregorianCalendar()
        get() = mStartDate

    override var endDate: Calendar = GregorianCalendar()
        get() = mEndDate

    override fun toString(): String {
        val df = SimpleDateFormat()
        return "${this.javaClass} from ${df.format(mStartDate.time)} to ${df.format(mEndDate.time)}"
    }
}