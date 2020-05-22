package com.ostin.qrreader.util.permission

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import com.ostin.qrreader.R

open class PermStatusListener(private val context: Context,
                              private val permission: String): LiveData<PermStatus>() {
    fun handlePermissionCheck() =
        if (isGranted()) {
            postValue(PermStatus.Granted())
        } else {
            postValue(PermStatus.Denied())
        }

    private fun isGranted() : Boolean {
        return ActivityCompat.checkSelfPermission(context, permission) ==
                PackageManager.PERMISSION_GRANTED
    }
}

sealed class PermStatus {
    data class Granted(val message: Int = R.string.permission_status_granted) : PermStatus()
    data class Denied(val message: Int = R.string.permission_status_denied) : PermStatus()
}