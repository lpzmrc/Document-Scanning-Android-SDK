package com.zynksoftware.documentscanner.ui.camerascreen.components

import com.zynksoftware.documentscanner.R
import com.zynksoftware.documentscanner.databinding.FragmentCameraScreenBinding

class CameraSurfaceViewController(
    private val view: CameraSurfaceView,
    private val binding: FragmentCameraScreenBinding,
) {
    internal fun takePhoto() {
        binding.scanSurfaceView.takePicture()
    }

    internal fun switchFlashState() {
        binding.scanSurfaceView.switchFlashState()
    }

    internal fun toggleAutoManualButton() {
        binding.run {
            scanSurfaceView.isAutoCaptureOn = !scanSurfaceView.isAutoCaptureOn
            if (scanSurfaceView.isAutoCaptureOn) {
                autoButton.text = view.getString(R.string.zdc_auto)
            } else {
                autoButton.text = view.getString(R.string.zdc_manual)
            }
        }
    }

    internal fun startCamera() {
        binding.scanSurfaceView.start()
    }
}
