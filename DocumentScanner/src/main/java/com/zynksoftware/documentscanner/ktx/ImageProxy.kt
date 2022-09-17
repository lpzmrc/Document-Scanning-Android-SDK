package com.zynksoftware.documentscanner.ktx

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.common.internal.ImageConvertUtils

val ImageProxy.bitmap: Bitmap?
    @SuppressLint("UnsafeOptInUsageError")
    get() = image?.let { image ->
        ImageConvertUtils.getInstance().getUpRightBitmap(InputImage.fromMediaImage(image, 0))
    }
