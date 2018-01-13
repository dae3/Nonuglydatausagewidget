package com.example.dever.nonUglyDataUsageWidget

import android.os.Bundle
import android.preference.PreferenceFragment

class SeekBarPreferenceFragment : PreferenceFragment() {

      override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preferences)
    }


}// Required empty public constructor
