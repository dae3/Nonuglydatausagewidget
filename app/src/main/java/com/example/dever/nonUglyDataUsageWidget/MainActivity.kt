package com.example.dever.nonUglyDataUsageWidget

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import java.text.NumberFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var txtDataUsed: TextView
    private lateinit var txtInterval: TextView
    private var nf = NumberFormat.getNumberInstance()
//    private lateinit var stateInterval: NetworkStatsInterval
    private lateinit var prefs : SharedPreferences
    private lateinit var stats : GetNetworkStats
//    private var myResources = getResources()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        txtDataUsed = findViewById(R.id.txtDataUsed)
        txtInterval = findViewById(R.id.txtInterval)
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        stats = GetNetworkStats(this)
    }

    override fun onResume() {
        super.onResume()

        // test permissions first - can't do much without these
        if (!(PermissionChecker.haveUsagePermission(this) || !PermissionChecker.havePhoneStatePermission(this)))
            startActivity(Intent(this, PrePermissionRequestActivity::class.java))
        else {
            var stateInterval = DayNOfMonthNetworkStatsInterval(
                    today = GregorianCalendar(),
                    dayOfMonth = prefs.getString(
                            resources.getString(R.string.prefs_key_billingcycle_startday), // TODO should these be R.string or R.id?
                            "1"
                    ).toInt()
            )

            txtDataUsed.text = "${nf.format(stats.getNetworkStats(stateInterval).toFloat() / 1024 / 1024)} MB"
            txtInterval.text = "$stateInterval"
        }
    }

    fun onButtonClick(@Suppress("UNUSED_PARAMETER") v: View) {
        startActivity(Intent(this, PreferencesActivity::class.java))
    }

}
