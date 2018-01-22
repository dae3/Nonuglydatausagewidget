package com.example.dever.nonUglyDataUsageWidget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.appwidget.AppWidgetProviderInfo
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.widget.RemoteViews

/**
 * Implementation of App Widget functionality.
 */
class Widget : AppWidgetProvider() {

    private lateinit var prefs : SharedPreferences
    private lateinit var interval : NetworkStatsInterval
    private lateinit var stats : GetNetworkStats

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {

        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        interval = NetworkStatsIntervalFactory.getInterval(context)
        stats = GetNetworkStats(context, interval)

        for (appWidgetId in appWidgetIds) updateAppWidget(context, appWidgetManager, appWidgetId, interval, stats)
    }

    companion object {

        internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager,
                                     appWidgetId: Int, interval: NetworkStatsInterval, stats: GetNetworkStats) {

            val views = RemoteViews(context.packageName, R.layout.widget)

            var info : AppWidgetProviderInfo = appWidgetManager.getAppWidgetInfo(appWidgetId)
            var chart = PieWithTickChart(info.minWidth, info.minHeight)
            chart.drawChart(
                    stats.actualData.toDouble(),
                    stats.maxData.toDouble(),
                    interval
            )
            views.setImageViewBitmap(R.id.widgetChartImageView, chart.bitmap)

            appWidgetManager.updateAppWidget(appWidgetId, views)

        }
    }
}

