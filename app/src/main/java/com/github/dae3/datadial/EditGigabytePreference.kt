package com.github.dae3.datadial

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet

/**
 * EditIntPreference that displays and steps in Gb but persists a value in bytes
 */
class EditGigabytePreference(context: Context?, attrs : AttributeSet?) : EditFloatPreference(context, attrs) {

    override fun onGetDefaultValue(a: TypedArray?, index: Int): Any = super.onGetDefaultValue(a, index) as Float

    override fun onSetInitialValue(restorePersistedValue: Boolean, defaultValue: Any?) {
        super.onSetInitialValue(restorePersistedValue, defaultValue)
        value = if (restorePersistedValue) value.asGb() else defaultValue as Float
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult && shouldPersist()) {
            val editor = prefs.edit()
            editor.putFloat(key, value.asBytes())
            editor.apply()
        }
    }

}

/**
 * Extensions to Long to convert between gigabytes and bytes
 */
fun Float.asGb() : Float { return this / 1024 / 1024 / 1024 }
fun Float.asBytes() : Float { return this * 1024 * 1024 * 1024 }
