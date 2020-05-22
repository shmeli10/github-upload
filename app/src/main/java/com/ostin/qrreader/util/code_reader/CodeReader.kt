package com.ostin.qrreader.util.code_reader

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.hardware.Camera
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.SurfaceHolder
import androidx.core.app.ActivityCompat
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import java.io.IOException
import java.lang.reflect.Field

class CodeReader(private val context: Context, private val callback: CodeReaderInterface) {
    private lateinit var barcodeDetector: BarcodeDetector
    lateinit var cameraSource: CameraSource
    private var camera : Camera? = null

    private var canDetectCode = true
    var isCameraStarted = false

    fun startReader(surfaceHolder: SurfaceHolder) {
        if (!isCameraStarted) {
            setupBarCodeDetector()
            setupCameraSource()
            setupCamera()
            cameraStart(surfaceHolder)
            setBarCodeProcessor()
        }
    }

    private fun setupBarCodeDetector() {
        barcodeDetector = BarcodeDetector.Builder(context)
            .setBarcodeFormats(Barcode.QR_CODE)
            .build()
    }

    private fun setupCameraSource() {
        if (isBarCodeDetectorReady()) {
            cameraSource = CameraSource.Builder(context, barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(640, 480)
                .setRequestedFps(15.0f)
                .setAutoFocusEnabled(true)
                .build()
        }
    }

    private fun setupCamera() {
        camera = getCamera()
        disableFlashlight()
    }

    private fun cameraStart(surfaceHolder: SurfaceHolder) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) !=
            PackageManager.PERMISSION_GRANTED
        ) return

        try {
            cameraSource.start(surfaceHolder)
            isCameraStarted = true
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun setBarCodeProcessor() {
        if (isBarCodeDetectorReady()) {
            barcodeDetector.setProcessor(object : Detector.Processor<Barcode> {
                override fun release() {}
                override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                    if (canDetectCode()) {
                        val qrCodes = detections.detectedItems
                        if (qrCodes.size() != 0) {
                            callback.onCodeValueDetected(qrCodes.valueAt(0).displayValue)
                            getCodeImg()
                        }
                    }
                }
            })
        }
    }

    fun getCodeImg() {
        val shutterCallback = CameraSource.ShutterCallback { }
        val pictureCallback = CameraSource.PictureCallback { arg0 ->
            val matrix = Matrix()
            matrix.postRotate(90f)
            val bitmapRaw = BitmapFactory.decodeByteArray(arg0, 0, arg0.size)
            val codeImg = Bitmap.createBitmap(
                bitmapRaw,
                0,
                0,
                bitmapRaw.getWidth(),
                bitmapRaw.getHeight(),
                matrix,
                true
            )

            try {
                saveCodeImg(codeImg)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        cameraSource.takePicture(shutterCallback, pictureCallback) //.toString()
    }

    private fun saveCodeImg(codeImg: Bitmap) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, System.currentTimeMillis().toString())
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
        }

        val resolver = context.contentResolver
        var uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        try {
            uri?.let { uri ->
                val stream = resolver.openOutputStream(uri)

                stream?.let { stream ->
                    if (!codeImg.compress(Bitmap.CompressFormat.JPEG, 80, stream)) {
                        throw IOException("Failed to save bitmap.")
                    }
                } ?: throw IOException("Failed to get output stream.")
            } ?: throw IOException("Failed to create new MediaStore record")
        } catch (e: IOException) {
            if (uri != null) {
                resolver.delete(uri, null, null)
                uri = null
            }
            throw IOException(e)
        } finally {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)

            if (uri != null) {
                callback.onCodeImgPathDetected(uri.toString())
            }
        }
    }

    private fun canDetectCode(): Boolean = canDetectCode

    private fun enableCodeDetection() {
        canDetectCode = true
    }

    private fun isBarCodeDetectorReady(): Boolean {
        return barcodeDetector!!.isOperational
    }

    fun enableFlashLight() {
        setFlashlightMode(Camera.Parameters.FLASH_MODE_TORCH)
    }

    fun disableFlashlight() {
        setFlashlightMode(Camera.Parameters.FLASH_MODE_OFF)
    }

    private fun setFlashlightMode(flashMode: String) {
        if (camera != null) {
            try {
                val param: Camera.Parameters = camera!!.getParameters()
                param.flashMode = flashMode
                camera!!.setParameters(param)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getCamera(): Camera? {
        val declaredFields: Array<Field> = CameraSource::class.java.declaredFields
        for (field in declaredFields) {
            if (field.type === Camera::class.java) {
                field.isAccessible = true

                try {
                    // return field.get(cameraSource) as Camera
                    if(field.get(cameraSource) != null) {
                        return field.get(cameraSource) as Camera
                    }
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                }
                break
            }
        }
        return null
    }

    fun stopReader() {
        if (isCameraStarted) {
            disableCodeDetection()
            releaseBarCodeDetector()
            releaseCamera()
        }
    }

    private fun disableCodeDetection() {
        canDetectCode = false
    }

    private fun releaseBarCodeDetector() {
        if (isBarCodeDetectorReady()) {
            barcodeDetector.release()
        }
    }

    private fun releaseCamera() {
        cameraSource.release()
    }
}