package com.example.dever.nonUglyDataUsageWidget

import android.app.Fragment
import android.os.Bundle
import android.util.TypedValue.COMPLEX_UNIT_PX
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.view.ViewTreeObserver
import kotlinx.android.synthetic.main.pie_chart.*
import kotlin.math.roundToInt

/**
 * Subclass of fragment to display network ussage stats in main activity (and any other non-
 * widget uses in the future
 */
class PieChartFragment : Fragment(), ViewTreeObserver.OnGlobalLayoutListener {

    private lateinit var statsInterval: NetworkStatsInterval
    private lateinit var networkStats: GetNetworkStats
    private lateinit var pie: PieWithTickChart
    private lateinit var rootView: View
    private var layoutDone = false

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        rootView = inflater?.inflate(R.layout.pie_chart, container, false)!!

        statsInterval = NetworkStatsIntervalFactory.getInterval(activity)
        networkStats = GetNetworkStats(activity, statsInterval)

        // defer drawing until the view has been laid out and sizes calculated
        rootView.viewTreeObserver.addOnGlobalLayoutListener(this)

        return rootView
    }

    override fun onResume() {
        super.onResume()
        if (layoutDone) onGlobalLayout()
    }

    override fun onGlobalLayout() {

        rootView.viewTreeObserver.removeOnGlobalLayoutListener(this)
        if (!layoutDone) {
            layoutDone = true
            progressBar.visibility = GONE
        }

        val density = resources.displayMetrics.density
        pie = PieWithTickChart(context, rootView.width / density.roundToInt(), rootView.height / density.roundToInt(), statsInterval, networkStats)

        try {
            widgetChartImageView.setImageBitmap(pie.bitmap)
            txtWidgetActualData.text = pie.actualDataText
            // this doesn't make sense since we gave the PieWithTickChart constructor dimensions
            // in DP, but hey, it works
            txtWidgetActualData.setTextSize(COMPLEX_UNIT_PX, pie.actualDataTextSize)
            txtWidgetDays.text = pie.daysText
            txtWidgetDays.setTextSize(COMPLEX_UNIT_PX, pie.daysTextSize)

            widgetChartImageView.visibility = VISIBLE
            txtWidgetActualData.visibility = VISIBLE
            txtWidgetDays.visibility = VISIBLE
            widgetErrorImageView.visibility = INVISIBLE
        } catch (e: SecurityException) {
            widgetChartImageView.visibility = INVISIBLE
            txtWidgetActualData.visibility = INVISIBLE
            txtWidgetDays.visibility = INVISIBLE
            widgetErrorImageView.visibility = VISIBLE
        }
    }
}
