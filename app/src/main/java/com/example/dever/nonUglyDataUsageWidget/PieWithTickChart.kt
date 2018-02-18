package com.example.dever.nonUglyDataUsageWidget

import android.content.Context
import android.graphics.*
import java.util.*
import java.util.concurrent.TimeUnit
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
 * Intended for use with R.layout.pie_chart
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
     * Text size in dp for the days TextView, calculated from the pie_chart size
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
        get() {
            val currentDay = GregorianCalendarDefaultLocale().get(Calendar.DAY_OF_MONTH) - interval.startDate.get(Calendar.DAY_OF_MONTH) + 1
            val duration = TimeUnit.DAYS.convert(interval.endDate.timeInMillis - interval.startDate.timeInMillis, TimeUnit.MILLISECONDS)+1
            return context.resources.getString(R.string.widget_days_template, currentDay, duration)
        }

    /**
     * Bitmap representation of data used, as a 'pie' chart
     * @return bitmap object, size set by class constructor width and height parameters
     */
    val bitmap: Bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444)
        get() {
            val maxData = stats.maxData.toDouble()
            if (maxData == 0.0) throw IllegalArgumentException("PieWithTickChart: maxData must be non-zero")

            // parameters
            val canvas = Canvas(field)
            val donutSize = 0.75F
            val rectWedge = RectF(
                    pieX() - pieRadius(),
                    pieY() - pieRadius(),
                    pieX() + pieRadius(),
                    pieY() + pieRadius()
            )
            var sweepangle = min((stats.actualData.toDouble() / maxData * 360).toFloat(), 360F)
            val startangle = 0F - 90
            val todayAngle: Float = ((GregorianCalendarDefaultLocale().timeInMillis - interval.startDate.timeInMillis).toFloat()
                    / (interval.endDate.timeInMillis - interval.startDate.timeInMillis).toFloat() * 2F * PI.toFloat()) - (PI.toFloat() / 2F)
            val tickEndFudge = 1F
            val tickStartFudge = 0.8F

            // background
            canvas.drawColor(Color.TRANSPARENT)

            // Draw the donut and the actual usage wedge as the difference of two paths.
            // Intuitively clipping the canvas with the middle hole then drawing a circle
            // and a wedge makes more sense, but this results in aliasing on the inside edge

            // the donut hole that will clip the circle and the wedge
            val hole = Path()
            hole.addCircle(pieX(), pieY(), pieRadius() * donutSize, Path.Direction.CW)

            // the full circle
            val circle = Path()
            circle.addCircle(pieX(), pieY(), pieRadius(), Path.Direction.CW)

            // clip the centre of the circle and draw
            val donut = Path()
            donut.op(circle, hole, Path.Op.DIFFERENCE)
            canvas.drawPath(donut, paintbox.pieBg)

            // the full wedge, sadly no Path equivalent to canvas.drawArc with auto path close
            //  so draw the whole thing (centre, line to top, arc around, line to centre)
            val wedge = Path()
            wedge.moveTo(pieX(), pieY())
            wedge.lineTo(pieX(), pieRadius())
            wedge.arcTo(rectWedge, startangle, sweepangle)
            wedge.lineTo(pieX(), pieY())

            // clip out the centre and draw
            val segment = Path()
            segment.op(wedge, hole, Path.Op.DIFFERENCE)
            canvas.drawPath(segment, paintbox.pieWedge)

            // today marker
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
            pieBg.isDither = true

            pieWedge.color = context.resources.getColor(R.color.colorPieWedge, context.theme)
            pieWedge.isAntiAlias = true
            pieWedge.strokeWidth = 5F

            pieWedgeOutline.color = context.resources.getColor(R.color.colorPieWedgeOutline, context.theme)
            pieWedgeOutline.style = Paint.Style.STROKE
            pieWedgeOutline.strokeWidth = 3F
            pieWedgeOutline.isAntiAlias = true
        }
    }
}
