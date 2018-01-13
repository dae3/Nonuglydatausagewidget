package com.example.dever.nonUglyDataUsageWidget

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import java.util.*

@RunWith(Parameterized::class)
class DayNOfMonthNetworkStatsIntervalTest(p : Pair<Int,Int>) {

    private var testparamMonth : Int = p.first
    private var testparamDay : Int = p.second
//    private var interval = DayNOfMonthNetworkStatsInterval(GregorianCalendar(), testparamDay)
    private val now = GregorianCalendar()
    private lateinit var interval : DayNOfMonthNetworkStatsInterval

    companion object GenerateParameters {
        @Parameters
        @JvmStatic fun months() : ArrayList<Pair<Int,Int>> {
            var r = ArrayList<Pair<Int,Int>>()
            for (month in 1..12) 
                for (day in 1..31) 
                    r.add(Pair(month,day))
            return r
        }
    }

    @Before
    fun createFixtures() {
        interval = DayNOfMonthNetworkStatsInterval(now, testparamDay)
    }

    @Suppress("unused")
    @Test
    fun startDayIsNthDayOfMonth() {
        var expectedDate = dateWithZeroHMSM(now)
        expectedDate.set(Calendar.DAY_OF_MONTH, testparamDay)
        assertThat(interval.startDate, equalTo(expectedDate))
    }

    @Test
    fun endDayIsOneDayBeforeStartDay() {
        var expectedDate = dateWithZeroHMSM(now)
//        assertThat(interval.endDate.get(Calendar.MONTH), equalTo(testparamMonth+1))
        assertThat(interval.endDate.get(Calendar.DAY_OF_MONTH), equalTo(testparamDay-1))
    }

    private fun dateWithZeroHMSM(baseDate: Calendar): Calendar {
        with(baseDate) {
            set(Calendar.HOUR, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return baseDate
    }
}