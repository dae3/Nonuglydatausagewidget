package com.example.dever.nonUglyDataUsageWidget

import android.content.Context
import android.util.AttributeSet

/**
 * EditIntPreference that displays and steps in Gb but persists a value in bytes
 */

//private const val GBTOBYTES : Int = 1024 * 1024 * 1024
//private const val BYTESTOGB : Long = (1 / GBTOBYTES).toLong()

class EditGigabytePreference(context: Context?, attrs : AttributeSet?) : EditIntPreference(context, attrs) {
//    override fun persistInt(value: Int): Boolean {
//        return super.persistInt(value * GBTOBYTES)
//    }

//    override fun getPersistedInt(defaultReturnValue: Int): Int {
//        return super.getPersistedInt(defaultReturnValue * BYTESTOGB)
//    }

    override fun getValueFromSharedPreferences() : Int {
        return super.getValueFromSharedPreferences().asGb()
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {
            val editor = prefs.edit()
            editor.putInt(key, value.asBytes())
            editor.commit()
        }
    }
}

fun Int.asGb() : Int { return this / 1024 / 1024 / 1024 }
fun Int.asBytes() : Int { return this * 1024 * 1024 * 1024 }
