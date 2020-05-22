package com.ostin.qrreader.util.permission

import android.Manifest
import android.content.Context

class CameraPermission(context: Context) : PermStatusListener(context,
    Manifest.permission.CAMERA)