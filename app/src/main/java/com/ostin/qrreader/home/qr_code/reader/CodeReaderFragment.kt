package com.ostin.qrreader.home.qr_code.reader

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.ostin.qrreader.databinding.FragmentCodeReaderBinding
import com.ostin.qrreader.home.MainActivity
import com.ostin.qrreader.util.code_reader.CodeReader
import com.ostin.qrreader.util.code_reader.CodeReaderInterface
import com.ostin.qrreader.util.permission.PermStatus

class CodeReaderFragment : Fragment(),
    CodeReaderInterface {
    private var binding: FragmentCodeReaderBinding? = null
    private lateinit var appContext: Context

    private val viewModel: CodeReaderViewModel by viewModels()
    private lateinit var codeReader: CodeReader

    private val requestCameraPermId = 1001

    private var cameraObserver = Observer<PermStatus> { status ->
        binding!!.hasCameraPermission = when (status) {
            is PermStatus.Granted -> {
                codeReader.startReader(binding!!.cameraPreview.holder)
                enableFlashlightBtn()
                disableAllowCameraPermBtn()
                true
            }
            is PermStatus.Denied -> {
                codeReader.stopReader()
                setupAllowCameraPermBtn()
                false
            }
        }
    }

    private var storageObserver = Observer<PermStatus> { status ->
        binding!!.hasStoragePermission = when (status) {
            is PermStatus.Granted -> {
                true
            }
            is PermStatus.Denied -> {
                false
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appContext = activity!!.applicationContext
        codeReader =
            CodeReader(appContext, this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCodeReaderBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.init(viewLifecycleOwner, cameraObserver, storageObserver)
    }

    override fun onCodeValueDetected(codeValue: String) {
        viewModel.onCodeValueDetected(codeValue)

        requireActivity().runOnUiThread {
            kotlin.run {
                binding!!.barCodeValue.text = codeValue
            }
        }
    }

    override fun onCodeImgPathDetected(codeImgPath: String) {
        viewModel.onCodeImgPathDetected(codeImgPath)
        Glide.with(appContext)
            .load(codeImgPath)
            .into(binding!!.codeImg)
        binding!!.codeDetected = true
    }

    private fun setupAllowCameraPermBtn() {
        binding!!.allowCameraPermission.setOnClickListener {
            (activity as MainActivity).requestPermission(
                requestCameraPermId,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }

    private fun enableFlashlightBtn() {
        binding!!.flashLight.setOnCheckedChangeListener { buttonView, isChecked ->
            when (isChecked) {
                true -> codeReader.enableFlashLight()
                false -> codeReader.disableFlashlight()
            }
        }
    }

    private fun disableAllowCameraPermBtn() {
        binding!!.allowCameraPermission.setOnClickListener { null }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        codeReader.stopReader()
    }
}