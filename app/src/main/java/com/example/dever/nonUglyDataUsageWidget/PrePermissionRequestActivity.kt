package  com.example.dever.nonUglyDataUsageWidget

import android.app.AlertDialog
import android.app.Dialog
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.DialogInterface
import android.content.DialogInterface.BUTTON_NEGATIVE
import android.content.DialogInterface.BUTTON_POSITIVE
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView

class PrePermissionRequestActivity : AppCompatActivity(), View.OnClickListener, DialogInterface.OnClickListener, ViewPager.OnPageChangeListener {
    private lateinit var vp: ViewPager
    private val perm = PermissionManager(this)
    private lateinit var rightarrow: ImageView
    private lateinit var leftarrow: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_pre_permission_request)

        vp = findViewById(R.id.perm_view_pager)
        vp.adapter = PrePermissionRequestActivity.PrePermissionRequestPagerAdapter(supportFragmentManager)
        vp.addOnPageChangeListener(this)

        rightarrow = findViewById<ImageView>(R.id.imgPrePermRightArrow)
        rightarrow.setOnClickListener(this)
        leftarrow = findViewById<ImageView>(R.id.imgPrePermLeftArrow)
        leftarrow.setOnClickListener(this)
        leftarrow.visibility = INVISIBLE

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    /**
     * Warn if closing without actually granting permissions, which renders the whole app kinda useless
     */
    override fun onBackPressed() {
        if (perm.havePhoneStatePermission && perm.haveUsagePermission)
            super.onBackPressed()
        else
            ClosingWithoutPermissionGrantedDialog.show(this)
    }

    /**
     * Sends an Intent to any AppWidgets so they can refresh after a permission change
     */
    override fun onStop() {
        super.onStop()
        val i = Intent()
        i.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        i.putExtra(
                Widget.WIDGET_IDS_KEY,
                AppWidgetManager.getInstance(this).getAppWidgetIds(ComponentName(this, Widget::class.java))
        )
        this.sendBroadcast(i)
    }

    /**
     * View.OnClickListener implementation
     * Navigate to previous or next screen using ViewPager
     */
    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.prepermission_fragment_image -> when (vp.currentItem) {
                0 -> Unit
                1 -> perm.requestPhoneStatePermission()
                2 -> perm.requestUsagePermission()
                else -> throw IllegalStateException("PrePermissionRequestActivity ViewPager unexpected currentItem ${vp.currentItem}")
            }
            R.id.prepermission_fragment_lastpagebutton -> {
                if (perm.havePhoneStatePermission && perm.haveUsagePermission)
                    finish()
                else
                    ClosingWithoutPermissionGrantedDialog.show(this)
            }
            R.id.imgPrePermRightArrow -> vp.currentItem += if (vp.canScrollHorizontally(1)) 1 else 0
            R.id.imgPrePermLeftArrow -> vp.currentItem -= if (vp.canScrollHorizontally(-1)) 1 else 0
            else -> throw IllegalStateException("PrePermissionRequestActivity unexpected click event on id $v?.id")
        }
    }

    /**
     * DialogInterface.OnClickListener implementation
     * Called via onBackPressed(); continue or stop closing of activity depending on dialog response
     */
    override fun onClick(dialog: DialogInterface?, which: Int) {
        if (which == BUTTON_POSITIVE)
            super.onBackPressed()
    }

    /**
     * ViewPager.OnPageChangeListener implementation
     */
    override fun onPageScrollStateChanged(state: Int) = Unit // don't care

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) = Unit // don't care

    /**
     * Enable left and right navigation arrows depending on current page
     */
    override fun onPageSelected(position: Int) {
        // position is 0 based
        when (position) {
            vp.adapter?.count?.minus(1) -> {
                leftarrow.visibility = VISIBLE; rightarrow.visibility = INVISIBLE
            } // last page
            0 -> {
                leftarrow.visibility = INVISIBLE; rightarrow.visibility = VISIBLE
            } // first page
            else -> {
                leftarrow.visibility = VISIBLE; rightarrow.visibility = VISIBLE
            }
        }
    }

    /**
     * Simple PagerAdapter to display Fragments as swipeable pages within this Activity
     */
    private class PrePermissionRequestPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        private val NUMPAGES: Int = 3
        private val pages = arrayOf(
                PrePermissionRequestActivity.PrePermissionRequestFragment.CreatePrePermissionRequestFragment(R.layout.prepermissionrequest_page1_fragment_layout),
                PrePermissionRequestActivity.PrePermissionRequestFragment.CreatePrePermissionRequestFragment(R.layout.prepermissionrequest_page2_fragment_layout),
                PrePermissionRequestActivity.PrePermissionRequestFragment.CreatePrePermissionRequestFragment(R.layout.prepermissionrequest_page3_fragment_layout)
        )


        override fun getCount(): Int = NUMPAGES

        override fun getItem(position: Int) = pages[position]
    }

    /**
     * Simple Fragment to display swipeable pages for each step within PrePermissionRequestActivity
     */
    class PrePermissionRequestFragment : android.support.v4.app.Fragment() {
        private var layout: Int? = null

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            if (layout == null)
                throw IllegalArgumentException("PrePermissionRequestFragment must have layout passed as an argument")
            else {
                val v = inflater.inflate(layout!!, container, false)
                val ppractivity = activity as PrePermissionRequestActivity
                v.findViewById<ImageView>(R.id.prepermission_fragment_image)?.setOnClickListener(ppractivity)
                v.findViewById<Button>(R.id.prepermission_fragment_lastpagebutton)?.setOnClickListener(ppractivity)

                return v
            }
        }

        override fun setArguments(args: Bundle?) {
            super.setArguments(args)
            layout = args?.getInt("layout")
        }

        /**
         * Convenience factory method to fake a constructor with arguments
         */
        companion object {
            public fun CreatePrePermissionRequestFragment(layout: Int): PrePermissionRequestFragment {
                val f = PrePermissionRequestFragment()
                val b = Bundle()
                b.putInt("layout", layout)

                f.arguments = b
                return f
            }
        }
    }

    class ClosingWithoutPermissionGrantedDialog : DialogFragment() {

        lateinit var parent: PrePermissionRequestActivity
        var response: Int = BUTTON_NEGATIVE

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val d = AlertDialog.Builder(activity)

            d.setIcon(R.drawable.ic_warning_black_24dp)
            d.setTitle(getString(R.string.prepermission_alert_dialog_title))
            d.setMessage(R.string.prepermission_alert_dialog_message)
            d.setPositiveButton(getString(R.string.prepermission_alert_dialog_positive_button_caption), parent)
            d.setNegativeButton(getString(R.string.prepermission_alert_dialog_negative_button_caption), parent)

            return d.create()
        }

        companion object {

            private val DIALOG_TAG = "CWPGD"

            fun show(parent: PrePermissionRequestActivity) {
                val dlg = ClosingWithoutPermissionGrantedDialog()
                dlg.parent = parent
                dlg.show(parent.supportFragmentManager, DIALOG_TAG)
            }
        }
    }
}
