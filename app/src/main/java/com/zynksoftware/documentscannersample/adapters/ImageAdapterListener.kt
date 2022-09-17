package com.zynksoftware.documentscannersample.adapters

import java.io.File

/**
 * Interface definition for a callback to be invoked when an action is performed on an [ImageAdapter].
 */
interface ImageAdapterListener {

    /**
     * Called when a save button has been clicked.
     */
    fun onSaveButtonClicked(image: File)
}
