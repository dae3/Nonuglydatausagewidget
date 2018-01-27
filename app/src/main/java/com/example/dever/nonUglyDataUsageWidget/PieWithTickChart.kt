package com.example.dever.nonUglyDataUsageWidget

import android.content.Context
import android.graphics.*
import java.util.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class PieWithTickChart(private val width: Int, private val height: Int, val context: Context) {

    init {
        if (width == 0 || height == 0) throw IllegalArgumentException("both width and height must be non-zero")
    }

    private val paintbox = PaintBox(context)
    var bitmap: Bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444)
    private var canvas: Canvas = Canvas(bitmap)
    private var isDirty = true

    private fun pieRadius() = minOf(width.toFloat(), height.toFloat()).div(2)
    private fun pieX() = width.toFloat().div(2)
    private fun pieY() = height.toFloat().div(2)

    // actual and max in bytes
    fun drawChart(actualData: Double, maxData: Double, interval: NetworkStatsInterval) {
        // background
        canvas.drawColor(Color.TRANSPARENT)

        // pie circle
        canvas.drawCircle(pieX(), pieY(), pieRadius(), paintbox.pieBg)

        // actual usage circle segment
        // useful reference for drawArc at https://robots.thoughtbot.com/android-canvas-drawarc-method-a-visual-guide
        //  basically start angle is left edge (with origin at 3 o'clock and sweep is delta from that
        canvas.drawArc(
                RectF(
                        pieX() - pieRadius(),
                        pieY() - pieRadius(),
                        pieX() + pieRadius(),
                        pieY() + pieRadius()
                ),
                0F - 90,
                (actualData / maxData * 360).toFloat(),
                true,
                paintbox.pieWedge
        )

        // today vs total period tick
        var todayAngle = ((GregorianCalendar().timeInMillis - interval.startDate.timeInMillis).toFloat() / (interval.endDate.timeInMillis - interval.startDate.timeInMillis).toFloat() * 360.0 * PI / 180.0).toFloat()
        canvas.drawLine(
                pieX(),
                pieY(),
                pieX() + (pieRadius() * sin(todayAngle)).toFloat(),
                pieY() + (pieRadius() * cos(todayAngle)).toFloat(),
                paintbox.pieTick
        )

        isDirty = false
    }

    // can't apply style directly because this isn't a View
    //  https://stackoverflow.com/questions/13719103/how-to-retrieve-style-attributes-programmatically-from-styles-xml
    inner class PaintBox(val context: Context) {
        val pieTick = Paint()
        val pieWedge = Paint()
        val pieBg = Paint()

        init {
            pieTick.color = context.resources.getColor(R.color.colorAccent)
            pieTick.strokeWidth = 2F
            pieBg.color = context.resources.getColor(R.color.colorPrimary)
            pieWedge.color = context.resources.getColor(R.color.colorPrimaryDark)
        }
    }
}
