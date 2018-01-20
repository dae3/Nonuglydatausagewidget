package com.example.dever.nonUglyDataUsageWidget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.appwidget.AppWidgetProviderInfo
import android.content.Context
import android.graphics.Paint
import android.widget.RemoteViews

/**
 * Implementation of App Widget functionality.
 */
class NewAppWidget : AppWidgetProvider() {

    private var myWidth : Int = 0
    private var myHeight : Int = 0
    private var myPaint = Paint()

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    companion object {

        internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager,
                                     appWidgetId: Int) {

            val widgetText = context.getString(R.string.appwidget_text)
            // Construct the RemoteViews object
            val views = RemoteViews(context.packageName, R.layout.new_app_widget)
            views.setTextViewText(R.id.appwidget_text, widgetText)

            // construct a bitmap
            var info : AppWidgetProviderInfo = appWidgetManager.getAppWidgetInfo(appWidgetId)
            var chart = PieWithTickChart(info.minWidth, info.minHeight)

            views.setImageViewBitmap(R.id.imageView, chart.bitmap)
//            views.setImageViewBitmap(R.id.imageView, Bitmap.createBitmap(info.minWidth, info.minHeight, Bitmap.Config.ARGB_4444))

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)

        }
    }
}

