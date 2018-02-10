package com.example.dever.nonUglyDataUsageWidget

import android.annotation.SuppressLint
import android.app.Fragment
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.ImageView
import android.widget.LinearLayout
import java.util.*

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private lateinit var prefs: SharedPreferences
    private lateinit var stats: GetNetworkStats
    private lateinit var interval : NetworkStatsInterval
    private var fragment : Fragment? = PieChartFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<BottomNavigationView>(R.id.bottom_navbar).setOnNavigationItemSelectedListener(this)

        prefs = PreferenceManager.getDefaultSharedPreferences(this)
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()

        // test permissions first - can't do much without these
        if (!(PermissionChecker.haveUsagePermission(this) || !PermissionChecker.havePhoneStatePermission(this)))
            startActivity(Intent(this, PrePermissionRequestActivity::class.java))
        else {
            try {
                interval = DayNOfMonthNetworkStatsInterval(
                        today = GregorianCalendar(Locale.getDefault()),
                        dayOfMonth = prefs.getInt(resources.getString(R.string.prefs_key_billingcycle_startday), 1)
                )
                stats = GetNetworkStats(this, interval)

            } catch (e: SecurityException) {
                startActivity(Intent(this, PrePermissionRequestActivity::class.java))
            }
        }

        fragmentManager.beginTransaction().replace(R.id.main_linear_body, fragment, null).commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        fragment = when(item.itemId) {
            R.id.bottomnav_home -> PieChartFragment()
            R.id.bottomnav_settings -> SettingsFragment()
            R.id.bottomnav_about -> AboutFragment()
            else -> null
        }

        return if (fragment == null)
            false
        else {
            fragmentManager.beginTransaction().replace(R.id.main_linear_body, fragment, null).commit()
            true
        }
    }
}
