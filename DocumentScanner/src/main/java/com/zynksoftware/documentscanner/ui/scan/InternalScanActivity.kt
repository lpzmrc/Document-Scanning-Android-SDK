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

package com.zynksoftware.documentscanner.ui.scan

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.zynksoftware.documentscanner.R
import com.zynksoftware.documentscanner.databinding.ActivityInternalScanBinding
import com.zynksoftware.documentscanner.manager.SessionManager
import com.zynksoftware.documentscanner.model.ScannerResults
import com.zynksoftware.documentscanner.ui.DocumentScanner
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.size
import id.zelory.compressor.extension
import id.zelory.compressor.saveBitmap
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Internal abstract class contaning all the function required to implement a scanner into a [FragmentActivity].
 */
abstract class InternalScanActivity : FragmentActivity(), ScanViewListener {

    private var _binding: ActivityInternalScanBinding? = null
    private val binding
        get() = _binding!!
    private val navHostFragment
        get() = supportFragmentManager.findFragmentById(R.id.zdcContent) as NavHostFragment
    private val navController get() = navHostFragment.navController

    internal lateinit var originalImageFile: File
    private lateinit var configuration: DocumentScanner.Configuration

    internal var croppedImage: Bitmap? = null
    internal var transformedImage: Bitmap? = null
    internal var shouldCallOnClose = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configuration = SessionManager(this).toConfiguration()
        reInitOriginalImageFile()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    internal fun reInitOriginalImageFile() {
        originalImageFile = File(filesDir, "$ORIGINAL_IMAGE_NAME.${configuration.imageType.extension()}")
        originalImageFile.delete()
    }

    private fun showCameraScreen() {
        navController.navigate(R.id.cameraScreenFragment)
    }

    internal fun showImageCropFragment() {
        navController.navigate(R.id.imageCropFragment)
    }

    internal fun showImageProcessingFragment() {
        navController.navigate(R.id.imageProcessingFragment)
    }

    internal fun closeCurrentFragment() {
        supportFragmentManager.popBackStackImmediate()
    }

    internal fun finalScannerResult() {
        binding.zdcContent.isVisible = false
        compressFiles()
    }

    private fun compressFiles() {
        Log.d(TAG, "ZDCcompress starts ${System.currentTimeMillis()}")
        binding.zdcProgressView.isVisible = true
        lifecycleScope.launch(Dispatchers.IO) {
            var croppedImageFile: File? = null
            croppedImage?.let {
                croppedImageFile = File(filesDir, "$CROPPED_IMAGE_NAME.${configuration.imageType.extension()}")
                saveBitmap(it, croppedImageFile!!, configuration.imageType, configuration.imageQuality)
            }

            var transformedImageFile: File? = null
            transformedImage?.let {
                transformedImageFile = File(filesDir, "$TRANSFORMED_IMAGE_NAME.${configuration.imageType.extension()}")
                saveBitmap(it, transformedImageFile!!, configuration.imageType, configuration.imageQuality)
            }

            originalImageFile = Compressor.compress(this@InternalScanActivity, originalImageFile) {
                quality(configuration.imageQuality)
                if (configuration.imageSize != NOT_INITIALIZED) size(configuration.imageSize)
                format(configuration.imageType)
            }

            croppedImageFile = croppedImageFile?.let {
                Compressor.compress(this@InternalScanActivity, it) {
                    quality(configuration.imageQuality)
                    if (configuration.imageSize != NOT_INITIALIZED) size(configuration.imageSize)
                    format(configuration.imageType)
                }
            }

            transformedImageFile = transformedImageFile?.let {
                Compressor.compress(this@InternalScanActivity, it) {
                    quality(configuration.imageQuality)
                    if (configuration.imageSize != NOT_INITIALIZED) size(configuration.imageSize)
                    format(configuration.imageType)
                }
            }

            val scannerResults = ScannerResults(originalImageFile, croppedImageFile, transformedImageFile)
            runOnUiThread {
                binding.zdcProgressView.isVisible = false
                shouldCallOnClose = false
                shouldCallOnClose = true
                onSuccess(scannerResults)
                Log.d(TAG, "ZDCcompress ends ${System.currentTimeMillis()}")
            }
        }
    }

    internal fun addFragmentContentLayoutInternal() {
        addContentView(
            ActivityInternalScanBinding.inflate(layoutInflater).apply {
                _binding = this
            }.root,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )

        binding.zdcProgressView.isVisible = false

        showCameraScreen()
    }

    companion object {
        private val TAG = InternalScanActivity::class.simpleName
        internal const val ORIGINAL_IMAGE_NAME = "original"
        internal const val CROPPED_IMAGE_NAME = "cropped"
        internal const val TRANSFORMED_IMAGE_NAME = "transformed"
        internal const val NOT_INITIALIZED = -1L
    }
}
