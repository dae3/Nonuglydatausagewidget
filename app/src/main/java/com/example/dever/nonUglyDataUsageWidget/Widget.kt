package com.example.dever.nonUglyDataUsageWidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT
import android.appwidget.AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH
import android.appwidget.AppWidgetProvider
import android.appwidget.AppWidgetProviderInfo
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.RemoteViews
import java.text.NumberFormat

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
            var clickIntent : PendingIntent

            var info: AppWidgetProviderInfo = appWidgetManager.getAppWidgetInfo(appWidgetId)

            try {
                val actualData = stats.actualData.toDouble()

                var chart = PieWithTickChart(
                        if (options.getInt(OPTION_APPWIDGET_MAX_WIDTH) == 0) info.minWidth else options.getInt(OPTION_APPWIDGET_MAX_WIDTH),
                        if (options.getInt(OPTION_APPWIDGET_MAX_HEIGHT) == 0) info.minHeight else options.getInt(OPTION_APPWIDGET_MAX_HEIGHT),
                        context
                )
                chart.drawChart(
                        actualData,
                        stats.maxData.toDouble(),
                        interval
                )
                views.setImageViewBitmap(R.id.widgetChartImageView, chart.bitmap)
                views.setTextViewText(R.id.txtWidgetActualData, NumberFormat.getInstance().format(stats.actualData / 1024 / 1024 / 1024))
                clickIntent = PendingIntent.getActivity(context, 0, Intent(context, MainActivity::class.java), 0)
            } catch (e: SecurityException) {
                // don't have permissions, but it'd be rude for the widget to jump straight to the perms activity
                views.setTextViewText(R.id.txtWidgetActualData, "999")
                clickIntent = PendingIntent.getActivity(context, 0, Intent(context, PrePermissionRequestActivity::class.java), 0)
            }

            views.setOnClickPendingIntent(R.id.widgetChartImageView, clickIntent)
            views.setOnClickPendingIntent(R.id.txtWidgetActualData, clickIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}

