package com.example.dever.nonUglyDataUsageWidget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT
import android.appwidget.AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH
import android.appwidget.AppWidgetProvider
import android.appwidget.AppWidgetProviderInfo
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.RemoteViews

/**
 * Implementation of App Widget functionality.
 */
class Widget : AppWidgetProvider() {

    private lateinit var prefs: SharedPreferences
    private lateinit var interval: NetworkStatsInterval
    private lateinit var stats: GetNetworkStats

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {

        setProps(context)

        for (appWidgetId in appWidgetIds) updateAppWidget(context, appWidgetManager, appWidgetId, interval, stats)
    }

    private fun setProps(context: Context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        interval = NetworkStatsIntervalFactory.getInterval(context)
        stats = GetNetworkStats(context, interval)
    }

    override fun onAppWidgetOptionsChanged(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetId: Int, newOptions: Bundle?) {
        setProps(context!!)
        updateAppWidget(context!!, appWidgetManager!!, appWidgetId, interval, stats)
    }

    companion object {

        internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager,
                                     appWidgetId: Int, interval: NetworkStatsInterval, stats: GetNetworkStats) {

            val views = RemoteViews(context.packageName, R.layout.widget)
            val options = appWidgetManager.getAppWidgetOptions(appWidgetId)

            var info: AppWidgetProviderInfo = appWidgetManager.getAppWidgetInfo(appWidgetId)
            var chart = PieWithTickChart(
                    if (options.getInt(OPTION_APPWIDGET_MAX_WIDTH) == 0) info.minWidth else options.getInt(OPTION_APPWIDGET_MAX_WIDTH),
                    if (options.getInt(OPTION_APPWIDGET_MAX_HEIGHT) == 0) info.minHeight else options.getInt(OPTION_APPWIDGET_MAX_HEIGHT),
                    context
            )
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

