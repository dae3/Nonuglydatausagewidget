package com.example.dever.nonUglyDataUsageWidget

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import kotlinx.android.synthetic.main.aboutlist.view.*
import kotlinx.android.synthetic.main.fragment_about.view.*

class AboutFragment : Fragment(), AdapterView.OnItemClickListener {

    private lateinit var mainview: View

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {

        mainview = inflater?.inflate(R.layout.fragment_about, container, false)!!
        mainview.about_listview.adapter = TwoLineArrayAdapter(activity, R.layout.aboutlist, arrayOf(
                AboutItem(getString(R.string.about_headline_version), getString(R.string.app_version), null),
                AboutItem(getString(R.string.about_headline_author), getString(R.string.app_author), null),
                AboutItem("License", "", fun(v: View?): Boolean { startActivity(Intent(context, ScrollingScreenOfTextActivity::class.java)); return true}),
                AboutItem("Privacy Policy", "How this app uses your personal information", null)
        ))

        mainview.about_listview.onItemClickListener = this

        return mainview
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val adapter = mainview.about_listview.adapter as TwoLineArrayAdapter
        adapter.getItem(position).clickAction?.invoke(view)
    }

    private data class AboutItem(
            var title: String,
            var subtitle: String,
            var clickAction: ((view: View?) -> Boolean)?
    )

    private class ViewHolder(var headline: TextView, var subline: TextView)

    private class TwoLineArrayAdapter(context: Context, layout: Int, private val content: Array<AboutItem>) :
            ArrayAdapter<AboutItem>(context, layout, content) {

        private val inflater = (context as Activity).layoutInflater

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val v: View =
                    if (convertView == null) {
                        val nv = inflater.inflate(R.layout.aboutlist, parent, false)
                        nv.tag = ViewHolder(nv.about_line1, nv.about_line2)
                        nv
                    } else convertView

            with(v.tag as ViewHolder) {
                headline.text = content[position].title
                subline.text = content[position].subtitle
            }

            return v
        }
    }
}