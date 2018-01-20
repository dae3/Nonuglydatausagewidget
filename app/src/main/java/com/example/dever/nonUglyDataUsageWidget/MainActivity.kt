package com.example.dever.nonUglyDataUsageWidget

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
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
//    private var myResources = getResources()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        txtDataUsed = findViewById(R.id.txtDataUsed)
        txtInterval = findViewById(R.id.txtInterval)
        prefs = getSharedPreferences(this.packageName, MODE_PRIVATE)
    }

    @SuppressLint("ResourceType")
    override fun onResume() {
        super.onResume()

        // test permissions first - can't do much without these
        if (!(PermissionChecker.haveUsagePermission(this) || !PermissionChecker.havePhoneStatePermission(this)))
            startActivity(Intent(this, PrePermissionRequestActivity::class.java))
        else {
            val dayOfMonth = prefs.getInt(
                    resources.getString(R.string.prefs_key_billingcycle_startday), // TODO should these be R.string or R.id?
                    1
            )
            Log.d(this.packageName, "dom is $dayOfMonth")
            var stateInterval = DayNOfMonthNetworkStatsInterval(
                    today = GregorianCalendar(),
                    dayOfMonth = dayOfMonth
            )

            txtDataUsed.text = "${nf.format(GetNetworkStats(stateInterval, this).getNetworkStats().toFloat() / 1024 / 1024)} MB"
            txtInterval.text = "$stateInterval"
        }
    }

    fun onButtonClick(@Suppress("UNUSED_PARAMETER") v: View) {
        startActivity(Intent(this, PreferencesActivity::class.java))
    }

}
