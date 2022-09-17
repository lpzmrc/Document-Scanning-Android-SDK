package com.zynksoftware.documentscanner.ui.scan

import com.zynksoftware.documentscanner.model.DocumentScannerErrorModel
import com.zynksoftware.documentscanner.model.ScannerResults

/**
 * Interface definition for the callbacks/functions to be invoked when a scan view event occurs.
 */
interface ScanViewListener {

    /**
     * Called when a scan view error occurs.
     */
    fun onError(error: DocumentScannerErrorModel)

    /**
     * Called when a scan result occurs.
     */
    fun onSuccess(scannerResults: ScannerResults)

    /**
     * Called when a close action is performed.
     */
    fun onClose()
}
