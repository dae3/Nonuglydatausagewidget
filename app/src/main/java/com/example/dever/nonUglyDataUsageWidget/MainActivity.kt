package com.example.dever.nonUglyDataUsageWidget

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import java.text.NumberFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var txtDataUsed: TextView
    private lateinit var txtInterval: TextView
    private lateinit var i: ImageView
    private var nf = NumberFormat.getNumberInstance()
    //    private lateinit var stateInterval: NetworkStatsInterval
    private lateinit var prefs: SharedPreferences
    private lateinit var stats: GetNetworkStats
    private lateinit var chart: PieWithTickChart
//    private var myResources = getResources()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        txtDataUsed = findViewById(R.id.txtDataUsed)
        txtInterval = findViewById(R.id.txtInterval)
        i = findViewById(R.id.imageView2)
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
//        stats = GetNetworkStats(this, )
        chart = PieWithTickChart(100, 100)
    }

    override fun onResume() {
        super.onResume()

        // test permissions first - can't do much without these
        if (!(PermissionChecker.haveUsagePermission(this) || !PermissionChecker.havePhoneStatePermission(this)))
            startActivity(Intent(this, PrePermissionRequestActivity::class.java))
        else {
            try {
                var statsInterval = DayNOfMonthNetworkStatsInterval(
                        today = GregorianCalendar(),
                        dayOfMonth = prefs.getInt(resources.getString(R.string.prefs_key_billingcycle_startday), 1)
                )
                stats = GetNetworkStats(this, statsInterval)

                txtDataUsed.text = "${nf.format(stats.actualData.toFloat() / 1024 / 1024)} MB of ${nf.format(stats.maxData.toFloat())} MB"
                txtInterval.text = "$statsInterval"

                chart.drawChart(stats.actualData.toDouble(), stats.maxData.toDouble(), statsInterval)
                i.setImageBitmap(chart.bitmap)
            } catch (e: SecurityException) {
                startActivity(Intent(this, PrePermissionRequestActivity::class.java))
            }
        }
    }

    fun onButtonClick(@Suppress("UNUSED_PARAMETER") v: View) {
        startActivity(Intent(this, PreferencesActivity::class.java))
    }

}
