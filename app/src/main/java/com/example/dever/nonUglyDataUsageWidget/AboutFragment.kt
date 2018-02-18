package com.example.dever.nonUglyDataUsageWidget

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView

class AboutFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val v = inflater?.inflate(R.layout.fragment_about, container, false)!!
        v.findViewById<ListView>(R.id.about_listview).adapter = TwoLineArrayAdapter(activity, R.layout.aboutlist, arrayOf(
                Pair(getString(R.string.about_headline_version), getString(R.string.app_version)),
                Pair(getString(R.string.about_headline_author), getString(R.string.app_author))
        ))
        return v
    }

    private class ViewHolder(var headline: TextView, var subline: TextView)

    private class TwoLineArrayAdapter(context: Context, layout: Int, private val content: Array<Pair<String, String>>) :
            ArrayAdapter<Pair<String, String>>(context, layout, content) {

        private val inflater = (context as Activity).layoutInflater

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val v : View =
                    if (convertView == null) {
                        val nv = inflater.inflate(R.layout.aboutlist, parent, false)
                        nv.tag = ViewHolder(nv.findViewById(R.id.about_line1), nv.findViewById(R.id.about_line2))
                        nv
                    } else convertView

            with (v.tag as ViewHolder) {
                headline.text = content[position].first
                subline.text = content[position].second
            }

            return v
        }
    }
}