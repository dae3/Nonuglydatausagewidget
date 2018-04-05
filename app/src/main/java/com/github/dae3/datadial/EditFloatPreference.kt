package com.github.dae3.datadial

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
 * Subclass DialogPreference to edit a number with
 * - optional unit caption
 * - Up and down buttons to roll the number
 * - Upper and lower limits
 */
private const val LAST_RESORT_DEFAULT = 1F

open class EditFloatPreference(context: Context?, attrs: AttributeSet?) : DialogPreference(context, attrs) {
    private lateinit var mValueTextView: TextView
    private lateinit var mUnitTextView : TextView
    protected var value: Float = 0F
    private val buttonListener = DialogListener()
    protected val prefs : SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private var keyname: String
    protected var minValue : Float = Float.MIN_VALUE
    protected var maxValue : Float = Float.MAX_VALUE
    protected var stepValue : Float = 1F
    private var unitCaptionText : String

    init {
         dialogLayoutResource = R.layout.editnum_preferences_dialog

        keyname = attrs?.getAttributeValue("http://schemas.android.com/apk/res/android", "key") ?:
            throw IllegalArgumentException("EditFloatPreference requires an android:key attribute")

        minValue = attrs.getAttributeFloatValue("https://github.com/dae3", "minimum", Float.MIN_VALUE)
        maxValue = attrs.getAttributeFloatValue("https://github.com/dae3", "maximum", Float.MAX_VALUE)
        stepValue = attrs.getAttributeFloatValue("https://github.com/dae3", "step", Float.MIN_VALUE+1)
        unitCaptionText = attrs.getAttributeValue("https://github.com/dae3", "unitcaption")
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult && shouldPersist()) {
            val editor = prefs.edit()
            editor.putFloat(key, value)
            editor.apply()
        }
    }

    override fun onSetInitialValue(restorePersistedValue: Boolean, defaultValue: Any?) {
        val v = if (restorePersistedValue) getPersistedFloat(LAST_RESORT_DEFAULT) else defaultValue as Float
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
        return (a?.getString(index)?.toFloatOrNull() ?: LAST_RESORT_DEFAULT)
    }

    inner class DialogListener : View.OnClickListener {
        override fun onClick(v: View?) {
            value += when (v?.id) {
                R.id.btnEditIntUp -> stepValue
                R.id.btnEditIntDown -> -stepValue
                else -> 0F
            }

            value = if (value < minValue) minValue else value
            value = if (value > maxValue) maxValue else value


            mValueTextView.text = "$value"
        }
    }
}
