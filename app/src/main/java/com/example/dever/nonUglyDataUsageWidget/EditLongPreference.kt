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
 * Subclass DialogPreference to edit a number with
 * - optional unit caption
 * - Up and down buttons to roll the number
 * - Upper and lower limits
 */
private const val LAST_RESORT_DEFAULT = 1L

open class EditLongPreference(context: Context?, attrs: AttributeSet?) : DialogPreference(context, attrs) {
    private lateinit var mValueTextView: TextView
    private lateinit var mUnitTextView : TextView
    protected var value: Long = 0
    private val buttonListener = DialogListener()
    protected val prefs : SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private var keyname: String
    protected var minValue : Long = Long.MIN_VALUE
    protected var maxValue : Long = Long.MAX_VALUE
    protected var stepValue : Long = 1L
    private var unitCaptionText : String

    init {
        isPersistent = false  // we handle our own persistence
        dialogLayoutResource = R.layout.editnum_preferences_dialog

        keyname = attrs?.getAttributeValue("http://schemas.android.com/apk/res/android", "key") ?:
            throw IllegalArgumentException("EditLongPreference requires an android:key attribute")

        minValue = attrs.getAttributeIntValue("http://dever.example.com", "minimum", Int.MIN_VALUE).toLong()
        maxValue = attrs.getAttributeIntValue("http://dever.example.com", "maximum", Int.MAX_VALUE).toLong()
        stepValue = attrs.getAttributeIntValue("http://dever.example.com", "step", Int.MIN_VALUE).toLong()
        unitCaptionText = attrs.getAttributeValue("http://dever.example.com", "unitcaption")
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult && shouldPersist()) {
            val editor = prefs.edit()
            editor.putLong(key, value)
            editor.apply()
        }
    }

    override fun onSetInitialValue(restorePersistedValue: Boolean, defaultValue: Any?) {
        val v = if (restorePersistedValue) getPersistedLong(LAST_RESORT_DEFAULT) else defaultValue as Long
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
        return (a?.getString(index)?.toLongOrNull() ?: LAST_RESORT_DEFAULT)
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
