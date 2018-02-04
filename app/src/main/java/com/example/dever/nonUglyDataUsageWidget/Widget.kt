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
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.RemoteViews
import java.util.*


//private const val WIDGET_DATA_KEY = "nudwidgetdatakey"

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

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.hasExtra(WIDGET_IDS_KEY))
            this.onUpdate(context, AppWidgetManager.getInstance(context), intent.extras!!.getIntArray(WIDGET_IDS_KEY))
        else
            super.onReceive(context, intent)
    }

    companion object {

        const val WIDGET_IDS_KEY = "nudwidgetkey"

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

            try {
                chart.drawChart(
                        stats.actualData.toDouble(),
                        stats.maxData.toDouble(),
                        interval
                )
                views.setImageViewBitmap(R.id.widgetChartImageView, chart.bitmap)
                views.setTextViewText(
                        R.id.txtWidgetActualData,
                        context.resources.getString(
                                R.string.widget_data_template,
                                (stats.actualData / 1024 / 1024 / 1024).toFloat()
                        )
                )
                views.setTextViewText(
                        R.id.txtWidgetDays,
                        context.resources.getString(
                                R.string.widget_days_template,
                                GregorianCalendarDefaultLocale().get(Calendar.DAY_OF_MONTH) - interval.startDate.get(Calendar.DAY_OF_MONTH) + 1,
                                interval.endDate.get(Calendar.DAY_OF_MONTH) - interval.startDate.get(Calendar.DAY_OF_MONTH) + 1
                        )
                )

                var clickIntent = PendingIntent.getActivity(context, 0, Intent(context, MainActivity::class.java), 0)
                views.setOnClickPendingIntent(R.id.widgetChartImageView, clickIntent)
                views.setOnClickPendingIntent(R.id.txtWidgetActualData, clickIntent)

                views.setViewVisibility(R.id.widgetChartImageView, VISIBLE)
                views.setViewVisibility(R.id.txtWidgetActualData, VISIBLE)
                views.setViewVisibility(R.id.txtWidgetNoPermMessage, INVISIBLE)
            } catch (e: SecurityException) {
                // don't have permissions, but it'd be rude for the widget to jump straight to the perms activity
                views.setTextViewText(R.id.txtWidgetNoPermMessage, context.getString(R.string.widget_no_perm_message))

                views.setViewVisibility(R.id.widgetChartImageView, INVISIBLE)
                views.setViewVisibility(R.id.txtWidgetActualData, INVISIBLE)
                views.setViewVisibility(R.id.txtWidgetNoPermMessage, VISIBLE)

                views.setOnClickPendingIntent(R.id.txtWidgetNoPermMessage, PendingIntent.getActivity(context, 0, Intent(context, PrePermissionRequestActivity::class.java), 0))
            }
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}

