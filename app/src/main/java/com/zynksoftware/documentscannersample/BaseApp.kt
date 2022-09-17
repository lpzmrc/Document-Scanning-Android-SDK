package com.zynksoftware.documentscannersample

import android.app.Application
import android.graphics.Bitmap
import com.zynksoftware.documentscanner.ui.DocumentScanner

class BaseApp : Application() {
    override fun onCreate() {
        super.onCreate()

        val configuration = DocumentScanner.Configuration(
            imageQuality = FILE_QUALITY,
            imageType = FILE_TYPE,
        )
        DocumentScanner.init(this, configuration)
    }

    companion object {
        @Suppress("unused")
        private const val FILE_SIZE = 1_000_000L
        private const val FILE_QUALITY = 100
        private val FILE_TYPE = Bitmap.CompressFormat.JPEG
    }
}
