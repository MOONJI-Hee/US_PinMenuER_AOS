package com.wooriyo.pinmenuer.payment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wooriyo.pinmenuer.databinding.ActivityPayCardBinding

class PayCardActivity : AppCompatActivity() {
    lateinit var binding: ActivityPayCardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPayCardBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}