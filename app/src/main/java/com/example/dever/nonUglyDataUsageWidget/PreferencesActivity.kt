package com.example.dever.nonUglyDataUsageWidget

import android.os.Bundle
import android.preference.PreferenceActivity

class PreferencesActivity : PreferenceActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preferences)
    }
}
