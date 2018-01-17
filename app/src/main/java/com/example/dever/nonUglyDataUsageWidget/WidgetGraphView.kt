package com.example.dever.nonUglyDataUsageWidget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View

/**
 * A custom view to draw the chart within the widget
 */

class WidgetGraphView(context : Context, attrs : AttributeSet) : View(context, attrs) {

    private var myWidth : Int = 0
    private var myHeight : Int = 0
    private var myPaint = Paint()

    init {
        myPaint.color = Color.BLUE
    }

    // cf res/values/attrs.xml
    var shiny : Boolean = true
        set(v) {
            invalidate()
        }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        Log.d(this.context.packageName, "onDraw")

        var wf = myWidth.toFloat()

        canvas?.drawCircle(
                myWidth.toFloat().div(2),
                myHeight.toFloat().div(2),
                minOf(myHeight, myWidth).toFloat() /2 ,
                myPaint
                )
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        myWidth = w
        myHeight = h
    }
}