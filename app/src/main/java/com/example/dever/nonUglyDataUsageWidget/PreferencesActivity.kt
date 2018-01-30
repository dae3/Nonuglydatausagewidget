package com.example.dever.nonUglyDataUsageWidget

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import com.lb.material_preferences_library.AppCompatPreferenceActivity



/*
https://gldraphael.com/blog/adding-a-toolbar-to-preference-activity/
Prolly would have been easier to extend AppCompatActivity, do the app bar normally,
and create a PreferenceFragment... oh well
*/

class PreferencesActivity : AppCompatPreferenceActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        layoutInflater.inflate(R.layout.toolbar, findViewById(android.R.id.content))
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar.setDisplayHomeAsUpEnabled(true)

        listView.setPadding(
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, resources.displayMetrics).toInt(),
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        (resources.getDimension(R.dimen.activity_vertical_margin).toInt() + 30).toFloat(),
                        resources.displayMetrics
                ).toInt(),
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, resources.displayMetrics).toInt(),
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, resources.displayMetrics).toInt()
        )

        addPreferencesFromResource(R.xml.preferences)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actionbar, menu)
        return true

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action_settings -> {
                parent.startActivity(Intent(parent, PreferencesActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
