package  com.example.dever.nonUglyDataUsageWidget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView

class PrePermissionRequestActivity : AppCompatActivity() {

    private lateinit var imagePhonePermTick: ImageView
    private lateinit var imageUsagePermTick: ImageView
    private val MYPERMREQREADPHONESTATE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_pre_permission_request)
        imagePhonePermTick = findViewById(R.id.image_phone_permission_tick)
        imageUsagePermTick = findViewById(R.id.image_usage_permission_tick)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onResume() {
        super.onResume()

        imageUsagePermTick.visibility = if (PermissionChecker.haveUsagePermission(this)) View.VISIBLE else View.INVISIBLE
        imagePhonePermTick.visibility = if (PermissionChecker.havePhoneStatePermission(this)) View.VISIBLE else View.INVISIBLE
    }

    fun requestPhoneStatePermission(view: View) {
        if (!PermissionChecker.havePhoneStatePermission(this))
            this.requestPermissions(arrayOf(android.Manifest.permission.READ_PHONE_STATE), MYPERMREQREADPHONESTATE)
    }

    fun requestUsagePermission(view: View) {
        if (!PermissionChecker.haveUsagePermission(this)) startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
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
