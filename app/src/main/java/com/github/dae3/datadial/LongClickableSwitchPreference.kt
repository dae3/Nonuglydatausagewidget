package com.github.dae3.datadial

import android.content.Context
import android.content.Intent
import android.content.res.TypedArray
import android.preference.SwitchPreference
import android.support.v7.widget.DialogTitle
import android.util.AttributeSet
import android.view.View

class LongClickableSwitchPreference(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int)
    : SwitchPreference(context, attrs, defStyleAttr, defStyleRes),
        View.OnLongClickListener {

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, android.R.attr.switchPreferenceStyle)
    constructor(context: Context) : this(context, null)

    private var textRes: Int
    private var title: String

    init {
        val ta = context.theme.obtainStyledAttributes(attrs, R.styleable.LongClickableSwitchPreference, defStyleAttr, defStyleRes)
        title = ta.getString(R.styleable.LongClickableSwitchPreference_moreInfoTitle)
        textRes = ta.getResourceId(R.styleable.LongClickableSwitchPreference_moreInfoTextResource, 0)
    }

    override fun onBindView(view: View?) {
        super.onBindView(view)

        view?.findViewById<View>(android.R.id.title)?.setOnLongClickListener(this)
        view?.findViewById<View>(android.R.id.summary)?.setOnLongClickListener(this)
    }

    override fun onLongClick(v: View?): Boolean {

        v?.context?.startActivity(
                Intent(v?.context, ScrollingScreenOfTextActivity::class.java).apply {
                    putExtra(ScrollingScreenOfTextActivity.INTENT_TITLE_EXTRA, title)
                    putExtra(ScrollingScreenOfTextActivity.INTENT_TEXTRESOURCE_EXTRA, textRes)
                })

        return true
    }
}