package com.example.dever.nonUglyDataUsageWidget

import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_scrolling_screen_of_text.*
import java.io.ByteArrayOutputStream

class ScrollingScreenOfTextActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling_screen_of_text)
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_HOME + ActionBar.DISPLAY_SHOW_TITLE + ActionBar.DISPLAY_HOME_AS_UP
        supportActionBar?.title = intent.getStringExtra(INTENT_TITLE_EXTRA) ?: this.javaClass.name

        val rid = intent.getIntExtra(ScrollingScreenOfTextActivity.INTENT_TEXTRESOURCE_EXTRA, 0)
        if (rid == 0)
            textView.text = resources.getString(R.string.scrolling_screen_of_text_error, rid)
        else {
            val ris = resources.openRawResource(rid)
            val sos = ByteArrayOutputStream()
            ris.copyTo(sos)
            textView.text = String(sos.toByteArray())
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean =
            if (item?.itemId == android.R.id.home) {
                finish()
                true
            } else
                super.onOptionsItemSelected(item)

    companion object {
        const val INTENT_TITLE_EXTRA = "Title"
        const val INTENT_TEXTRESOURCE_EXTRA = "TextResource"
    }
}
