package com.example.dever.nonUglyDataUsageWidget

import android.graphics.*
import java.util.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class PieWithTickChart(private val width: Int, private val height: Int) {

    private var paintPieBg = Paint()
    private var paintPieFg = Paint()
    private var paintTick = Paint()
    private var paintBg = Paint()

    init {
        if (width == 0 || height == 0) throw IllegalArgumentException("both width and height must be non-zero")
        paintPieBg.color = Color.LTGRAY
        paintPieFg.color = Color.BLUE
        paintTick.color = Color.RED
        paintTick.strokeWidth = 2F
    }

    var bitmap: Bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444)
    private var canvas: Canvas = Canvas(bitmap)
    private var isDirty = true

    private fun pieRadius() = minOf(width.toFloat(), height.toFloat()).div(2)
    private fun pieX() = width.toFloat().div(2)
    private fun pieY() = height.toFloat().div(2)

    // actual and max in bytes
    fun drawChart(actualData: Double, maxData: Double, interval : NetworkStatsInterval) {
        // background
        canvas.drawColor(Color.TRANSPARENT)

        // pie circle
        canvas.drawCircle(pieX(), pieY(), pieRadius(), paintPieBg)

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
                0F-90,
                (actualData/maxData*360).toFloat(),
                true,
                paintPieFg
        )

        // today vs total period tick
        var todayAngle = ((GregorianCalendar().timeInMillis - interval.startDate.timeInMillis).toFloat() / (interval.endDate.timeInMillis - interval.startDate.timeInMillis).toFloat() * 360.0 * PI/180.0).toFloat()
        canvas.drawLine(
                pieX(),
                pieY(),
                pieX() + (pieRadius() * sin(todayAngle)).toFloat(),
                pieY() + (pieRadius() * cos(todayAngle)).toFloat(),
                paintTick
        )

        isDirty = false
    }
}