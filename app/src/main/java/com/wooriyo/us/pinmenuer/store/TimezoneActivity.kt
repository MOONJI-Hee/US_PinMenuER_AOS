package com.wooriyo.us.pinmenuer.store

import android.os.Bundle
import android.widget.RadioButton
import com.wooriyo.us.pinmenuer.BaseActivity
import com.wooriyo.us.pinmenuer.R
import com.wooriyo.us.pinmenuer.databinding.ActivityTimezoneBinding

class TimezoneActivity : BaseActivity() {
    lateinit var binding: ActivityTimezoneBinding

    var selRadioId = 0
    var pre = ""    // reg : 등록할 때, udt : 수정

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimezoneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        selRadioId = binding.radioEastern.id
        pre = intent.getStringExtra("pre") ?: ""

        binding.run {
            if(pre == "more") save.text = getString(R.string.save)

            eastern.setOnClickListener { setRadioClickEvent(radioEastern) }
            central.setOnClickListener { setRadioClickEvent(radioCentral) }
            mountain.setOnClickListener { setRadioClickEvent(radioMountain) }
            pacific.setOnClickListener { setRadioClickEvent(radioPacific) }
            alaska.setOnClickListener { setRadioClickEvent(radioAlaska) }
            hawaii.setOnClickListener { setRadioClickEvent(radioHawaii) }

            back.setOnClickListener { finish() }
            save.setOnClickListener { save() }
        }
    }

    fun setRadioClickEvent (v: RadioButton) {
        if(selRadioId != v.id) {
            findViewById<RadioButton>(selRadioId).isChecked = false
            v.isChecked = true
            selRadioId = v.id
        }
    }

    fun save() {

    }
}