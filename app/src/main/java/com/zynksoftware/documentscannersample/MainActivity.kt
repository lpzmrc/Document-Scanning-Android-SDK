package com.zynksoftware.documentscannersample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zynksoftware.documentscannersample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(
            ActivityMainBinding.inflate(layoutInflater).apply {
                _binding = this
            }.root
        )

        initListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initListeners() {
        binding.scanLibButton.setOnClickListener {
            AppScanActivity.start(this)
        }
    }
}
