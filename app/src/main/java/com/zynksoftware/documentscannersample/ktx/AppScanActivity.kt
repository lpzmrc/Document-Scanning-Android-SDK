package com.zynksoftware.documentscannersample.ktx

import android.Manifest
import com.tbruyelle.rxpermissions3.RxPermissions
import com.zynksoftware.documentscanner.model.DocumentScannerErrorModel
import com.zynksoftware.documentscanner.model.DocumentScannerErrorModel.ErrorMessage
import com.zynksoftware.documentscannersample.AppScanActivity
import java.io.File

internal fun AppScanActivity.checkForStoragePermissions(image: File) {
    RxPermissions(this)
        .requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
        .subscribe { permission ->
            when {
                permission.granted -> saveImage(image = image)
                permission.shouldShowRequestPermissionRationale -> onError(
                    error = DocumentScannerErrorModel(
                        errorMessage = ErrorMessage.STORAGE_PERMISSION_REFUSED_WITHOUT_NEVER_ASK_AGAIN
                    )
                )
                else -> onError(
                    error = DocumentScannerErrorModel(
                        errorMessage = ErrorMessage.STORAGE_PERMISSION_REFUSED_GO_TO_SETTINGS
                    )
                )
            }
        }
}
