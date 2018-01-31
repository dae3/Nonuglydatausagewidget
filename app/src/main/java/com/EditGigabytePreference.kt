package com.example.dever.nonUglyDataUsageWidget

import android.content.Context
import android.util.AttributeSet

/**
 * EditIntPreference that displays and steps in Gb but persists a value in bytes
 */

//private const val GBTOBYTES : Int = 1024 * 1024 * 1024
//private const val BYTESTOGB : Long = (1 / GBTOBYTES).toLong()

class EditGigabytePreference(context: Context?, attrs : AttributeSet?) : EditLongPreference(context, attrs) {

    override fun getValueFromSharedPreferences() : Long {
        return super.getValueFromSharedPreferences().asGb()
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {
            val editor = prefs.edit()
            editor.putLong(key, value.asBytes())
            editor.commit()
        }
    }

    override fun onSetInitialValue(restorePersistedValue: Boolean, defaultValue: Any?) {
        super.onSetInitialValue(restorePersistedValue, defaultValue)
    }
}

fun Long.asGb() : Long { return this / 1024 / 1024 / 1024 }
fun Long.asBytes() : Long { return this * 1024 * 1024 * 1024 }
