package com.example.dever.nonUglyDataUsageWidget

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class FirstRunPreferenceCaptureActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_run_preferences)
    }

    public fun onDone() {
        val rdata = Intent()
        setResult(Activity.RESULT_CANCELED, rdata)
    }
}
