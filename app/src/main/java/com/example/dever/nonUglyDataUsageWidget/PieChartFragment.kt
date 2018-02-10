package com.example.dever.nonUglyDataUsageWidget

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.TextView
import java.util.*

class PieChartFragment : Fragment() {

    private lateinit var statsInterval: NetworkStatsInterval
    private lateinit var pie: PieWithTickChart
    private lateinit var networkStats: GetNetworkStats
    private lateinit var img: ImageView

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val v = inflater?.inflate(R.layout.widget, container, false)!!
        img = v.findViewById(R.id.widgetChartImageView)

        statsInterval = NetworkStatsIntervalFactory.getInterval(activity)
        networkStats = GetNetworkStats(activity, statsInterval)

        v.findViewById<TextView>(R.id.txtWidgetActualData).text =
                activity.resources.getString(R.string.widget_data_template, networkStats.actualData.toFloat() / 1024F / 1024F / 1024F)

        v.findViewById<TextView>(R.id.txtWidgetDays).text =
                activity.resources.getString(
                        R.string.widget_days_template,
                        GregorianCalendarDefaultLocale().get(Calendar.DAY_OF_MONTH) - statsInterval.startDate.get(Calendar.DAY_OF_MONTH) + 1,
                        statsInterval.endDate.get(Calendar.DAY_OF_MONTH) - statsInterval.startDate.get(Calendar.DAY_OF_MONTH) + 1
                )
                
        val vto = v.viewTreeObserver
        vto.addOnGlobalLayoutListener(VTObserver(v))

        return v
    }

    inner class VTObserver(val v: View) : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            v.viewTreeObserver.removeOnGlobalLayoutListener(this)
            pie = PieWithTickChart(v.width, v.height, activity)
            pie.drawChart(networkStats.actualData.toDouble(), networkStats.maxData.toDouble(), statsInterval)
            img.setImageBitmap(pie.bitmap)
        }
    }
}