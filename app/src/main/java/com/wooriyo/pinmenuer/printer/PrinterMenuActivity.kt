package com.wooriyo.pinmenuer.printer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wooriyo.pinmenuer.databinding.ActivityPrinterMenuBinding

class PrinterMenuActivity : AppCompatActivity() {
    lateinit var binding: ActivityPrinterMenuBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrinterMenuBinding.inflate(layoutInflater)
        setContentView(binding.root
        )
    }
}