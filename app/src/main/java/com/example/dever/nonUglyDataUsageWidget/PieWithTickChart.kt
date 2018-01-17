package com.example.dever.nonUglyDataUsageWidget

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

/**
 * Created by daniel.everett on 17/01/2018.
 */
class PieWithTickChart(val stats : NetworkStatsInterval, val width : Int, val height : Int) {

    private var paintPieBg = Paint(Color.DKGRAY)
    private var paintPieFg = Paint(Color.MAGENTA)
    private var paintTick = Paint(Color.RED)
    private var paintBg = Paint(Color.TRANSPARENT)

    var bitmap : Bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444)
        get() {
            canvas?.drawColor(Color.TRANSPARENT)
            canvas?.drawCircle(pieX(), pieY(), pieRadius(), paintPieBg)
            return field
        }

    private var canvas : Canvas? = Canvas(bitmap)

    private fun pieRadius() = minOf(width.toFloat(), height.toFloat()).div(2)
    private fun pieX() = width.toFloat().div(2)
    private fun pieY() = height.toFloat().div(2)

    fun placeholder() {
        /*val b = Bitmap.createBitmap(info.minWidth, info.minHeight, Bitmap.Config.ARGB_4444)
        var canvas = Canvas(b)
        canvas.drawColor(Color.GREEN)
        canvas.drawCircle(
                info.minWidth.toFloat().div(2),
                info.minHeight.toFloat().div(2),
                minOf(info.minHeight.toFloat(), info.minWidth.toFloat()).div(2),
                Paint(Color.RED)
        )*/
    }

}