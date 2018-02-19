package  com.example.dever.nonUglyDataUsageWidget

import android.Manifest.permission.READ_PHONE_STATE
import android.app.AlertDialog
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.DialogInterface
import android.content.DialogInterface.BUTTON_NEGATIVE
import android.content.DialogInterface.BUTTON_POSITIVE
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import com.example.dever.nonUglyDataUsageWidget.PermissionManager.PhonePermissionState.*
import kotlinx.android.synthetic.main.activity_pre_permission_request.*
import kotlinx.android.synthetic.main.prepermissionrequest_page2_fragment_layout.view.*
import kotlinx.android.synthetic.main.prepermissionrequest_page3_fragment_layout.view.*

class PrePermissionRequestActivity :
        AppCompatActivity(),
        View.OnClickListener,
        ViewPager.OnPageChangeListener,
        ActivityCompat.OnRequestPermissionsResultCallback {
    private lateinit var perm: PermissionManager

    private val permReqReadState = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_pre_permission_request)

        perm_view_pager.adapter = PrePermissionRequestActivity.PrePermissionRequestPagerAdapter(supportFragmentManager)
        perm_view_pager.addOnPageChangeListener(this)

        imgPrePermRightArrow.setOnClickListener(this)
        imgPrePermLeftArrow.setOnClickListener(this)
        imgPrePermLeftArrow.visibility = INVISIBLE

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        perm = PermissionManager(this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        // let PermissionManager update its permission state tracking
        if (requestCode == permReqReadState) perm.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    /**
     * Warn if closing without actually granting permissions, which renders the whole app kinda useless
     */
    override fun onBackPressed() {
        if (perm.havePhonePermission && perm.haveUsagePermission)
            super.onBackPressed()
        else
            showContinueWithoutPermissionDialog()
    }

    private fun superOnBackPressed() = super.onBackPressed()

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
     * Also handles events from Fragments
     */
    override fun onClick(v: View?) {

        when (v?.id) {
            /*
            Grant permission button on phone permission page
             - request permission if we don't have it
             - show explanatory dialog then launch App Info activity if denied don't ask again
             - throw exception if somehow clicked when we already have permission
             */
            R.id.prepermission_phone_button -> when (perm.phonePermissionState) {
                NeverRequested, Denied -> requestPermissions(arrayOf(READ_PHONE_STATE), permReqReadState)
                Granted -> throw IllegalStateException(getString(R.string.exception_ppra_phone_permission_already_granted))
                DeniedNeverAskAgain -> {
                    // alert that you're now forced to display the App Info activity to grant permission
                    AlertDialog.Builder(this)
                            .setTitle(R.string.permission_denied_dialog_title)
                            .setMessage(R.string.permission_denied_dialog_message)
                            .setPositiveButton(
                                    R.string.prepermission_button_grant_caption,
                                    { _, which ->
                                        if (which == BUTTON_POSITIVE) {
                                            val i = Intent()
                                            i.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                            i.data = Uri.fromParts("package", this.packageName, null)
                                            startActivity(i)
                                        }
                                    })
                            .show()
                }
            }

        // "Grant permission" button on usage permission page
            R.id.usage_grant_permission_button -> ContextCompat.startActivity(this, Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), null)

        // page navigation arrows
            R.id.imgPrePermRightArrow -> when (perm_view_pager.currentItem) {
                1 -> if (perm.havePhonePermission) perm_view_pager.currentItem++ else showContinueWithoutPermissionDialog()
                2 -> if (perm.haveUsagePermission) finish() else showContinueWithoutPermissionDialog()
                else -> perm_view_pager.currentItem += if (perm_view_pager.canScrollHorizontally(1)) 1 else 0
            }
            R.id.imgPrePermLeftArrow -> perm_view_pager.currentItem -= if (perm_view_pager.canScrollHorizontally(-1)) 1 else 0

            else -> throw IllegalStateException("PrePermissionRequestActivity unexpected click event on id $v?.id")
        }
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
        if (position == 0) {
            imgPrePermLeftArrow.visibility = INVISIBLE; imgPrePermRightArrow.visibility = VISIBLE
        } else {
            imgPrePermLeftArrow.visibility = VISIBLE; imgPrePermRightArrow.visibility = VISIBLE
        }
    }

    /**
     * Convenience wrapper to show warning dialog from various places
     */
    private fun showContinueWithoutPermissionDialog() {
        val dialogListener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                BUTTON_POSITIVE -> this@PrePermissionRequestActivity.superOnBackPressed()
                BUTTON_NEGATIVE -> Unit // just close the dialog returning to the Activity
                else -> throw IllegalStateException("PrePermissionRequestActivity continue without closing dialog unexpected button $which")
            }
        }

        AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_warning_black_24dp)
                .setTitle(getString(R.string.prepermission_alert_dialog_title))
                .setMessage(R.string.prepermission_alert_dialog_message)
                .setPositiveButton(getString(R.string.prepermission_alert_dialog_positive_button_caption), dialogListener)
                .setNegativeButton(getString(R.string.prepermission_alert_dialog_negative_button_caption), dialogListener)
                .show()
    }

    /**
     * Simple PagerAdapter to display Fragments as swipeable pages within this Activity
     */
    private class PrePermissionRequestPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        private val pages = arrayOf(
                PrePermissionRequestActivity.PrePermissionRequestFragment.createPrePermissionRequestFragment(R.layout.prepermissionrequest_page1_fragment_layout),
                PrePermissionRequestActivity.PrePermissionRequestFragment.createPrePermissionRequestFragment(R.layout.prepermissionrequest_page2_fragment_layout),
                PrePermissionRequestActivity.PrePermissionRequestFragment.createPrePermissionRequestFragment(R.layout.prepermissionrequest_page3_fragment_layout)
        )

        override fun getCount(): Int = pages.size
        override fun getItem(position: Int) = pages[position]
    }

    /**
     * Simple Fragment to display swipeable pages for each step within PrePermissionRequestActivity
     */
    class PrePermissionRequestFragment : android.support.v4.app.Fragment() {
        private var layout: Int? = null
        private lateinit var activity: PrePermissionRequestActivity
        private lateinit var v: View

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            if (layout == null)
                throw IllegalArgumentException("PrePermissionRequestFragment must have layout passed as an argument")
            else {
                activity = getActivity() as PrePermissionRequestActivity
                v = inflater.inflate(layout!!, container, false)
                v.prepermission_phone_button?.setOnClickListener(activity)
                v.usage_grant_permission_button?.setOnClickListener(activity)

                updateLayoutForPermission()

                return v
            }
        }

        /**
         * Toggle various Views' visibility depending on permission state
         */
        private fun updateLayoutForPermission() {
            when (layout) {
                R.layout.prepermissionrequest_page2_fragment_layout -> {
                    v.prepermission_phone_button.visibility = if (activity.perm.havePhonePermission) INVISIBLE else VISIBLE
                    v.prepermission_granted_text_p2.visibility = if (activity.perm.havePhonePermission) VISIBLE else INVISIBLE
                }
                R.layout.prepermissionrequest_page3_fragment_layout -> {
                    v.usage_grant_permission_button.visibility = if (activity.perm.haveUsagePermission) INVISIBLE else VISIBLE
                    v.prepermission_granted_text_p3.visibility = if (activity.perm.haveUsagePermission) VISIBLE else INVISIBLE
                }
                else -> Unit
            }
        }

        override fun onResume() {
            super.onResume()
            updateLayoutForPermission()
        }

        override fun setArguments(args: Bundle?) {
            super.setArguments(args)
            layout = args?.getInt("layout")
        }

        /**
         * Convenience factory method to fake a constructor with arguments
         */
        companion object {
            fun createPrePermissionRequestFragment(layout: Int): PrePermissionRequestFragment {
                val f = PrePermissionRequestFragment()
                val b = Bundle()
                b.putInt("layout", layout)

                f.arguments = b
                return f
            }
        }
    }

}
