package com.zynksoftware.documentscannersample

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment.DIRECTORY_DCIM
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.zynksoftware.documentscanner.ScanActivity
import com.zynksoftware.documentscanner.model.DocumentScannerErrorModel
import com.zynksoftware.documentscanner.model.ScannerResults
import com.zynksoftware.documentscannersample.adapters.ImageAdapter
import com.zynksoftware.documentscannersample.adapters.ImageAdapterListener
import com.zynksoftware.documentscannersample.databinding.AppScanActivityLayoutBinding
import com.zynksoftware.documentscannersample.ktx.BYTE_SCALE
import com.zynksoftware.documentscannersample.ktx.checkForStoragePermissions
import com.zynksoftware.documentscannersample.ktx.sizeInMb
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * A [ScanActivity] implementation for the app.
 */
class AppScanActivity : ScanActivity(), ImageAdapterListener {

    private var _binding: AppScanActivityLayoutBinding? = null
    private val binding: AppScanActivityLayoutBinding
        get() = _binding!!
    private var alertDialogBuilder: AlertDialog.Builder? = null
    private var alertDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(
            AppScanActivityLayoutBinding.inflate(layoutInflater).apply {
                _binding = this
            }.root
        )
        addFragmentContentLayout()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onError(error: DocumentScannerErrorModel) {
        showAlertDialog(getString(R.string.error_label), error.errorMessage?.error, getString(R.string.ok_label))
    }

    override fun onSuccess(scannerResults: ScannerResults) {
        initViewPager(scannerResults)
    }

    override fun onClose() {
        Log.d(TAG, "onClose")
        finish()
    }

    override fun onSaveButtonClicked(image: File) {
        checkForStoragePermissions(image)
    }

    internal fun saveImage(image: File) {
        showProgressBar()

        val date = Date()
        val formatter = SimpleDateFormat("dd_MM_yyyy_HH_mm_ss:mm", Locale.getDefault())
        val dateFormatted = formatter.format(date)

        val to = File(getExternalFilesDir(DIRECTORY_DCIM), "zynkphoto$dateFormatted.jpg")

        val inputStream: InputStream = FileInputStream(image)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver: ContentResolver = contentResolver
            val contentValues = ContentValues()
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "zynkphoto$dateFormatted.jpg")
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/*")
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM")
            val imageUri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            val out = resolver.openOutputStream(imageUri!!)
            out?.write(image.readBytes())
            out?.flush()
            out?.close()
        } else {
            val out: OutputStream = FileOutputStream(to)

            val buf = ByteArray(BYTE_SCALE)
            var len: Int
            while (inputStream.read(buf).also { len = it } > 0) {
                out.write(buf, 0, len)
            }
            inputStream.close()
            out.flush()
            out.close()
        }

        hideProgessBar()
        showAlertDialog(getString(R.string.photo_saved), "", getString(R.string.ok_label))
    }

    private fun showProgressBar() {
        binding.progressLayoutApp.isVisible = true
    }

    private fun hideProgessBar() {
        binding.progressLayoutApp.isVisible = false
    }

    private fun initViewPager(scannerResults: ScannerResults) {
        val fileList = ArrayList<File>()

        scannerResults.originalImageFile?.let {
            Log.d(TAG, "ZDCoriginalPhotoFile size ${it.sizeInMb}")
        }

        scannerResults.croppedImageFile?.let {
            Log.d(TAG, "ZDCcroppedPhotoFile size ${it.sizeInMb}")
        }

        scannerResults.transformedImageFile?.let {
            Log.d(TAG, "ZDCtransformedPhotoFile size ${it.sizeInMb}")
        }

        scannerResults.originalImageFile?.let { fileList.add(it) }
        scannerResults.transformedImageFile?.let { fileList.add(it) }
        scannerResults.croppedImageFile?.let { fileList.add(it) }
        val targetAdapter = ImageAdapter(this, fileList, this)
        binding.run {
            viewPagerTwo.adapter = targetAdapter
            viewPagerTwo.isUserInputEnabled = false

            previousButton.setOnClickListener {
                viewPagerTwo.currentItem = viewPagerTwo.currentItem - 1
                nextButton.isVisible = true
                if (viewPagerTwo.currentItem == 0) {
                    previousButton.isVisible = false
                }
            }

            nextButton.setOnClickListener {
                viewPagerTwo.currentItem = viewPagerTwo.currentItem + 1
                previousButton.isVisible = true
                if (viewPagerTwo.currentItem == fileList.size - 1) {
                    nextButton.isVisible = false
                }
            }
        }
    }

    private fun showAlertDialog(title: String?, message: String?, buttonMessage: String) {
        alertDialogBuilder = AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(buttonMessage) { _, _ ->
            }
        alertDialog?.dismiss()
        alertDialog = alertDialogBuilder?.create()
        alertDialog?.setCanceledOnTouchOutside(false)
        alertDialog?.show()
    }

    companion object {
        private val TAG = AppScanActivity::class.simpleName
        fun start(context: Context) {
            val intent = Intent(context, AppScanActivity::class.java)
            context.startActivity(intent)
        }
    }
}
