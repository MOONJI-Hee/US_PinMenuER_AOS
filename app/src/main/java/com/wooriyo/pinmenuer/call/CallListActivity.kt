package com.wooriyo.pinmenuer.call

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.wooriyo.pinmenuer.call.adapter.CallListAdapter
import com.wooriyo.pinmenuer.databinding.ActivityCallListBinding
import com.wooriyo.pinmenuer.model.CallDTO
import com.wooriyo.pinmenuer.model.CallListDTO
import com.wooriyo.pinmenuer.store.StoreMenuActivity

class CallListActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binding: ActivityCallListBinding

    var callList = ArrayList<CallListDTO>()
    var callListAdapter = CallListAdapter(callList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCallListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvCall.layoutManager = LinearLayoutManager(this@CallListActivity, LinearLayoutManager.HORIZONTAL, false)
        binding.rvCall.adapter = callListAdapter

        val call = CallDTO(1, "직원 호출", 0)
        val call2 = CallDTO(2, "포크", 1)
        val call3 = CallDTO(3, "수저", 1)

        val test = CallListDTO(1, "", "01",  arrayListOf(call), "2023-02-20 16:00:01'")
        val test2 = CallListDTO(1, "", "01",  arrayListOf(call2, call3), "2023-02-20 13:23:01'")

        callList.add(test)
        callList.add(test2)
        callListAdapter.notifyDataSetChanged()



        binding.back.setOnClickListener(this)
        binding.btnSet.setOnClickListener(this)
    }

    override fun onBackPressed() {
        startActivity(Intent(this@CallListActivity, StoreMenuActivity::class.java))
    }

    override fun onClick(v: View?) {
        when(v) {
            binding.back -> startActivity(Intent(this@CallListActivity, StoreMenuActivity::class.java))
            binding.btnSet -> startActivity(Intent(this@CallListActivity, CallSetActivity::class.java))
        }
    }
}