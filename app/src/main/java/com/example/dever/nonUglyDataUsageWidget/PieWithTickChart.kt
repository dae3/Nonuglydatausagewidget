package com.example.dever.nonUglyDataUsageWidget

import android.graphics.*

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

    fun drawChart(actualData: Double, maxData: Double) {
        canvas.drawColor(Color.TRANSPARENT)
        canvas.drawCircle(pieX(), pieY(), pieRadius(), paintPieBg)
//        canvas.drawLine(pieX(), pieY(), pieX()+pieRadius(), pieY(), paintTick)

        var angle : Float = actualData.toFloat()/maxData.toFloat()*360
//        var angle : Float = (actualData.toFloat()/maxData.toFloat()*360 * PI.toFloat()/180)
//        var dx : Float = (pieRadius() * sin(angle))
//        var dy : Float = (pieRadius() * cos(angle))

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
                angle,
                true,
                paintPieFg
        )

        with (canvas) {
//            drawLine(pieX(), pieY(), pieX(), pieY()-pieRadius(), paintPieFg)
//            drawLine(pieX(), pieY(), pieX() + dx, pieY() + dy, paintPieFg)
        }

        isDirty = false
    }
}