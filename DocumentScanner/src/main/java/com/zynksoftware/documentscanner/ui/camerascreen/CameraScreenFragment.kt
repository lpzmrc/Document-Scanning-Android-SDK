/**
Copyright 2020 ZynkSoftware SRL

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
associated documentation files (the "Software"), to deal in the Software without restriction,
including without limitation the rights to use, copy, modify, merge, publish, distribute,
sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or
substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.zynksoftware.documentscanner.ui.camerascreen

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import com.zynksoftware.documentscanner.common.utils.FileUriUtils
import com.zynksoftware.documentscanner.databinding.FragmentCameraScreenBinding
import com.zynksoftware.documentscanner.model.DocumentScannerErrorModel
import com.zynksoftware.documentscanner.model.DocumentScannerErrorModel.ErrorMessage
import com.zynksoftware.documentscanner.ui.base.BaseFragment
import com.zynksoftware.documentscanner.ui.camerascreen.components.CameraScanSurfaceViewListener
import com.zynksoftware.documentscanner.ui.camerascreen.components.CameraSurfaceView
import com.zynksoftware.documentscanner.ui.camerascreen.components.CameraSurfaceViewController
import com.zynksoftware.documentscanner.ui.components.scansurface.ScanSurfaceListener
import com.zynksoftware.documentscanner.ui.scan.InternalScanActivity
import java.io.File
import java.io.FileNotFoundException

/**
 * The camera screen [BaseFragment].
 *
 * Users can:
 *
 * - capture an image to crop it: live edge detection included.
 * - open the gallery to select an image to crop it.
 * - navigate back to the previous screen.
 */
internal class CameraScreenFragment : BaseFragment(), CameraSurfaceView {

    private var _scanSurfaceViewListener: ScanSurfaceListener? = null
    private val scanSurfaceViewListener: ScanSurfaceListener
        get() = _scanSurfaceViewListener!!

    private var _binding: FragmentCameraScreenBinding? = null
    private val binding
        get() = _binding!!

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted: Boolean ->
        if (granted) {
            cameraSurfaceViewController.startCamera()
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                scanSurfaceViewListener.onError(
                    error = DocumentScannerErrorModel(
                        errorMessage = ErrorMessage.CAMERA_PERMISSION_REFUSED_WITHOUT_NEVER_ASK_AGAIN
                    )
                )
            } else {
                scanSurfaceViewListener.onError(
                    error = DocumentScannerErrorModel(
                        errorMessage = ErrorMessage.CAMERA_PERMISSION_REFUSED_GO_TO_SETTINGS
                    )
                )
            }
        }
    }

    private val storagePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted: Boolean ->
        if (granted) {
            galleryActivityResultLauncher.launch(galleryPickerIntent)
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                scanSurfaceViewListener.onError(
                    error = DocumentScannerErrorModel(
                        errorMessage = ErrorMessage.STORAGE_PERMISSION_REFUSED_WITHOUT_NEVER_ASK_AGAIN
                    )
                )
            } else {
                scanSurfaceViewListener.onError(
                    error = DocumentScannerErrorModel(
                        errorMessage = ErrorMessage.STORAGE_PERMISSION_REFUSED_GO_TO_SETTINGS
                    )
                )
            }
        }
    }

    private val galleryPickerIntent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
        addCategory(Intent.CATEGORY_OPENABLE)
        type = "image/*"
    }

    private val galleryActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            try {
                val imageUri = result.data?.data
                if (imageUri != null) {
                    val realPath = FileUriUtils.getRealPath(getScanActivity(), imageUri)
                    if (realPath != null) {
                        getScanActivity().reInitOriginalImageFile()
                        getScanActivity().originalImageFile = File(realPath)
                        startCroppingProcess()
                    } else {
                        Log.e(TAG, ErrorMessage.TAKE_IMAGE_FROM_GALLERY_ERROR.error)
                        scanSurfaceViewListener.onError(
                            DocumentScannerErrorModel(
                                ErrorMessage.TAKE_IMAGE_FROM_GALLERY_ERROR, null
                            )
                        )
                    }
                } else {
                    Log.e(TAG, ErrorMessage.TAKE_IMAGE_FROM_GALLERY_ERROR.error)
                    scanSurfaceViewListener.onError(
                        DocumentScannerErrorModel(
                            ErrorMessage.TAKE_IMAGE_FROM_GALLERY_ERROR, null
                        )
                    )
                }
            } catch (expected: FileNotFoundException) {
                Log.e(TAG, "FileNotFoundException", expected)
                scanSurfaceViewListener.onError(
                    DocumentScannerErrorModel(
                        ErrorMessage.TAKE_IMAGE_FROM_GALLERY_ERROR, expected
                    )
                )
            }
        }
    }

    private var _cameraSurfaceViewController: CameraSurfaceViewController? = null
    private val cameraSurfaceViewController: CameraSurfaceViewController
        get() = _cameraSurfaceViewController!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return FragmentCameraScreenBinding.inflate(layoutInflater).apply {
            _binding = this
            _scanSurfaceViewListener = CameraScanSurfaceViewListener(this@CameraScreenFragment, binding)
            _cameraSurfaceViewController = CameraSurfaceViewController(this@CameraScreenFragment, binding)
        }.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _scanSurfaceViewListener = null
        _cameraSurfaceViewController = null
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            scanSurfaceView.lifecycleOwner = viewLifecycleOwner
            scanSurfaceView.listener = scanSurfaceViewListener
            scanSurfaceView.originalImageFile = getScanActivity().originalImageFile
        }

        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        initListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (getScanActivity().shouldCallOnClose) {
            getScanActivity().onClose()
        }
    }

    override fun onResume() {
        super.onResume()
        getScanActivity().reInitOriginalImageFile()
        binding.scanSurfaceView.originalImageFile = getScanActivity().originalImageFile
    }

    private fun initListeners() {
        binding.run {
            cameraCaptureButton.setOnClickListener {
                cameraSurfaceViewController.takePhoto()
            }
            cancelButton.setOnClickListener {
                finishActivity()
            }
            flashButton.setOnClickListener {
                cameraSurfaceViewController.switchFlashState()
            }
            galleryButton.setOnClickListener {
                storagePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            autoButton.setOnClickListener {
                cameraSurfaceViewController.toggleAutoManualButton()
            }
        }
    }

    private fun getScanActivity(): InternalScanActivity = requireActivity() as InternalScanActivity

    private fun finishActivity() {
        getScanActivity().finish()
    }

    override fun startCroppingProcess() {
        if (isAdded) {
            getScanActivity().showImageCropFragment()
        }
    }

    override fun onError(error: DocumentScannerErrorModel) {
        run {
            if (isAdded) {
                getScanActivity().onError(error)
            }
        }
    }

    companion object {
        internal val TAG = CameraScreenFragment::class.simpleName

        fun newInstance(): CameraScreenFragment {
            return CameraScreenFragment()
        }
    }
}
