package com.ostin.qrreader.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil.setContentView
import com.ostin.qrreader.R
import com.ostin.qrreader.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView<ActivityMainBinding>(this,
            R.layout.activity_main
        )
    }

    fun requestPermission(requestId: Int, vararg permissions: String) {
        ActivityCompat.requestPermissions(
            this,
            permissions,
            requestId
        )
    }
}