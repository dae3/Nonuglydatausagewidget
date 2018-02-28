package com.example.dever.nonUglyDataUsageWidget

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View

enum class FirstRunPreferenceResult { NoChoice, ChangePreferences, Continue }

class FirstRunPreferenceCaptureActivity : AppCompatActivity() {

    private var myResult : Int = FirstRunPreferenceResult.NoChoice.ordinal
    private var canClose = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_run_preferences)
    }

    public fun onClickButton(view: View) {
        setResult(
                when (view.id) {
                    R.id.button_done -> FirstRunPreferenceResult.Continue.ordinal
                    R.id.button_changesettings -> FirstRunPreferenceResult.ChangePreferences.ordinal
                    else -> throw IllegalStateException("FirstRunPreferenceActivity unknown onClickButton view id $view.id")
                },
                Intent()
        )
    }
}
