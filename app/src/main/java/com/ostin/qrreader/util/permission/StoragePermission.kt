package com.ostin.qrreader.util.permission

import android.Manifest
import android.content.Context

class StoragePermission(context: Context) : PermStatusListener(context,
    Manifest.permission.WRITE_EXTERNAL_STORAGE)