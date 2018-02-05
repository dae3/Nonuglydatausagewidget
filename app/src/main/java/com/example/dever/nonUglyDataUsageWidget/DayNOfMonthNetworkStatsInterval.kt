/*
Network statistics interval that restarts on the N-th day of every month
 */

package com.example.dever.nonUglyDataUsageWidget

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.min

private fun Calendar.setMidnight() {
    this.set(Calendar.HOUR, 0)
    this.set(Calendar.MINUTE, 0)
    this.set(Calendar.SECOND, 0)
    this.set(Calendar.MILLISECOND, 0)
    this.set(Calendar.AM_PM, Calendar.AM)
}

private fun Calendar.setFieldToMax(field : Int) = this.set(field, this.getActualMaximum(field))

private fun Calendar.setOneSecBeforeMidnight() {
    this.setFieldToMax(Calendar.HOUR)
    this.setFieldToMax(Calendar.MINUTE)
    this.setFieldToMax(Calendar.SECOND)
    this.setFieldToMax(Calendar.MILLISECOND)
    this.set(Calendar.AM_PM, Calendar.PM)
}


class DayNOfMonthNetworkStatsInterval(today: Calendar, dayOfMonth: Int) : NetworkStatsInterval {
    private var mStartDate: Calendar = today.clone() as Calendar
    private var mEndDate: Calendar = mStartDate.clone() as Calendar

    init {
        // Calendar.DAY_OF_MONTH is 1-based
        if (today.get(Calendar.DAY_OF_MONTH) < dayOfMonth) {
            // interval is from dayOfMonth/today month-1/year roll if required -> dayOfMonth-1/today month/year roll if required
            val m = today.clone() as Calendar; m.add(Calendar.MONTH, -1)
            mStartDate = GregorianCalendarDefaultLocale(
                    m.get(Calendar.YEAR),
                    m.get(Calendar.MONTH),
                    min(dayOfMonth, m.getActualMaximum(Calendar.DAY_OF_MONTH))
            )
            mStartDate.setMidnight()

            mEndDate = today.clone() as Calendar
            if (dayOfMonth > mEndDate.getActualMaximum(Calendar.DAY_OF_MONTH))
                mEndDate.set(Calendar.DAY_OF_MONTH, mEndDate.getActualMaximum(Calendar.DAY_OF_MONTH))
            else {
                mEndDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                mEndDate.add(Calendar.DAY_OF_MONTH, -1)
            }
            mEndDate.setOneSecBeforeMidnight()
        } else {
            // interval is from dayOfMonth/today month/today year -> dayOfMonth-1/today month+1/year roll if required
            // fields <= HOUR default to 0, AM_FM defaults to AM
            mStartDate = GregorianCalendarDefaultLocale(today.get(Calendar.YEAR), today.get(Calendar.MONTH), dayOfMonth)
            mStartDate.setMidnight()

            mEndDate = GregorianCalendarDefaultLocale(
                    if (today.get(Calendar.MONTH) == Calendar.DECEMBER) today.get(Calendar.YEAR) + 1 else today.get(Calendar.YEAR),
                    if (today.get(Calendar.MONTH) == Calendar.DECEMBER) Calendar.JANUARY else today.get(Calendar.MONTH) + 1,
                    dayOfMonth - 1)
            mEndDate.setOneSecBeforeMidnight()
        }
    }

    override var startDate: Calendar = GregorianCalendarDefaultLocale()
        get() = mStartDate

    override var endDate: Calendar = GregorianCalendarDefaultLocale()
        get() = mEndDate

    @SuppressLint("SimpleDateFormat") // specific format wanted for JUnit output, locale works for me
    override fun toString(): String {
        val df = SimpleDateFormat()
        return "${this.javaClass} from ${df.format(mStartDate.time)} to ${df.format(mEndDate.time)}"
    }
}

