package com.example.dever.nonUglyDataUsageWidget

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet

/**
 * EditIntPreference that displays and steps in Gb but persists a value in bytes
 */
class EditGigabytePreference(context: Context?, attrs : AttributeSet?) : EditLongPreference(context, attrs) {

    override fun onGetDefaultValue(a: TypedArray?, index: Int): Any = super.onGetDefaultValue(a, index) as Long

    override fun onSetInitialValue(restorePersistedValue: Boolean, defaultValue: Any?) {
        super.onSetInitialValue(restorePersistedValue, defaultValue)
        value = if (restorePersistedValue) value.asGb() else defaultValue as Long
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult && shouldPersist()) {
            val editor = prefs.edit()
            editor.putLong(key, value.asBytes())
            editor.apply()
        }
    }

}

/**
 * Extensions to Long to convert between gigabytes and bytes
 */
fun Long.asGb() : Long { return this / 1024 / 1024 / 1024 }
fun Long.asBytes() : Long { return this * 1024 * 1024 * 1024 }
