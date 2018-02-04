package  com.example.dever.nonUglyDataUsageWidget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView

class PrePermissionRequestActivity : AppCompatActivity() {

    private lateinit var mTextViewUsage: TextView
    private lateinit var mTextViewPhone: TextView
    private val MYPERMREQ_READ_PHONE_STATE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_pre_permission_request)
        mTextViewUsage = findViewById(R.id.txtUsagePermission)
        mTextViewPhone = findViewById(R.id.txtPhonePermission)

        setSupportActionBar(findViewById(R.id.prepermreqestactivity_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onResume() {
        super.onResume()

        mTextViewUsage.text = resources.getString(
                if (PermissionChecker.haveUsagePermission(this)) R.string.have_permissions_text else R.string.havent_permission_text
        )
        mTextViewPhone.text = if (PermissionChecker.havePhoneStatePermission(this)) "good" else "bad"
    }

    fun onContinueClick(@Suppress("UNUSED_PARAMETER") v: View) {
        if (!PermissionChecker.haveUsagePermission(this)) startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        if (!PermissionChecker.havePhoneStatePermission(this)) if (!PermissionChecker.havePhoneStatePermission(this)) this.requestPermissions(arrayOf(android.Manifest.permission.READ_PHONE_STATE), MYPERMREQ_READ_PHONE_STATE)
    }

    override fun onStop() {
        // let any widgets know perms may have changed
        super.onStop()
        val i = Intent()
        i.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        i.putExtra(
                Widget.WIDGET_IDS_KEY,
                AppWidgetManager.getInstance(this).getAppWidgetIds(ComponentName(this, Widget::class.java))
        )
        this.sendBroadcast(i)
    }
}
