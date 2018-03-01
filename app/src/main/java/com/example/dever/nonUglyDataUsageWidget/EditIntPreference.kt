package com.example.dever.nonUglyDataUsageWidget

import android.content.Context
import android.content.SharedPreferences
import android.content.res.TypedArray
import android.preference.DialogPreference
import android.preference.PreferenceManager
import android.util.AttributeSet
import android.view.View
import android.widget.ImageButton
import android.widget.TextView

/**
 * last resort default value used if the XML default is missing or attempt to retrieve persisted
 * value from preference backing store fails
 */
private const val LAST_RESORT_DEFAULT = 1

open class EditIntPreference(context: Context?, attrs: AttributeSet?) : DialogPreference(context, attrs) {
    private lateinit var mValueTextView: TextView
    private lateinit var mUnitTextView: TextView
    protected var value: Int = 0
    private val buttonListener = DialogListener()
    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private var keyname: String
    protected var minValue: Int = Int.MIN_VALUE
    protected var maxValue: Int = Int.MAX_VALUE
    protected var stepValue: Int = 1
    private var unitCaptionText: String

    init {
//        isPersistent = false
        dialogLayoutResource = R.layout.editnum_preferences_dialog

        keyname = attrs?.getAttributeValue("http://schemas.android.com/apk/res/android", "key") ?:
            throw IllegalArgumentException("EditIntPreference requires an android:key attribute")


        minValue = attrs.getAttributeIntValue("http://dever.example.com", "minimum", minValue)
        maxValue = attrs.getAttributeIntValue("http://dever.example.com", "maximum", maxValue)
        stepValue = attrs.getAttributeIntValue("http://dever.example.com", "step", stepValue)
        unitCaptionText = attrs.getAttributeValue("http://dever.example.com", "unitcaption")
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult && shouldPersist()) {
            val editor = prefs.edit()
            editor.putInt(key, value)
            editor.apply()
        }
    }

    /**
     * set the preference's value
     *
     * Use the superclasses's getPersistedInt if restoring from preferences
     *  otherwise the passed-in defaultValue, which has in turn been set by
     *  out onGetDefaultValue()
     *
     *  Fall back to LAST_RESORT_DEFAULT if restorePersistedValue == true but
     *   nothing found in persistent store, L_R_D == 1 which is a reasonable
     *   default for an integer
     */
    override fun onSetInitialValue(restorePersistedValue: Boolean, defaultValue: Any?) {
        val v = if (restorePersistedValue) getPersistedInt(LAST_RESORT_DEFAULT) else defaultValue as Int
        value = when {
            v < minValue -> minValue
            v > maxValue -> maxValue
            else -> v
        }
    }

    override fun onBindDialogView(view: View?) {
        super.onBindDialogView(view)

        if (view != null) {
            mValueTextView = view.findViewById(R.id.txtIntPrefInt)
            mValueTextView.text = value.toString()

            mUnitTextView = view.findViewById(R.id.txtIntPrefUnitCaption)
            mUnitTextView.text = unitCaptionText

            val mBtnDown = view.findViewById<ImageButton>(R.id.btnEditIntDown)
            mBtnDown.setOnClickListener(buttonListener)

            val mBtnUp = view.findViewById<ImageButton>(R.id.btnEditIntUp)
            mBtnUp.setOnClickListener(buttonListener)

        }
    }

    override fun onGetDefaultValue(a: TypedArray?, index: Int): Any {
        return a?.getInteger(
                index,
                LAST_RESORT_DEFAULT
        ) as Any
    }

    inner class DialogListener : View.OnClickListener {
        override fun onClick(v: View?) {
            value += when (v?.id) {
                R.id.btnEditIntUp -> stepValue
                R.id.btnEditIntDown -> -stepValue
                else -> 0
            }

            value = if (value < minValue) minValue else value
            value = if (value > maxValue) maxValue else value


            mValueTextView.text = "$value"
        }
    }
}
