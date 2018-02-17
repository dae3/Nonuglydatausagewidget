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
import android.util.TypedValue
import android.util.TypedValue.COMPLEX_UNIT_PX
import android.view.View.*
import android.widget.RemoteViews


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

        for (appWidgetId in appWidgetIds) {
            val ws = widgetSize(appWidgetManager, appWidgetId)
            updateAppWidget(context, appWidgetManager, appWidgetId, PieWithTickChart(context, ws.first, ws.second, interval, stats))
        }
    }

    /**
     * Set various properties that require a Context and therefore can't be initialized in the constructor
     * @param context  Context from the enclosing widget
     */
    private fun setProps(context: Context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        interval = NetworkStatsIntervalFactory.getInterval(context)
        stats = GetNetworkStats(context, interval)
    }

    /**
     * Redraw the AppWidget after a resize
     */
    override fun onAppWidgetOptionsChanged(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetId: Int, newOptions: Bundle?) {
        if (context != null && appWidgetManager != null && newOptions != null) {

            setProps(context)

            updateAppWidget(
                    context,
                    appWidgetManager,
                    appWidgetId,
                    PieWithTickChart(context, newOptions.getInt(OPTION_APPWIDGET_MAX_WIDTH), newOptions.getInt(OPTION_APPWIDGET_MAX_HEIGHT), interval, stats)
            )
        }
    }

    /**
     * Trigger a widget update outside the normal widget host update frequency
     * Used by PrePermissionRequestActivity to toggle widget between normal and permission warning
     * when permissions are changed
     */
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.hasExtra(WIDGET_IDS_KEY))
            this.onUpdate(context, AppWidgetManager.getInstance(context), intent.extras!!.getIntArray(WIDGET_IDS_KEY))
        else
            super.onReceive(context, intent)
    }

    /**
     * static methods
     *
     * updateAppWidget as required by Android
     * widgetSize so it can be called by updateAppWidget
     */
    companion object {

        /**
         * Returns the widget dimensions, calculated from appWidgetManager.getAppWidgetOptions if non-zero
         * or from appWidgetManager.getAppWidgetInfo otherwise
         * @param appWidgetManager the AppWidgetManager instance that owns this widget
         * @param appWidgetId the unique ID of this widget within the AppWidgetManager instance
         * @return widget width and height (in that order) as a Pair<Int, Int>
         */
        private fun widgetSize(appWidgetManager: AppWidgetManager, appWidgetId: Int): Pair<Int, Int> {
            val options = appWidgetManager.getAppWidgetOptions(appWidgetId)
            val info: AppWidgetProviderInfo = appWidgetManager.getAppWidgetInfo(appWidgetId)

            return Pair(
                    if (options.getInt(OPTION_APPWIDGET_MAX_WIDTH) == 0) info.minWidth else options.getInt(OPTION_APPWIDGET_MAX_WIDTH),
                    if (options.getInt(OPTION_APPWIDGET_MAX_HEIGHT) == 0) info.minHeight else options.getInt(OPTION_APPWIDGET_MAX_HEIGHT)
            )
        }

        const val WIDGET_IDS_KEY = "nudwidgetkey"

        internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager,
                                     appWidgetId: Int, pie: PieWithTickChart) {

            val views = RemoteViews(context.packageName, R.layout.pie_chart)
            views.setViewVisibility(R.id.progressBar, GONE)

            try {
                views.setImageViewBitmap(R.id.widgetChartImageView, pie.bitmap)
                views.setTextViewText(R.id.txtWidgetActualData, pie.actualDataText)
                views.setTextViewTextSize(R.id.txtWidgetActualData, COMPLEX_UNIT_PX, pie.actualDataTextSize)
                views.setTextViewText(R.id.txtWidgetDays, pie.daysText)
                views.setTextViewTextSize(R.id.txtWidgetDays, TypedValue.COMPLEX_UNIT_PX, pie.daysTextSize)

                val clickIntent = PendingIntent.getActivity(context, 0, Intent(context, MainActivity::class.java), 0)
                views.setOnClickPendingIntent(R.id.widgetChartImageView, clickIntent)
                views.setOnClickPendingIntent(R.id.txtWidgetActualData, clickIntent)
                views.setOnClickPendingIntent(R.id.txtWidgetDays, clickIntent)

                views.setViewVisibility(R.id.widgetChartImageView, VISIBLE)
                views.setViewVisibility(R.id.txtWidgetActualData, VISIBLE)
                views.setViewVisibility(R.id.txtWidgetDays, VISIBLE)
                views.setViewVisibility(R.id.widgetErrorImageView, INVISIBLE)
            } catch (e: SecurityException) {
                // don't have permissions, but it'd be rude for the widget to jump straight to the perms activity
                views.setViewVisibility(R.id.widgetChartImageView, INVISIBLE)
                views.setViewVisibility(R.id.txtWidgetActualData, INVISIBLE)
                views.setViewVisibility(R.id.txtWidgetDays, INVISIBLE)
                views.setViewVisibility(R.id.widgetErrorImageView, VISIBLE)

                views.setOnClickPendingIntent(R.id.widgetErrorImageView, PendingIntent.getActivity(context, 0, Intent(context, PrePermissionRequestActivity::class.java), 0))
            }
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}

