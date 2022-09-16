package com.zynksoftware.documentscannersample.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zynksoftware.documentscannersample.R
import java.io.File

class ImageAdapter(
    private val context: Context?,
    private var imageList: ArrayList<File>,
    private var listener: ImageAdapterListener,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        val view = layoutInflater.inflate(R.layout.image_preview_adapter, parent, false) as ViewGroup
        return ImageViewHolder(view)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as ImageViewHolder
        holder.bindData(imageList[position], listener)
    }
}