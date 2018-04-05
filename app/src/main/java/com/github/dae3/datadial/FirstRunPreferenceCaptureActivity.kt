package com.github.dae3.datadial

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_first_run_preferences.*

enum class FirstRunPreferenceResult { NoChoice, ChangePreferences, Continue }

class FirstRunPreferenceCaptureActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_run_preferences)

        this.text_blurb.text = getString(
                R.string.firstrun_blurb,
                resources.getInteger(R.integer.default_maxdata),
                resources.getInteger(R.integer.default_interval_startday),
                button_done.text as String,
                button_changesettings.text as String
        )
    }

    fun onClickButton(view: View) {
        setResult(
                when (view.id) {
                    R.id.button_done -> FirstRunPreferenceResult.Continue.ordinal
                    R.id.button_changesettings -> FirstRunPreferenceResult.ChangePreferences.ordinal
                    else -> throw IllegalStateException("FirstRunPreferenceActivity unknown onClickButton view id $view.id")
                },
                Intent()
        )
        finish()
    }
}
