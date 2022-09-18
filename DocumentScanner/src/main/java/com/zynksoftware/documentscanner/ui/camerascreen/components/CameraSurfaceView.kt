package com.zynksoftware.documentscanner.ui.camerascreen.components

import androidx.annotation.StringRes
import com.zynksoftware.documentscanner.model.DocumentScannerErrorModel

interface CameraSurfaceView {
    fun showProgressBar()
    fun hideProgressBar()
    fun startCroppingProcess()
    fun onError(error: DocumentScannerErrorModel)
    fun getString(@StringRes stringRes: Int): String
}
