package com.example.dever.nonUglyDataUsageWidget

import android.annotation.SuppressLint
import android.app.Fragment
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.Snackbar
import android.support.design.widget.Snackbar.LENGTH_INDEFINITE
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import java.util.*

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private lateinit var prefs: SharedPreferences
    private lateinit var stats: GetNetworkStats
    private lateinit var interval: NetworkStatsInterval
    private var fragment: Fragment? = PieChartFragment()
    private val perm = PermissionManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<BottomNavigationView>(R.id.bottom_navbar).setOnNavigationItemSelectedListener(this)

        prefs = PreferenceManager.getDefaultSharedPreferences(this)
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()

        val sb = Snackbar.make(findViewById(R.id.main_coord_layout), getString(R.string.main_snackbar_nopermission), LENGTH_INDEFINITE).setAction(R.string.snackbar_noperm_action, this)

        if (!perm.havePhoneStatePermission || !perm.haveUsagePermission)
            sb.show()
        else {
            try {
                interval = DayNOfMonthNetworkStatsInterval(
                        today = GregorianCalendar(Locale.getDefault()),
                        dayOfMonth = prefs.getInt(resources.getString(R.string.prefs_key_billingcycle_startday), 1)
                )
                stats = GetNetworkStats(this, interval)
                fragmentManager.beginTransaction().replace(R.id.main_linear_body, fragment, null).commit()

            } catch (e: SecurityException) {
                sb.show()
            }
        }

    }

    /**
     * onClick handler for snackbar "Fix" button - launches PrePermissionCheckActivity
     */
    override fun onClick(v: View?) =
            startActivity(Intent(this, PrePermissionRequestActivity::class.java), null)

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        fragment = when (item.itemId) {
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
