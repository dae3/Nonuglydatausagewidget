/*
Network statistics interval that restarts on the N-th day of every month
 */

package com.example.dever.nonUglyDataUsageWidget

import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.min

class DayNOfMonthNetworkStatsInterval(today: Calendar, dayOfMonth: Int) : NetworkStatsInterval {
    private var mStartDate : Calendar = today.clone() as Calendar
    private var mEndDate : Calendar = mStartDate.clone() as Calendar

    init {
        // Calendar.DAY_OF_MONTH is 1-based
        if (today.get(Calendar.DAY_OF_MONTH) < dayOfMonth) {
            // interval is from dayOfMonth/today month-1/year roll if required -> dayOfMonth-1/today month/year roll if required
            var m = today.clone() as Calendar; m.add(Calendar.MONTH, -1)
            mStartDate = GregorianCalendar(
                    m.get(Calendar.YEAR),
                    m.get(Calendar.MONTH),
                    min(dayOfMonth, m.getActualMaximum(Calendar.DAY_OF_MONTH))
            )

            mEndDate = today.clone() as Calendar
            if (dayOfMonth > mEndDate.getActualMaximum(Calendar.DAY_OF_MONTH))
                mEndDate.set(Calendar.DAY_OF_MONTH, mEndDate.getActualMaximum(Calendar.DAY_OF_MONTH))
            else {
                mEndDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                mEndDate.add(Calendar.DAY_OF_MONTH, -1)
            }
        } else {
            // interval is from dayOfMonth/today month/today year -> dayOfMonth-1/today month+1/year roll if required
            mStartDate = GregorianCalendar(
                    today.get(Calendar.YEAR),
                    today.get(Calendar.MONTH),
                    dayOfMonth
            )

            mEndDate = GregorianCalendar(
                    if (today.get(Calendar.MONTH) == Calendar.DECEMBER) today.get(Calendar.YEAR)+1 else today.get(Calendar.YEAR),
                    if (today.get(Calendar.MONTH) == Calendar.DECEMBER) Calendar.JANUARY else today.get(Calendar.MONTH)+1,
                    dayOfMonth-1
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