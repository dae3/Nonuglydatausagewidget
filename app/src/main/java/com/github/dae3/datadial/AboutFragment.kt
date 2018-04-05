package com.github.dae3.datadial

import android.app.Activity
import android.app.Fragment
import android.app.PendingIntent
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
                AboutItem(
                        getString(R.string.about_headline_license),
                        "",
                        createPendingIntentForSSOT(getString(R.string.about_headline_license), R.raw.license)
                ),
                AboutItem(
                        getString(R.string.about_headline_privacypolicy),
                        getString(R.string.about_subhead_privacypolicy),
                        createPendingIntentForSSOT(getString(R.string.about_headline_privacypolicy), R.raw.privacypolicy)
                )
        ))

        mainview.about_listview.onItemClickListener = this

        return mainview
    }

    private fun createPendingIntentForSSOT(title : String, textResource : Int) : PendingIntent {
        val intent = Intent(context, ScrollingScreenOfTextActivity::class.java)
        intent.putExtra(ScrollingScreenOfTextActivity.INTENT_TITLE_EXTRA, title)
        intent.putExtra(ScrollingScreenOfTextActivity.INTENT_TEXTRESOURCE_EXTRA, textResource)
        return PendingIntent.getActivity(context, textResource, intent, 0)
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val adapter = mainview.about_listview.adapter as TwoLineArrayAdapter
        adapter.getItem(position).clickAction?.send()
    }

    private data class AboutItem(
            var title: String,
            var subtitle: String,
            var clickAction: PendingIntent?
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