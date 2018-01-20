package com.example.dever.nonUglyDataUsageWidget

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

/**
 * Created by daniel.everett on 17/01/2018.
 */
class PieWithTickChart(private val width: Int, private val height: Int) {

    private var paintPieBg = Paint(Color.RED)
    private var paintPieFg = Paint(Color.MAGENTA)
    private var paintTick = Paint(Color.RED)
    private var paintBg = Paint(Color.TRANSPARENT)

    init {
        if (width == 0 || height == 0) throw IllegalArgumentException("both width and height must be non-zero")
        paintPieBg.color = Color.GREEN
        paintTick.strokeWidth = 2F
    }

    var bitmap: Bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444)
        get() {
            if (isDirty) drawChart(0, 0)
            return field
        }
    private var canvas: Canvas = Canvas(bitmap)
    private var isDirty = true

    private fun pieRadius() = minOf(width.toFloat(), height.toFloat()).div(2)
    private fun pieX() = width.toFloat().div(2)
    private fun pieY() = height.toFloat().div(2)

    fun drawChart(actualData: Long, maxData: Long) {
        canvas.drawColor(Color.RED)
        canvas.drawCircle(pieX(), pieY(), pieRadius(), paintPieBg)
//        canvas.drawLine(pieX(), pieY(), pieX()+pieRadius(), pieY(), paintTick)
//        canvas.drawPaint(Paint(Color.RED))

        isDirty = false
    }
}