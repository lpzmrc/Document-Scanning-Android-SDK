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

package com.zynksoftware.documentscanner.manager

import android.content.Context
import android.graphics.Bitmap
import com.zynksoftware.documentscanner.ui.DocumentScanner
import id.zelory.compressor.extension
import java.util.Locale

internal class SessionManager(context: Context) {

    private val preferences = context.getSharedPreferences("ZDC_Shared_Preferences", Context.MODE_PRIVATE)

    var imageSize: Long
        get() = preferences.getLong(IMAGE_SIZE_KEY, -1L)
        set(value) {
            preferences.edit().putLong(IMAGE_SIZE_KEY, value).apply()
        }

    var imageQuality: Int
        get() = preferences.getInt(IMAGE_QUALITY_KEY, 100)
        set(value) {
            preferences.edit().putInt(IMAGE_QUALITY_KEY, value).apply()
        }

    var imageType: Bitmap.CompressFormat
        get() {
            return compressFormat(preferences.getString(IMAGE_TYPE_KEY, DEFAULT_IMAGE_TYPE)!!)
        }
        set(value) {
            preferences.edit().putString(IMAGE_TYPE_KEY, value.extension()).apply()
        }

    @Suppress("DEPRECATION")
    private fun compressFormat(format: String) = when (format.lowercase(Locale.getDefault())) {
        IMAGE_TYPE_PNG -> Bitmap.CompressFormat.PNG
        IMAGE_TYPE_WEBP -> Bitmap.CompressFormat.WEBP
        else -> Bitmap.CompressFormat.JPEG
    }

    fun toConfiguration() = DocumentScanner.Configuration(
        imageQuality = imageQuality,
        imageSize = imageSize,
        imageType = imageType,
    )

    companion object {
        private const val IMAGE_SIZE_KEY = "IMAGE_SIZE_KEY"
        private const val IMAGE_QUALITY_KEY = "IMAGE_QUALITY_KEY"
        private const val IMAGE_TYPE_KEY = "IMAGE_TYPE_KEY"

        private const val IMAGE_TYPE_JPG = "jpg"
        private const val IMAGE_TYPE_PNG = "png"
        private const val IMAGE_TYPE_WEBP = "webp"
        private const val DEFAULT_IMAGE_TYPE = IMAGE_TYPE_JPG
    }
}
