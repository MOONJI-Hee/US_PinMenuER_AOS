package com.wooriyo.pinmenuer.menu

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.wooriyo.pinmenuer.BaseActivity
import com.wooriyo.pinmenuer.MyApplication.Companion.allCateList
import com.wooriyo.pinmenuer.MyApplication.Companion.storeidx
import com.wooriyo.pinmenuer.MyApplication.Companion.useridx
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.databinding.ActivityCategorySetBinding
import com.wooriyo.pinmenuer.listener.DialogListener
import com.wooriyo.pinmenuer.listener.ItemClickListener
import com.wooriyo.pinmenuer.menu.adpter.CateAdapter
import com.wooriyo.pinmenuer.menu.adpter.CateEditAdapter
import com.wooriyo.pinmenuer.menu.dialog.CategoryDialog
import com.wooriyo.pinmenuer.model.CateListDTO
import com.wooriyo.pinmenuer.model.CategoryDTO
import com.wooriyo.pinmenuer.model.ResultDTO
import com.wooriyo.pinmenuer.util.ApiClient
import com.wooriyo.pinmenuer.util.AppHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategorySetActivity : BaseActivity(), DialogListener {
    lateinit var binding : ActivityCategorySetBinding

    val TAG = "CategorySetActivity"

    lateinit var cateAdapter : CateAdapter
    lateinit var cateEditAdapter : CateEditAdapter

    var flag = 0 // 순서 수정 모드 구분 > 0 : 수정모드 X, 1 : 수정모드 O

    val backList = ArrayList<CategoryDTO>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategorySetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(allCateList.isEmpty()) { showCateDialog(-1, null) }

        setView()

        binding.run {
            back.setOnClickListener { finish() }
            confirm.setOnClickListener {
                if(allCateList.isEmpty()) {
                    Toast.makeText(this@CategorySetActivity, R.string.msg_no_category, Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                intent = Intent(this@CategorySetActivity, MenuSetActivity::class.java)
                startActivity(intent)
            }
            arrowLeft.setOnClickListener {  }
            arrowRight.setOnClickListener {  }
            btnAdd.setOnClickListener { showCateDialog(-1, null) }
            btnSeq.setOnClickListener {
                if(flag == 0)
                    setSeqMode()
                else
                    setUdtMode()
            }
            seqSave.setOnClickListener { seqSave() }
            seqCancel.setOnClickListener { setUdtMode() }
        }
    }

    override fun onCateAdd(cateList: ArrayList<CategoryDTO>) {
        allCateList.clear()
        allCateList.addAll(cateList)
        cateAdapter.notifyItemInserted(allCateList.size-1)
        cateAdapter.notifyItemChanged(allCateList.size-2)  // 추가한 카테고리 바로 앞 카테고리의 오른쪽 실선을 숨기기 위해서...
        cateEditAdapter.notifyItemInserted(allCateList.size-1)
        setResult(RESULT_OK, intent)
    }

    override fun onCateSet(position: Int, data: CategoryDTO) {
        super.onCateSet(position, data)
        allCateList[position] = data
        cateAdapter.notifyItemChanged(position)
        cateEditAdapter.notifyItemChanged(position)
        setResult(RESULT_OK, intent)
    }

    override fun onItemDelete(position: Int) {
        super.onItemDelete(position)
        allCateList.removeAt(position)
        cateAdapter.notifyItemRemoved(position)
        cateEditAdapter.notifyItemRemoved(position)
        setResult(RESULT_OK, intent)
    }

    fun setView() {
        cateAdapter = CateAdapter(allCateList, 0)
        cateEditAdapter = CateEditAdapter(allCateList)

        cateEditAdapter.setOnItemClickListener(object: ItemClickListener{
            override fun onItemClick(position: Int) {
                super.onItemClick(position)
                showCateDialog(position, allCateList[position])
            }
        })

        cateEditAdapter.setOnMoveListener(object : ItemClickListener {
            override fun onItemMove(fromPos: Int, toPos: Int) {
                super.onItemMove(fromPos, toPos)
                Log.d(TAG, "fromPos >> $fromPos")
                Log.d(TAG, "toPos >> $toPos")
                Log.d(TAG, "순서변경 전 allCateList >> $allCateList")
                val moveCate = allCateList[fromPos]
                allCateList.removeAt(fromPos)
                allCateList.add(toPos, moveCate)
                Log.d(TAG, "순서변경 후 allCateList >> $allCateList")

                cateEditAdapter.notifyItemMoved(fromPos, toPos)

                cateEditAdapter.notifyItemChanged(fromPos)
                cateEditAdapter.notifyItemChanged(toPos)
            }
        })

        binding.run {
            rvCate.layoutManager = LinearLayoutManager(this@CategorySetActivity, LinearLayoutManager.HORIZONTAL, false)
            rvCate.adapter = cateAdapter

            rvCateEdit.layoutManager = LinearLayoutManager(this@CategorySetActivity, LinearLayoutManager.HORIZONTAL, false)
            rvCateEdit.adapter = cateEditAdapter
        }
    }

    private fun setUdtMode() {
        // 취소했기 때문에 백업 데이터로 다시 복원
        // **setMode에 notifyDataSet()이 있기 떄문에 이 코드보다 위에 적기
        allCateList.clear()
        allCateList.addAll(backList)

        flag = 0
        binding.run {
            btnAdd.isEnabled = true
            btnSeq.setBackgroundResource(R.drawable.bg_btn_r6)
            subtextUdt.visibility = View.VISIBLE
            subtextSeq.visibility = View.GONE
            confirm.visibility = View.VISIBLE
            llSeq.visibility = View.GONE
        }
        cateEditAdapter.setMode(flag)
    }

    private fun setSeqMode() {
        // 취소할 경우를 대비하여 백업 리스트 생성
        backList.clear()
        backList.addAll(allCateList)

        flag = 1    // 순서 수정 모드로 변경
        binding.run {
            btnAdd.isEnabled = false
            btnSeq.setBackgroundResource(R.drawable.bg_btn_r6_grd)
            subtextUdt.visibility = View.GONE
            subtextSeq.visibility = View.VISIBLE
            confirm.visibility = View.GONE
            llSeq.visibility = View.VISIBLE
        }
        cateEditAdapter.setMode(flag)
    }

    fun showCateDialog(position: Int, categoryDTO: CategoryDTO?) {
        val categoryDialog = CategoryDialog(this@CategorySetActivity, position, categoryDTO)
        categoryDialog.setOnDialogListener(this@CategorySetActivity)
        categoryDialog.show()
    }

    fun seqSave() {
        ApiClient.service.udtCateSeq(useridx, storeidx, "").enqueue(object : Callback<CateListDTO> {
            override fun onResponse(call: Call<CateListDTO>, response: Response<CateListDTO>) {
                Log.d(TAG, "카테고리 순서변경 url : $response")
                if(!response.isSuccessful) {return}

                val resultDTO = response.body() ?: return
                when(resultDTO.status) {
                    1 -> {
                        Toast.makeText(this@CategorySetActivity, R.string.msg_complete, Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK, intent)
                        allCateList.clear()
                        allCateList.addAll(resultDTO.cateList)
                        backList.clear()
                        cateAdapter.notifyDataSetChanged()
                    }
                    else -> Toast.makeText(this@CategorySetActivity, resultDTO.msg, Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<CateListDTO>, t: Throwable) {
                Log.d(TAG, "카테고리 순서변경 실패 > $t")
                Log.d(TAG, "카테고리 순서변경 실패 > ${call.request()}")
            }
        })
    }
}