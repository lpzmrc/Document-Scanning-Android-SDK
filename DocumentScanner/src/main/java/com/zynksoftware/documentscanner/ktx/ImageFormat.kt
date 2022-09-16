package com.zynksoftware.documentscanner.ktx

import android.graphics.ImageFormat

fun Int.toImageFormatString(): String {
    return when (this) {
        ImageFormat.JPEG -> "ImageFormat.JPEG"
        ImageFormat.HEIC -> "ImageFormat.HEIC"
        ImageFormat.FLEX_RGBA_8888 -> "ImageFormat.FLEX_RGBA_8888"
        ImageFormat.FLEX_RGB_888 -> "ImageFormat.FLEX_RGB_888"
        ImageFormat.NV16 -> "ImageFormat.NV16"
        ImageFormat.NV21 -> "ImageFormat.NV21"
        ImageFormat.PRIVATE -> "ImageFormat.PRIVATE"
        ImageFormat.RAW10 -> "ImageFormat.RAW10"
        ImageFormat.RAW12 -> "ImageFormat.RAW12"
        ImageFormat.RAW_PRIVATE -> "ImageFormat.RAW_PRIVATE"
        ImageFormat.RAW_SENSOR -> "ImageFormat.RAW_SENSOR"
        ImageFormat.RGB_565 -> "ImageFormat.RGB_565"
        ImageFormat.UNKNOWN -> "ImageFormat.UNKNOWN"
        ImageFormat.YUV_420_888 -> "ImageFormat.YUV_420_888"
        ImageFormat.YUV_422_888 -> "ImageFormat.YUV_422_888"
        ImageFormat.YUV_444_888 -> "ImageFormat.YUV_444_888"
        ImageFormat.Y8 -> "ImageFormat.Y8"
        ImageFormat.YUY2 -> "ImageFormat.YUY2"
        ImageFormat.YV12 -> "ImageFormat.YV12"
        else -> "WTF"
    }.plus(" $this")
}