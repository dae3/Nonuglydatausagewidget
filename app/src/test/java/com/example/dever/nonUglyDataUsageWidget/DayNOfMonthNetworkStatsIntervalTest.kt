package com.example.dever.nonUglyDataUsageWidget

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.min

/*
    test iterates over
        non-leap year (2018, just because that's now)
        leap-year (2020, next one)
            all months
                all possible N-days

        keeping 'today' fixed as the 15th of the test month
 */

@RunWith(Parameterized::class)
class DayNOfMonthNetworkStatsIntervalTest(private val testToday: Calendar, private val testDayN: Int) {

    private lateinit var interval : NetworkStatsInterval
    private val df = SimpleDateFormat.getDateInstance()
    private val testDisplayName = "(today=${df.format(testToday.timeInMillis)}, Nth=$testDayN)"

    companion object GenerateParameters {
        private val LEAPYEAR = 2020
        private val NONLEAPYEAR = 2018
        private val FIXEDDAY = 15

        @Parameters(name = "{0},{1}")
        @JvmStatic fun params(): ArrayList<Any> {
            var r = ArrayList<Any>()

            for (year in arrayOf(LEAPYEAR, NONLEAPYEAR))
                for (month in 0..11)
                    for (day in 1..GregorianCalendar(year, month, 1).getActualMaximum(Calendar.DAY_OF_MONTH))
                        r.add(arrayOf(GregorianCalendar(year,month, FIXEDDAY),day))

            return r
        }
    }

    @Before
    fun createFixtures() {
        interval = DayNOfMonthNetworkStatsInterval(testToday, testDayN)
    }

    @Test
    fun startMonthIsCorrect() {
        // if today is >= N then start month is current month, otherwise previous month
        assertThat("start month wrong $testDisplayName", interval.startDate.get(Calendar.MONTH), `is`(
                if (testToday.get(Calendar.DAY_OF_MONTH) >= testDayN)
                    testToday.get(Calendar.MONTH)
                else
                    { testToday.roll(Calendar.MONTH, -1); testToday.get(Calendar.MONTH) }
            )
        )
    }

    @Test
    fun startDayIsCorrect() {
        // start day is minimum of N and highest day in the month
        assertThat(
                "start day wrong $testDisplayName",
                interval.startDate.get(Calendar.DAY_OF_MONTH),
                `is`(min(testDayN, interval.startDate.getActualMaximum(Calendar.DAY_OF_MONTH)))
        )
    }

    @Test
    fun endMonthIsCorrect() {
        // if today < N || N == 1 end month is this month, otherwise next
        assertThat(
                "end month wrong $testDisplayName",
                interval.endDate.get(Calendar.MONTH),
                    `is`(
                        if (testToday.get(Calendar.DAY_OF_MONTH) < testDayN || testDayN == 1) testToday.get(Calendar.MONTH)
                        else {
                            var m = testToday.clone() as Calendar
                            m.add(Calendar.MONTH, 1)
                            m.get(Calendar.MONTH)
                        }
                )
        )
    }

    @Test
    fun endDayIsCorrect() {
        // if Nth day is 1 then end day is last day of current month, otherwise N-1
        assertThat("end day is wrong $testDisplayName", interval.endDate.get(Calendar.DAY_OF_MONTH), `is`(
                if (testDayN == 1) {
                    testToday.getActualMaximum(Calendar.DAY_OF_MONTH)
                } else {
                    testDayN - 1
                })
        )
    }

//    @Test
//    fun endYearIsCorrect() {
//        // end year is this year unless current month is December and Nth <> 1
//        var expectedYear =
//                if (testToday.get(Calendar.MONTH) == Calendar.DECEMBER && testDayN > testToday.getActualMinimum(Calendar.DAY_OF_MONTH)) {
//                    testToday.add(Calendar.YEAR, 1)
//                    testToday.get(Calendar.YEAR)
//                } else testToday.get(Calendar.YEAR)
//        assertThat("end year wrong $testDisplayName", expectedYear, `is`(interval.endDate.get(Calendar.YEAR)))
//    }
}