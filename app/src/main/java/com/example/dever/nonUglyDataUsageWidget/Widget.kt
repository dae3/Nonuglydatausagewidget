package com.example.dever.nonUglyDataUsageWidget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.appwidget.AppWidgetProviderInfo
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.widget.RemoteViews
import java.util.*

/**
 * Implementation of App Widget functionality.
 */
class Widget : AppWidgetProvider() {

    private lateinit var prefs : SharedPreferences

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {

        prefs = PreferenceManager.getDefaultSharedPreferences(context)

        for (appWidgetId in appWidgetIds) updateAppWidget(context, appWidgetManager, appWidgetId)
    }

    companion object {

        internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager,
                                     appWidgetId: Int) {

            val views = RemoteViews(context.packageName, R.layout.widget)

            // construct a bitmap
            var info : AppWidgetProviderInfo = appWidgetManager.getAppWidgetInfo(appWidgetId)
            var chart = PieWithTickChart(info.minWidth, info.minHeight)
            chart.drawChart(60.0,100.0, DayNOfMonthNetworkStatsInterval(GregorianCalendar(), 1))

            views.setImageViewBitmap(R.id.widgetChartImageView, chart.bitmap)
//            views.setImageViewBitmap(R.id.widgetChartImageView, Bitmap.createBitmap(info.minWidth, info.minHeight, Bitmap.Config.ARGB_4444))

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)

        }
    }
}

