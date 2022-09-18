package com.zynksoftware.documentscanner.ui.camerascreen.components

import androidx.core.view.isVisible
import com.zynksoftware.documentscanner.R
import com.zynksoftware.documentscanner.databinding.FragmentCameraScreenBinding
import com.zynksoftware.documentscanner.model.DocumentScannerErrorModel
import com.zynksoftware.documentscanner.ui.components.scansurface.ScanSurfaceListener

internal class CameraScanSurfaceViewListener(
    private val view: CameraSurfaceView,
    private val binding: FragmentCameraScreenBinding,
) : ScanSurfaceListener {

    override fun scanSurfaceShowProgress() {
        view.showProgressBar()
    }

    override fun scanSurfaceHideProgress() {
        view.hideProgressBar()
    }

    override fun onError(error: DocumentScannerErrorModel) {
        view.onError(error)
    }

    override fun showFlash() {
        binding.flashButton.isVisible = true
    }

    override fun hideFlash() {
        binding.flashButton.isVisible = false
    }

    override fun showFlashModeOn() {
        binding.flashButton.setImageResource(R.drawable.zdc_flash_on)
    }

    override fun showFlashModeOff() {
        binding.flashButton.setImageResource(R.drawable.zdc_flash_off)
    }

    override fun scanSurfacePictureTaken() {
        view.startCroppingProcess()
    }
}
