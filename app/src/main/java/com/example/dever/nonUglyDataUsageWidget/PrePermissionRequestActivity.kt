package  com.example.dever.nonUglyDataUsageWidget

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.TextView

class PrePermissionRequestActivity : Activity() {

    private lateinit var mTextViewUsage: TextView
    private lateinit var mTextViewPhone : TextView
    private val MYPERMREQ_READ_PHONE_STATE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_pre_permission_request)
        mTextViewUsage = findViewById(R.id.txtUsagePermission)
        mTextViewPhone = findViewById(R.id.txtPhonePermission)
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>?, grantResults: IntArray?) {
//        if (requestCode == MYPERMREQ_READ_PHONE_STATE) haveReadPhoneStatePermission = grantResults?.get(0) == PERMISSION_GRANTED
    }
}
