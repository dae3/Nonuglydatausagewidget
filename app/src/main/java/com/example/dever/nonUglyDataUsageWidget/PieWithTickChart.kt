package com.example.dever.nonUglyDataUsageWidget

import android.content.Context
import android.graphics.*
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
    // 0.88 chosen experimentally in conjunction with shadow radius to avoid widget bottom shadow
    //  being clipped
    private val availsize: Float = min(width, height) * .86F

    private val isSmall : Boolean = (availsize < context.resources.getInteger(R.integer.widget_small_threshold))

    /**
     * The text to display for actual data used
     * @return formatted text (String) using R.string.widget_data_template
     */
    val actualDataText: String
        get() = context.resources.getString(
                if (isSmall) R.string.widget_data_template_small else R.string.widget_data_template,
                stats.actualData.toFloat() / 1024F / 1024F / 1024F
        )

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
        get() = availsize * if (isSmall) 0.6F else 0.4F

    /**
     * Text size in dp for the days TextView, calculated from the pie_chart size
     * @see actualDataTextSize
     *  @return font size in dp
     */
    val daysTextSize: Float
        get() = availsize * 0.225F

    /**
     * The text to display for the 'day N of M' TextView
     * @return formatted string using R.string.widget_days_template
     */
    val daysText: String
        get() {
            val currentDay = TimeUnit.DAYS.convert(GregorianCalendarDefaultLocale().timeInMillis - interval.startDate.timeInMillis, TimeUnit.MILLISECONDS) + 1
            val duration = TimeUnit.DAYS.convert(interval.endDate.timeInMillis - interval.startDate.timeInMillis, TimeUnit.MILLISECONDS) + 1
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
            val pieX = width.toFloat().div(2)
            val pieY = height.toFloat().div(2)

            val pieRadius = availsize/2F

            val canvas = Canvas(field)
            val donutSize = 0.75F  // aesthetic
            val rectWedge = RectF(pieX - pieRadius, pieY - pieRadius, pieX + pieRadius, pieY + pieRadius)
            val sweepAngle = Angle(
                    min(
                            (stats.actualData.toDouble() / maxData * 360).toFloat(),
                            359F // 360 overlaps and looks like 0
                    )
            )
            val startAngle = Angle(0F)
            val todayAngle = Angle((GregorianCalendarDefaultLocale().timeInMillis - interval.startDate.timeInMillis).toFloat()
                    / (interval.endDate.timeInMillis - interval.startDate.timeInMillis).toFloat() * 360F)
            val todayRadiusFudge = 0.8F  // aesthetic

            // background
            canvas.drawColor(Color.TRANSPARENT)

            // Draw the donut and the actual usage wedge as the difference of two paths.
            //  Intuitively clipping the canvas with the middle hole then drawing a circle
            //  and a wedge makes more sense, but this results in aliasing on the inside edge
            //  so we generate the clipped shape as a single Path then draw it on the Canvas

            // the donut hole that will clip the circle and the wedge
            val hole = Path()
            hole.addCircle(pieX, pieY, pieRadius * donutSize, Path.Direction.CW)

            // the full circle
            val circle = Path()
            circle.addCircle(pieX, pieY, pieRadius, Path.Direction.CW)

            // clip the centre of the circle and draw
            val donut = Path()
            donut.op(circle, hole, Path.Op.DIFFERENCE)
            canvas.drawPathWithShadow(
                    donut,
                    paintbox.pieBg,
                    R.color.widget_shadow,
                    context.resources.getInteger(R.integer.widget_elevation_circle)
            )

            // the full wedge, sadly no Path equivalent to canvas.drawArc with auto path close
            //  so draw the whole thing (move centre, line to top, arc around, line to centre)
            val wedge = Path()
            wedge.moveTo(pieX, pieY)
            wedge.lineTo(pieX, pieRadius)
            wedge.arcTo(rectWedge, startAngle, sweepAngle)
            wedge.lineTo(pieX, pieY)

            // circular ends are pretty
            // donut width = 1-donutsize * pieRadius; circle end-cap radius is 0.5 * that
            val capRadius = pieRadius * (1 - donutSize) / 2
            val startCapXY = CircleCoords(pieX, pieY, pieRadius - capRadius, Angle(0F))
            val endCapXY = startCapXY.copy(angle = Angle(sweepAngle.inDegrees))
            wedge.addCircle(startCapXY, capRadius)
            wedge.addCircle(endCapXY, capRadius)

            // clip out the centre and draw
            val segment = Path()
            segment.op(wedge, hole, Path.Op.DIFFERENCE)
            canvas.drawPathWithShadow(
                    segment,
                    paintbox.pieWedge,
                    R.color.widget_shadow,
                    context.resources.getInteger(R.integer.widget_elevation_wedge)
            )

            // today marker
            val todayCircle = Path()
            todayCircle.addCircle(CircleCoords(pieX, pieY, pieRadius - capRadius, todayAngle), capRadius * todayRadiusFudge)
            canvas.drawPathWithShadow(
                    todayCircle,
                    paintbox.pieTick,
                    R.color.widget_shadow,
                    context.resources.getInteger(R.integer.widget_elevation_todaymarker)
            )

            return field
        }



    private inner class PaintBox(context: Context) {
        val pieTick = Paint()
        val pieWedge = Paint()
        val pieBg = Paint()
        val shadow = Paint()

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

            shadow.color = context.resources.getColor(R.color.colorPieShadow, context.theme)
            shadow.strokeWidth = 5F
            shadow.isAntiAlias = true
            shadow.alpha = 50
        }
    }

    /**
     * Representation of an angle
     *
     * @param inDegrees  the angle, measured in degrees
     */
    data class Angle(var inDegrees: Float = 0F) {

        private val d2r: Float = 2F * PI.toFloat() / 360F

        /**
         * @return  this angle, measured in radians
         */
        var inRadians
            get() = inDegrees * d2r
            set(v) {
                inDegrees = v / d2r
            }
    }

    /**
     * Representation of a coordinate on the radius of a circle
     *
     * @param originX  the x-coordinate of the centre of the circle
     * @param originY  the y-coordinate of the centre of the circle
     * @param offset   this coordinate's distance from the origin, along both axes
     * @param angle   this coordinate's rotation from the radial origin at 12 o'clock
     */
    data class CircleCoords(
            private var originX: Float,
            private var originY: Float,
            private var offset: Float,
            private var angle: PieWithTickChart.Angle
    ) {

        val x: Float
            get() = originX + (offset * cos(angle.inRadians - PI.toFloat() / 2F))
        val y: Float
            get() = originY + (offset * sin(angle.inRadians - PI.toFloat() / 2F))
    }

    /**
     * Some extensions to allow passing CircleCoords and Angle objects to Path and Canvas methods
     */
    fun Path.addCircle(coords: PieWithTickChart.CircleCoords, radius: Float) = this.addCircle(coords.x, coords.y, radius, android.graphics.Path.Direction.CW)
    fun Canvas.drawCircle(coords: PieWithTickChart.CircleCoords, radius: Float, paint: Paint) = this.drawCircle(coords.x, coords.y, radius, paint)
    fun Path.arcTo(rect: RectF, startangle: Angle, sweepangle: Angle) = this.arcTo(rect, startangle.inDegrees - 90F, sweepangle.inDegrees)

    /**
     * Draw a Path on a Canvas using the provided Paint
     * Create Material-ish ambient shadow and key shadow for specified elevation in dp
     * @param path the Path to draw
     * @param canvas the Canvas to draw it on
     * @param basePaint Paint to draw the object. Set color, stroke, etc. on this
     * @param shadowColor the resource ID for the shadow's colour. Should usually be black
     * @param elevation the object's elevation in dp
     */
    private fun Canvas.drawPathWithShadow(path : Path, basePaint: Paint, shadowColor: Int, elevation : Int) {

        // TODO align these shadow parameters with those in the base class

        val baseAmbientRadius = 1F
        val baseKeyRadius = 2F
        val baseKeyOffset = 1F
        val elevationScaleFactor = 0.5F

        val aPaint = Paint(basePaint)
        aPaint.setShadowLayer(baseAmbientRadius * elevation * elevationScaleFactor, 0F, 0F, shadowColor)
        this.drawPath(path, aPaint)

        val kPaint = Paint(basePaint)
        kPaint.setShadowLayer(baseKeyRadius * elevation * elevationScaleFactor, 0F, baseKeyOffset * elevation, shadowColor)
        this.drawPath(path, kPaint)
    }
}
