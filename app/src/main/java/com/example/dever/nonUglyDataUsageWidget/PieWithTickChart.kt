package com.example.dever.nonUglyDataUsageWidget

import android.content.Context
import android.graphics.*
import java.util.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * Encapsulation of the drawing and caption generation for a 'pie' chart representation
 * of data usage (actually more of a speedo gauge but the name has stuck.
 * Implemented as a class exposing Bitmap and String values rather than a View
 * so it can be used within an AppWidget as well as a fragment
 *
 * Intended for use with R.layout.widget
 *
 * @param context Context from containing activity, view, etc.
 * @param width width in dp of the outermost ImageView
 * @param height height in dp of the outermost ImageView
 * @param interval an object implementing NetworkStatsInterval which
 * will provide start and end dates for the period
 * @param stats a GetNetworkStats object to provide actual data usage
 */
class PieWithTickChart(
        private val context: Context,
        private val width: Int,
        private val height: Int,
        private val interval: NetworkStatsInterval,
        private val stats: GetNetworkStats
) {

    init {
        if (width == 0 || height == 0) throw IllegalArgumentException("both width and height must be non-zero")
    }

    private val paintbox = PaintBox(context)
    private val availsize: Float = min(width, height) * 0.7F

    /**
     * The text to display for actual data used
     * @return formatted text (String) using R.string.widget_data_template
     */
    val actualDataText: String
        get() = context.resources.getString(R.string.widget_data_template, stats.actualData.toFloat() / 1024F / 1024F / 1024F)

    /**
     * Text size in dp for the actual data TextView, calculated from the widget size
     * Uses a simple algorithm:
     *  Avail space = 0.8 * min(width, height)
     *  Data text = 0.6 * Avail space
     *  Days text = (1-Data text) * Avail space
     *
     *  have to do text scaling ourselves because TextViewCompat (which does autosizing) doesn't work inside a RemoteViews, see
     * @link(https://stackoverflow.com/questions/45412380/autosize-text-in-homescreen-widget-with-support-library) and
     * @link(https://issuetracker.google.com/issues/37071559)
     * and TextView doesn't do autosizing until API 26
     *
     *  @return font size in dp
     */
    val actualDataTextSize: Float
        get() = availsize * 0.6F

    /**
     * Text size in dp for the days TextView, calculated from the widget size
     * @see actualDataTextSize
     *  @return font size in dp
     */
    val daysTextSize: Float
        get() = availsize * 0.3F

    /**
     * The text to display for the 'day N of M' TextView
     * @return formatted string using R.string.widget_days_template
     */
    val daysText: String
        get() = context.resources.getString(
                    R.string.widget_days_template,
                    GregorianCalendarDefaultLocale().get(Calendar.DAY_OF_MONTH) - interval.startDate.get(Calendar.DAY_OF_MONTH) + 1,
                    interval.endDate.get(Calendar.DAY_OF_MONTH) - interval.startDate.get(Calendar.DAY_OF_MONTH) + 1
            )

    /**
     * Bitmap representation of data used, as a 'pie' chart
     * @return bitmap object, size set by class constructor width and height parameters
     */
    val bitmap: Bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444)
        get() {
            val maxData = stats.maxData.toDouble()
            var canvas = Canvas(field)
            if (maxData == 0.0) {
                throw IllegalArgumentException("maxData must be non-zero")
            }
            canvas.drawColor(Color.TRANSPARENT)
            val donutSize = 0.75
            val path = Path()
            path.addCircle(pieX(), pieY(), (pieRadius() * donutSize).toFloat(), Path.Direction.CW)
            @Suppress("DEPRECATION")
            canvas.clipPath(path, Region.Op.DIFFERENCE)
            canvas.drawCircle(pieX(), pieY(), pieRadius(), paintbox.pieBg)
            val rectWedge = RectF(
                    pieX() - pieRadius(),
                    pieY() - pieRadius(),
                    pieX() + pieRadius(),
                    pieY() + pieRadius()
            )
            val sweepangle = min((stats.actualData.toDouble() / maxData * 360).toFloat(), 360F)
            val startangle = 0F - 90
            rectWedge.left += 1
            rectWedge.top += 1
            val shadowSweepExtra = 2
            canvas.drawArc(rectWedge, startangle, sweepangle + shadowSweepExtra, true, paintbox.pieWedgeOutline)
            canvas.drawArc(rectWedge, startangle, sweepangle, true, paintbox.pieWedge)
            val todayAngle: Float = ((GregorianCalendarDefaultLocale().timeInMillis - interval.startDate.timeInMillis).toFloat()
                    / (interval.endDate.timeInMillis - interval.startDate.timeInMillis).toFloat() * 2F * PI.toFloat()) - (PI.toFloat() / 2F)
            val tickEndFudge = 1F
            val tickStartFudge = 0.8F
            canvas.drawLine(
                    pieX() + (pieRadius() * tickStartFudge * cos(todayAngle)),
                    pieY() + (pieRadius() * tickStartFudge * sin(todayAngle)),
                    pieX() + (pieRadius() * tickEndFudge * cos(todayAngle)),
                    pieY() + (pieRadius() * tickEndFudge * sin(todayAngle)),
                    paintbox.pieTick
            )

            return field
        }

    private fun pieRadius() = minOf(width.toFloat(), height.toFloat()).div(2)
    private fun pieX() = width.toFloat().div(2)
    private fun pieY() = height.toFloat().div(2)

    private inner class PaintBox(context: Context) {
        val pieTick = Paint()
        val pieWedge = Paint()
        val pieWedgeOutline = Paint()
        val pieBg = Paint()

        // TODO default colours if the resources aren't present in the theme
        init {
            pieTick.color = context.resources.getColor(R.color.colorPieTick, context.theme)
            pieTick.strokeWidth = 5F
            pieTick.isAntiAlias = true

            pieBg.color = context.resources.getColor(R.color.colorPieBg, context.theme)
            pieBg.isAntiAlias = true

            pieWedge.color = context.resources.getColor(R.color.colorPieWedge, context.theme)
            pieWedge.isAntiAlias = true

            pieWedgeOutline.color = context.resources.getColor(R.color.colorPieWedgeOutline, context.theme)
            pieWedgeOutline.style = Paint.Style.STROKE
            pieWedgeOutline.strokeWidth = 3F
            pieWedgeOutline.isAntiAlias = true
        }
    }
}
