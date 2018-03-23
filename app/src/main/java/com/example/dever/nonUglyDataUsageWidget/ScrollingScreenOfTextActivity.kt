package com.example.dever.nonUglyDataUsageWidget

import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem

class ScrollingScreenOfTextActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling_screen_of_text)
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_HOME + ActionBar.DISPLAY_SHOW_TITLE + ActionBar.DISPLAY_HOME_AS_UP

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean =
            if (item?.itemId == android.R.id.home) {
                finish()
                true
            } else
                super.onOptionsItemSelected(item)
}