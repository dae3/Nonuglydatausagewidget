package com.example.dever.nonUglyDataUsageWidget

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import java.text.NumberFormat

class MainActivity : AppCompatActivity() {

    private lateinit var mTextData : TextView
    private var nf = NumberFormat.getNumberInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mTextData = findViewById(R.id.txtDataUsed)
    }

    override fun onResume() {
        super.onResume()

        if (PermissionChecker.haveUsagePermission(this) && PermissionChecker.havePhoneStatePermission(this)) {
            var d : Float = GetNetworkStats(this).getNetworkStats().toFloat()
            mTextData.text = "${nf.format(d / 1024 / 1024)} MB"
        } else {
            startActivity(Intent(this, PrePermissionRequestActivity::class.java))
        }
    }

}
