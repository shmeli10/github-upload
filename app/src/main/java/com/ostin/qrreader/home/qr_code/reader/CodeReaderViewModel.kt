package com.ostin.qrreader.home.qr_code.reader

import android.app.Application
import android.content.Context
import android.os.Vibrator
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.ostin.qrreader.util.permission.CameraPermission
import com.ostin.qrreader.util.permission.PermStatus
import com.ostin.qrreader.util.permission.StoragePermission

class CodeReaderViewModel(application: Application) : AndroidViewModel(application) {
    private val appContext: Context = application
    private val permListeners = listOf(
        CameraPermission(appContext),
        StoragePermission(appContext)
    )

    private val vibrator: Vibrator by lazy {
        appContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    var codeValue: String? = null
    var codeImgPath: String? = null

    fun init(owner: LifecycleOwner, cameraObserver: Observer<PermStatus>,
             storageObserver: Observer<PermStatus>) {
        for (listener in permListeners) {
            when (listener) {
                is CameraPermission -> listener.observe(owner, cameraObserver)
                is StoragePermission -> listener.observe(owner, storageObserver)
            }
            listener.handlePermissionCheck()
        }
    }

    fun onCodeValueDetected(codeValue: String) {
        this.codeValue = codeValue
        vibrate()
    }

    fun onCodeImgPathDetected(codeImgPath: String) {
        this.codeImgPath = codeImgPath
    }

    private fun vibrate() = vibrator.vibrate(100)
}