package com.example.dever.nonUglyDataUsageWidget

import android.app.Fragment
import android.os.Bundle
import android.util.TypedValue.COMPLEX_UNIT_PX
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import kotlin.math.roundToInt

class PieChartFragment : Fragment(), ViewTreeObserver.OnGlobalLayoutListener {

    private lateinit var statsInterval: NetworkStatsInterval
    private lateinit var networkStats: GetNetworkStats
    private lateinit var pie: PieWithTickChart
    private lateinit var img: ImageView
    private lateinit var txtData: TextView
    private lateinit var txtDays: TextView
    private lateinit var imgError : ImageView
    private lateinit var rootView: View
    private var layoutDone = false

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        rootView = inflater?.inflate(R.layout.widget, container, false)!!
        img = rootView.findViewById(R.id.widgetChartImageView)
        txtData = rootView.findViewById(R.id.txtWidgetActualData)
        txtDays = rootView.findViewById(R.id.txtWidgetDays)
        imgError = rootView.findViewById(R.id.widgetErrorImageView)

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

        rootView?.viewTreeObserver.removeOnGlobalLayoutListener(this)
        if (!layoutDone) {
            layoutDone = true
            rootView.findViewById<ProgressBar>(R.id.progressBar).visibility = GONE
        }

        val density = resources.displayMetrics.density
        pie = PieWithTickChart(context, rootView.width/density.roundToInt(), rootView.height/density.roundToInt(), statsInterval, networkStats)

        try {
            img.setImageBitmap(pie.bitmap)
            txtData.text = pie.actualDataText
            // this doesn't make sense since we gave the PieWithTickChart constructor dimensions
            // in DP, but hey, it works
            txtData.setTextSize(COMPLEX_UNIT_PX, pie.actualDataTextSize)
            txtDays.text = pie.daysText
            txtDays.setTextSize(COMPLEX_UNIT_PX, pie.daysTextSize)

            img.visibility = VISIBLE
            txtData.visibility = VISIBLE
            txtDays.visibility = VISIBLE
            imgError.visibility = INVISIBLE
        } catch (e: SecurityException) {
            img.visibility = INVISIBLE
            txtData.visibility = INVISIBLE
            txtDays.visibility = INVISIBLE
            imgError.visibility = VISIBLE
        }
    }
}
