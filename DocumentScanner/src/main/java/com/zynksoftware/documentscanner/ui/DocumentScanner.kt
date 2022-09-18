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

package com.zynksoftware.documentscanner.ui

import android.content.Context
import android.graphics.Bitmap
import com.zynksoftware.documentscanner.manager.SessionManager
import com.zynksoftware.documentscanner.ui.DocumentScanner.Configuration.Companion.MAX_IMAGE_QUALITY
import com.zynksoftware.documentscanner.ui.DocumentScanner.Configuration.Companion.MIN_IMAGE_QUALITY

object DocumentScanner {

    fun init(context: Context, configuration: Configuration = Configuration()) {
        System.loadLibrary("opencv_java4")
        val sessionManager = SessionManager(context)
        if (configuration.imageQuality in MIN_IMAGE_QUALITY..MAX_IMAGE_QUALITY) {
            sessionManager.imageQuality = configuration.imageQuality
        }
        sessionManager.imageSize = configuration.imageSize
        sessionManager.imageType = configuration.imageType
    }

    data class Configuration(
        val imageQuality: Int = DEFAULT_IMAGE_QUALITY,
        val imageSize: Long = -1,
        val imageType: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
    ) {
        companion object {
            const val MIN_IMAGE_QUALITY = 1
            const val MAX_IMAGE_QUALITY = 100
            const val DEFAULT_IMAGE_QUALITY: Int = MAX_IMAGE_QUALITY
        }
    }
}
