package com.example.dever.nonUglyDataUsageWidget

import android.app.Fragment
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.os.Environment
import android.util.TypedValue.COMPLEX_UNIT_PX
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import kotlinx.android.synthetic.main.pie_chart.*
import java.io.File
import java.io.FileOutputStream
import kotlin.math.roundToInt

/**
 * Subclass of fragment to display network usage stats in main activity (and any other non-
 * widget uses in the future
 */
class PieChartFragment : Fragment(), ViewTreeObserver.OnGlobalLayoutListener, OnClickListener {

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

            txtWidgetDays.setOnClickListener(this)
        } catch (e: SecurityException) {
            widgetChartImageView.visibility = INVISIBLE
            txtWidgetActualData.visibility = INVISIBLE
            txtWidgetDays.visibility = INVISIBLE
            widgetErrorImageView.visibility = VISIBLE
        }
    }

    /**
     * For build purposes only, save a PNG of the current view, for use as an AppWidgetPreview image
     * Also needs WRITE_EXTERNAL_STORAGE permission declared in manifest
     */
    override fun onClick(v: View?) {
        if (BuildConfig.BUILD_TYPE == "dummyData" && v?.id == R.id.txtWidgetDays) {
            var b = Bitmap.createBitmap(view.measuredWidth,view.measuredHeight,Bitmap.Config.ARGB_4444)
            rootView.draw(Canvas(b))


            if (context.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED)
                activity.requestPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),0)
            else {
                val os = FileOutputStream(File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "appwidget_preview.png"))
                val ok = b.compress(Bitmap.CompressFormat.PNG, 0, os)
                os.close()
                Toast.makeText(view.context, "AppWidget preview ${if (!ok) "not " else ""}saved", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
