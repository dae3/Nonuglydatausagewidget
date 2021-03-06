package com.github.dae3.datadial

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
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

const val requestFRPCA = 0

class MainActivity : AppCompatActivity()
        , BottomNavigationView.OnNavigationItemSelectedListener
        , View.OnClickListener {

    private lateinit var prefs: SharedPreferences
    private lateinit var interval: NetworkStatsInterval
    private var fragment: Fragment? = PieChartFragment()
    private lateinit var perm: PermissionManager
    private var curFrag: Int = R.id.bottomnav_home

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        bottom_navbar.setOnNavigationItemSelectedListener(this)

        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        perm = PermissionManager(this)

        if (firstRun) {
            startActivityForResult(Intent(this, FirstRunPreferenceCaptureActivity::class.java), requestFRPCA)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == requestFRPCA && resultCode == FirstRunPreferenceResult.ChangePreferences.ordinal)
            bottom_navbar.selectedItemId = R.id.bottomnav_settings
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()

        // might have been changed by PrePermissionRequestActivity
        // this is a stinky way of managing shared state but it'll do for now
        perm.refresh()

        val sb = Snackbar.make(main_coord_layout, getString(R.string.main_snackbar_nopermission), LENGTH_INDEFINITE)
                .setAction(R.string.snackbar_noperm_action, this)

        fragmentManager.beginTransaction().replace(R.id.main_activity_body, fragment).commit()

        // If missing any permission, display snackbar with button to launch PrePermissionRequestActivity
        if (!perm.havePhonePermission || !perm.haveUsagePermission)
            sb.show()
        else {
            try {
                interval = DayNOfMonthNetworkStatsInterval(
                        today = GregorianCalendar(Locale.getDefault()),
                        dayOfMonth = prefs.getInt(
                                resources.getString(R.string.prefs_key_billingcycle_startday),
                                resources.getInteger(R.integer.default_interval_startday)
                        )
                )
            } catch (e: SecurityException) {
                // belt and braces; permission *could* be revoked between checking
                //  and actually using
                sb.show()
            }
        }

    }

    /**
     * onClick handler for snackbar "Fix" button, launches PrePermissionCheckActivity
     */
    override fun onClick(v: View?) {
        startActivity(Intent(this, PrePermissionRequestActivity::class.java))
    }

    /**
     * Inflate appropriate fragment when bottom nav bar used
     */
    override fun onNavigationItemSelected(item: MenuItem): Boolean = setContent(item.itemId)

    private fun setContent(layout: Int): Boolean {
        fragment = when (layout) {
            R.id.bottomnav_home -> PieChartFragment()
            R.id.bottomnav_settings -> SettingsFragment()
            R.id.bottomnav_about -> AboutFragment()
            else -> null
        }

        curFrag = layout

        return if (fragment == null)
            false
        else {
            fragmentManager.beginTransaction().replace(R.id.main_activity_body, fragment).commit()
            true
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putInt("fragment", curFrag)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        val savedlayout = savedInstanceState?.getInt("fragment")
        savedlayout?.let {
            setContent(savedlayout)
            bottom_navbar.selectedItemId = savedlayout
        }
    }

    private val firstRun: Boolean
        get() {
            val key = resources.getString(R.string.prefs_itemkey_firstrun)
            return if (prefs.getBoolean(key, true)) {
                prefs.edit().putBoolean(key, false).apply()
                true
            } else false
        }
}
