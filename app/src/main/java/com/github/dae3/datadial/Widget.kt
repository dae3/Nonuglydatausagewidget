package com.github.dae3.datadial

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetManager.*
import android.appwidget.AppWidgetProvider
import android.appwidget.AppWidgetProviderInfo
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.DisplayMetrics
import android.util.TypedValue
import android.util.TypedValue.*
import android.view.View.*
import android.widget.RemoteViews
import com.github.dae3.datadial.R.id
import com.github.dae3.datadial.R.layout
import kotlin.math.min


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
            val ws = widgetSize(appWidgetManager, appWidgetId, context)
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

            val ws = widgetSize(appWidgetManager, appWidgetId, context)

            updateAppWidget(
                    context,
                    appWidgetManager,
                    appWidgetId,
                    PieWithTickChart(context, ws.first, ws.second, interval, stats)
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
         * @return Pair<widget width,  height> in px
         */
        private fun widgetSize(appWidgetManager: AppWidgetManager, appWidgetId: Int, context: Context): Pair<Int, Int> {
            val options = appWidgetManager.getAppWidgetOptions(appWidgetId)
            val info: AppWidgetProviderInfo = appWidgetManager.getAppWidgetInfo(appWidgetId)
            val scaledDensity = context.resources.displayMetrics.scaledDensity

            return Pair(
                    ((if (options.getInt(OPTION_APPWIDGET_MIN_WIDTH) == 0) info.minWidth else options.getInt(OPTION_APPWIDGET_MIN_WIDTH)) * scaledDensity).toInt(),
                    ((if (options.getInt(OPTION_APPWIDGET_MIN_HEIGHT) == 0) info.minHeight else options.getInt(OPTION_APPWIDGET_MIN_HEIGHT)) * scaledDensity).toInt()
            )
        }

        private val vvNormal = hashMapOf<Int, Int>()
        private val vvNormalSmall = hashMapOf<Int, Int>()
        private val vvError = hashMapOf<Int, Int>()

        init {
            vvNormal[id.widgetChartImageView] = VISIBLE
            vvNormal[id.txtWidgetActualData] = VISIBLE
            vvNormal[id.txtWidgetDays] = VISIBLE
            vvNormal[id.widgetErrorImageView] = INVISIBLE
            vvNormal[id.progressBar] = GONE

            vvNormalSmall[id.widgetChartImageView] = VISIBLE
            vvNormalSmall[id.txtWidgetActualData] = VISIBLE
            vvNormalSmall[id.txtWidgetDays] = GONE
            vvNormalSmall[id.widgetErrorImageView] = INVISIBLE
            vvNormalSmall[id.progressBar] = GONE

            vvError[id.txtWidgetActualData] = INVISIBLE
            vvError[id.txtWidgetActualData] = INVISIBLE
            vvError[id.txtWidgetDays] = INVISIBLE
            vvError[id.widgetErrorImageView] = VISIBLE
            vvNormal[id.progressBar] = GONE
        }

        const val WIDGET_IDS_KEY = "nudwidgetkey"

        @SuppressLint("NewApi")
        internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager,
                                     appWidgetId: Int, pie: PieWithTickChart) {

            val views = RemoteViews(context.packageName, layout.widget)
            lateinit var vv: HashMap<Int, Int>

            try {
                views.setImageViewBitmap(id.widgetChartImageView, pie.bitmap)
                views.setTextViewText(id.txtWidgetActualData, pie.actualDataText)
                views.setTextViewTextSize(id.txtWidgetActualData, COMPLEX_UNIT_SP, pie.actualDataTextSize)
                views.setTextViewText(id.txtWidgetDays, pie.daysText)
                views.setTextViewTextSize(id.txtWidgetDays, TypedValue.COMPLEX_UNIT_SP, pie.daysTextSize)

                val clickIntent = PendingIntent.getActivity(context, 0, Intent(context, MainActivity::class.java), 0)
                views.setOnClickPendingIntent(id.widgetChartImageView, clickIntent)
                views.setOnClickPendingIntent(id.txtWidgetActualData, clickIntent)
                views.setOnClickPendingIntent(id.txtWidgetDays, clickIntent)

                val sizes = widgetSize(appWidgetManager, appWidgetId, context)
                vv = if (pie.isSmall) vvNormalSmall else vvNormal

            } catch (e: SecurityException) {
                // don't have permissions, but it'd be rude for the widget to jump straight to the perms activity
                vv = vvError

                views.setOnClickPendingIntent(id.widgetErrorImageView, PendingIntent.getActivity(context, 0, Intent(context, PrePermissionRequestActivity::class.java), 0))
            }
            vv.forEach({ k, v -> views.setViewVisibility(k, v) })
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}

