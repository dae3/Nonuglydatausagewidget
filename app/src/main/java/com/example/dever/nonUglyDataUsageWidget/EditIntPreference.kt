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

open class EditIntPreference(context: Context?, attrs: AttributeSet?) : DialogPreference(context, attrs) {
    private lateinit var mValueTextView: TextView
    protected var value: Int = 0
    private val buttonListener = DialogListener()
    private val prefs : SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private var keyname: String
    protected var minValue : Int = Int.MIN_VALUE
    protected var maxValue : Int = Int.MAX_VALUE
    protected var stepValue : Int = 1

    init {
        isPersistent = false
        dialogLayoutResource = R.layout.editnumber_preferences_dialog

        val k: String? = attrs?.getAttributeValue("http://schemas.android.com/apk/res/android", "key")
        if (k == null)
            throw IllegalArgumentException("EditIntPreference requires an android:key attribute")
        else
            keyname = k

        minValue = attrs.getAttributeIntValue("http://dever.example.com", "minimum", minValue)
        maxValue = attrs.getAttributeIntValue("http://dever.example.com", "maximum", maxValue)
        stepValue = attrs.getAttributeIntValue("http://dever.example.com", "step", stepValue)
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {
            val editor = prefs.edit()
            editor.putInt(key, value)
            editor.apply()
        }
    }

    override fun onSetInitialValue(restorePersistedValue: Boolean, defaultValue: Any?) {
        value = if (restorePersistedValue)
            getPersistedInt(0)
        else
            defaultValue as Int
    }

    override fun onBindDialogView(view: View?) {
        super.onBindDialogView(view)

        value = getValueFromSharedPreferences()

        if (view != null) {
            mValueTextView = view.findViewById(R.id.txtIntPrefInt)
            mValueTextView.text = value.toString()

            val mBtnDown = view.findViewById<ImageButton>(R.id.btnEditIntDown)
            mBtnDown.setOnClickListener(buttonListener)

            val mBtnUp = view.findViewById<ImageButton>(R.id.btnEditIntUp)
            mBtnUp.setOnClickListener(buttonListener)

        }
    }

    protected open fun getValueFromSharedPreferences() = prefs.getInt(key, 0)

    override fun onGetDefaultValue(a: TypedArray?, index: Int): Any {
        return a?.getInt(index, 0)!!
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
