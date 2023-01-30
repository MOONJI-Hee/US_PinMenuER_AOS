package com.wooriyo.pinmenuer.menu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wooriyo.pinmenuer.databinding.ActivityCategorySetBinding

class CategorySetActivity : AppCompatActivity() {
    lateinit var binding : ActivityCategorySetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategorySetBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}