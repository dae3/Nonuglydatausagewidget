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
        val rectWedge = RectF(
                pieX() - pieRadius(),
                pieY() - pieRadius(),
                pieX() + pieRadius(),
                pieY() + pieRadius()
        )
        val angle = (actualData / maxData * 360).toFloat()
        val startangle = 0F - 90

        // wedge outline
        rectWedge.left += 1
        rectWedge.top += 1
//        rectWedge.right += 1
//        rectWedge.bottom += 1

        canvas.drawArc(rectWedge, startangle, angle + 2, true, paintbox.pieWedgeOutline)

        // wedge body
        canvas.drawArc(rectWedge, startangle, angle, true, paintbox.pieWedge)

        // today vs total period tick
        var todayAngle = ((GregorianCalendar().timeInMillis - interval.startDate.timeInMillis).toFloat() / (interval.endDate.timeInMillis - interval.startDate.timeInMillis).toFloat() * 360.0 * PI / 180.0).toFloat()
        val tickEndFudge = 1.1
        val tickStartFudge = 0.8
        canvas.drawLine(
                (pieX() + pieRadius() * sin(todayAngle) * tickStartFudge).toFloat(),
                (pieY() + pieRadius() * cos(todayAngle) * tickStartFudge).toFloat(),
                pieX() + (pieRadius() * tickEndFudge * sin(todayAngle)).toFloat(),
                pieY() + (pieRadius() * tickEndFudge * cos(todayAngle)).toFloat(),
                paintbox.pieTick
        )

        isDirty = false
    }

    private inner class PaintBox(context: Context) {
        val pieTick = Paint()
        val pieWedge = Paint()
        val pieWedgeOutline = Paint()
        val pieBg = Paint()

        init {
            pieTick.color = context.resources.getColor(R.color.colorAccent)
            pieTick.strokeWidth = 3F
            pieBg.color = context.resources.getColor(R.color.colorPrimary)
            pieWedge.color = context.resources.getColor(R.color.colorPrimaryDark)
            pieWedgeOutline.color = Color.DKGRAY
            pieWedgeOutline.style = Paint.Style.STROKE
            pieWedgeOutline.strokeWidth = 3F
        }
    }
}
