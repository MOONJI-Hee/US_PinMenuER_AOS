package com.wooriyo.pinmenuer.menu

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.wooriyo.pinmenuer.BaseActivity
import com.wooriyo.pinmenuer.MyApplication.Companion.store
import com.wooriyo.pinmenuer.MyApplication.Companion.storeidx
import com.wooriyo.pinmenuer.MyApplication.Companion.useridx
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.databinding.ActivityMenuSetBinding
import com.wooriyo.pinmenuer.listener.ItemClickListener
import com.wooriyo.pinmenuer.menu.adpter.CateAdapter
import com.wooriyo.pinmenuer.menu.adpter.GoodsAdapter
import com.wooriyo.pinmenuer.menu.dialog.BgDialog
import com.wooriyo.pinmenuer.menu.dialog.OptionDialog
import com.wooriyo.pinmenuer.menu.dialog.ViewModeDialog
import com.wooriyo.pinmenuer.model.*
import com.wooriyo.pinmenuer.util.ApiClient
import com.wooriyo.pinmenuer.util.AppHelper
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import retrofit2.Call

class MenuSetActivity : BaseActivity(), View.OnClickListener {
    val TAG = "MenuSetActivity"
    val mActivity = this@MenuSetActivity

    lateinit var binding: ActivityMenuSetBinding
    lateinit var cateList: ArrayList<CategoryDTO>
    lateinit var cateAdapter : CateAdapter

    val allGoodsList = ArrayList<GoodsDTO>()
    val selGoodsList = ArrayList<GoodsDTO>()
    val goodsAdapter = GoodsAdapter(selGoodsList)

    var mode : Int = 0      // 0: 저장, 1: 모드, 3: 순서변경, 4: 삭제
    var selCate = "001"
    var goods = GoodsDTO()

    val REQUEST_R_STORAGE = 1
    var bisStorage: Boolean = false
    var selThum: ImageView ?= null

    val mmtp = MediaType.parse("image/*") // 임시
    var media1: MultipartBody.Part? = null
    var media2: MultipartBody.Part? = null
    var media3: MultipartBody.Part? = null

    //registerForActivityResult
    val chooseImg = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if(it.resultCode == Activity.RESULT_OK) {
            val imgUri = it.data?.data
            if(imgUri != null)
                setImage(imgUri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuSetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cateList = (intent.getSerializableExtra("cateList")?: ArrayList<CategoryDTO>()) as ArrayList<CategoryDTO>
        if(cateList.isNotEmpty())
            setCateAdapter()

        var selBg = ""
        when(store.bgcolor) {
            "d" -> selBg = getString(R.string.bg_dark)
            "s" -> selBg = getString(R.string.bg_silver)
            "l" -> selBg = getString(R.string.bg_light)
        }
        binding.setBg.text = getString(R.string.bg).format(selBg)

        var selMode = ""
        when(store.viewmode) {
            "b" -> selMode = "기본"
            "p" -> selMode = "사진"
        }
        binding.setViewMode.text = getString(R.string.viewmode).format(selMode)

        binding.etPrice.addTextChangedListener(textWatcher)

        binding.back.setOnClickListener(this)
        binding.btnCateUdt.setOnClickListener(this)     // 카테고리 수정 버튼

        // 좌측 메뉴 리스트 관련
        binding.btnSeq.setOnClickListener(this)
        binding.btnDel.setOnClickListener(this)

        binding.setTablePass.setOnClickListener(this)   // 테이블 비밀번호 저장
        binding.setBg.setOnClickListener(this)          // 배경 선택
        binding.setViewMode.setOnClickListener(this)    // 뷰어 모드 선택

        // 중앙 메뉴 상세 관련
        binding.menuSave.setOnClickListener{save()}
        binding.optRequire.setOnClickListener(this)
        binding.optChoice.setOnClickListener(this)
        binding.thum1.setOnClickListener(this)
        binding.thum2.setOnClickListener(this)
        binding.thum3.setOnClickListener(this)

        getMenu()
    }

    override fun onClick(v: View?) {
        when(v) {
            binding.back -> finish()
            binding.btnCateUdt -> {     // 카테고리 수정 버튼 > 카테고리 설정 페이지로 이동
                val intent = Intent(mActivity, CategorySetActivity::class.java)
                intent.putExtra("cateList", cateList)
                startActivity(intent)
            }
            binding.setTablePass -> {setTablePass()}
            binding.setBg -> { BgDialog(mActivity).show() }
            binding.setViewMode -> { ViewModeDialog(mActivity).show() }

            // 중앙 메뉴 상세 관련
            binding.optRequire -> { OptionDialog(mActivity, 1, OptionDTO(1)).show() }
            binding.optChoice -> { OptionDialog(mActivity, 0, OptionDTO(0)).show() }
            binding.thum1, binding.thum2, binding.thum3-> {
                selThum = v as ImageView
                if(bisStorage)
                    getMedia()
                else
                    checkPms()
            }
        }
    }

    fun setCateAdapter() {
        selCate = cateList[0].code

        cateAdapter = CateAdapter(cateList, 1)
        cateAdapter.setOnItemClickListener(object: ItemClickListener {
            override fun onItemClick(position: Int) {
                selCate = cateList[position].code
                setMenuList()
            }
        })
        setAdapter()

        binding.run {
            rvCate.layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false)
            rvCate.adapter = cateAdapter

            rvMenu.layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
            rvMenu.adapter = goodsAdapter
        }
    }

    fun setAdapter() {
        goodsAdapter.setOnItemClickListener(object : ItemClickListener{
            override fun onItemClick(position: Int) {
                clearDetail()
                goods = selGoodsList[position]
                if(position < selGoodsList.size-1) {
                    mode = 1
                    setDetail()
                }
            }
        })
    }

    fun setMenuList() {
        selGoodsList.clear()
        allGoodsList.forEach {
            if(it.category == selCate)
                selGoodsList.add(it)
        }
        selGoodsList.add(GoodsDTO())
        goodsAdapter.selPos = 0
        goodsAdapter.notifyDataSetChanged()

        goods = selGoodsList[0]

        clearDetail()
        if(selGoodsList.size > 1) {
            mode = 1
            setDetail()
        }
    }

    fun getMenu() {
        ApiClient.service.getGoods(useridx, storeidx).enqueue(object : Callback<GoodsListDTO>{
            override fun onResponse(call: Call<GoodsListDTO>, response: Response<GoodsListDTO>) {
                Log.d(TAG, "메뉴 리스트 조회 url : $response")
                if(!response.isSuccessful) return

                val result = response.body()
                if(result != null) {
                    when(result.status){
                        1 -> {
                            allGoodsList.clear()
                            allGoodsList.addAll(result.glist)
                            if(allGoodsList.isNotEmpty())
                                setMenuList()
                        }
                        else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<GoodsListDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "메뉴 리스트 조회 실패 > $t")
            }
        })
    }

    fun setDetail() {
        binding.run {
            etName.setText(goods.name)
            etContent.setText(goods.content)
            etCookingTime.setText(goods.cooking_time_max)
            etPrice.setText(goods.price.toString())

            val img1 = goods.img1
            val img2 = goods.img2
            val img3 = goods.img3

            if(!(img1.isNullOrEmpty() || img1.contains("default_img.png"))) {
                Glide.with(mActivity)
                    .load(goods.img1)
                    .transform(CenterCrop(), RoundedCorners(6))
                    .into(thum1)
                imgHint1.visibility = View.GONE
            }

            if(!(img2.isNullOrEmpty() || img2.contains("default_img.png"))) {
                Glide.with(mActivity)
                    .load(goods.img2)
                    .transform(CenterCrop(), RoundedCorners(6))
                    .into(thum2)
                imgHint2.visibility = View.GONE
            }

            if(!(img3.isNullOrEmpty() || img3.contains("default_img.png"))) {
                Glide.with(mActivity)
                    .load(goods.img3)
                    .transform(CenterCrop(), RoundedCorners(6))
                    .into(thum3)
                imgHint3.visibility = View.GONE
            }

            if(goods.adDisplay == y)
                toggleSleep.isChecked = true

            when(goods.icon) {
                1 -> rbNone.isChecked = true
                2 -> rbHide.isChecked = true
                3 -> rbBest.isChecked = true
                4 -> rbSoldout.isChecked = true
                5 -> rbNew.isChecked = true
            }

            if(goods.boption == y)
                toggleOption.isChecked = true
        }
    }

    fun clearDetail() {
        mode = 0
        binding.run {
            etName.text.clear()
            etContent.text.clear()
            etCookingTime.text.clear()
            etPrice.text.clear()

            Glide.with(mActivity)
                .load(R.drawable.bg_r6w)
                .transform(RoundedCorners(6))
                .into(thum1)
            imgHint1.visibility = View.VISIBLE

            Glide.with(mActivity)
                .load(R.drawable.bg_r6w)
                .transform(RoundedCorners(6))
                .into(thum2)
            imgHint2.visibility = View.VISIBLE

            Glide.with(mActivity)
                .load(R.drawable.bg_r6w)
                .transform(RoundedCorners(6))
                .into(thum3)
            imgHint3.visibility = View.VISIBLE

            toggleSleep.isChecked = false
            rbNone.isChecked = true
            toggleOption.isChecked = false
        }
    }

    fun save() {
        media1 = null
        media2 = null
        media3 = null

        binding.run {
            val strName = etName.text.toString()
            var strCookTime = etCookingTime.text.toString()
            var strPrice = etPrice.text.toString().replace(",", "")

            if(strName.isEmpty()){
                Toast.makeText(mActivity, R.string.msg_no_goods_name, Toast.LENGTH_SHORT).show()
                return
            }

            if(strCookTime.isEmpty())
                strCookTime = "0"

            if(strPrice.isEmpty())
                strPrice = "0"

            goods.name = strName                            // 상품명
            goods.content = etName.text.toString()          // 상품설명
            goods.cooking_time_max = strCookTime            // 조리시간
            goods.price = strPrice.toInt()                  // 가격

            if(goods.file1 != null) {
                val body = RequestBody.create(mmtp, goods.file1!!)
                media1 = MultipartBody.Part.createFormData("img1", goods.file1!!.name, body)
            }
            if(goods.file2 != null) {
                val body = RequestBody.create(mmtp, goods.file2!!)
                media2 = MultipartBody.Part.createFormData("img2", goods.file2!!.name, body)
            }
            if(goods.file3 != null) {
                val body = RequestBody.create(mmtp, goods.file3!!)
                media3 = MultipartBody.Part.createFormData("img3", goods.file3!!.name, body)
            }

            goods.adDisplay = if(toggleSleep.isChecked) y else n

            goods.icon = when {
                rbNone.isChecked -> 1
                rbHide.isChecked -> 2
                rbBest.isChecked -> 3
                rbSoldout.isChecked -> 4
                rbNew.isChecked -> 5
                else -> 0
            }
            goods.boption = if(toggleOption.isChecked) y else n
        }

        if(mode == 0)
            insGoods()
        else if(mode == 1)
            udtGoods()
    }

    fun insGoods() {
        goods.let {
            ApiClient.service.insGoods(useridx, storeidx, selCate, it.name, it.content?:"", it.cooking_time_min, it.cooking_time_max, it.price, it.adDisplay, it.icon, it.boption)
                .enqueue(object : Callback<ResultDTO> {
                    override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                        Log.d(TAG, "메뉴 등록 url : $response")
                        if(!response.isSuccessful) return

                        val result = response.body()
                        if(result != null) {
                            when(result.status){
                                1 -> {
                                    val gidx = result.idx
                                    uploadImage(gidx, media1, media2, media3)
                                }
                                else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                        Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "메뉴 등록 실패 > $t")
                        Log.d(TAG, "메뉴 등록 실패 > ${call.request()}")
                    }
                })
        }
    }

    fun udtGoods() {
        goods.let {
            ApiClient.service.udtGoods(useridx, storeidx, selCate, it.name, it.content?:"", it.cooking_time_min, it.cooking_time_max, it.price, it.adDisplay, it.icon, it.boption)
                .enqueue(object : Callback<ResultDTO> {
                    override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                        Log.d(TAG, "메뉴 수정 url : $response")
                        if(!response.isSuccessful) return

                        val result = response.body()
                        if(result != null) {
                            when(result.status){
                                1 -> {
                                    val gidx = result.idx
                                    uploadImage(gidx, media1, media2, media3)
                                    // TODO 등록된 사진 중 삭제할 것 태우기
                                }
                                else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                        Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "메뉴 수정 실패 > $t")
                        Log.d(TAG, "메뉴 수정 실패 > ${call.request()}")
                    }
                })
        }
    }

    fun uploadImage(gidx: Int, media1: MultipartBody.Part?, media2: MultipartBody.Part?, media3: MultipartBody.Part?) {
        ApiClient.imgService.uploadImg(useridx, gidx, media1, media2, media3)
            .enqueue(object : Callback<ResultDTO>{
                override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                    Log.d(TAG, "이미지 등록 url : $response")
                    if(!response.isSuccessful) return

                    val result = response.body()
                    if(result != null) {
                        when(result.status){
                            1 -> { Toast.makeText(mActivity, R.string.msg_complete, Toast.LENGTH_SHORT).show() }
                            else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                    Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "이미지 등록 실패 > $t")
                    Log.d(TAG, "이미지 등록 실패 > ${call.request()}")
                }
            })
    }

    fun delGoods(gidx: Int, position: Int) {
        ApiClient.service.delGoods(useridx, storeidx, gidx).enqueue(object : Callback<ResultDTO>{
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "메뉴 수정 url : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return
                when(result.status) {
                    1 -> {
                        Toast.makeText(mActivity, R.string.msg_complete, Toast.LENGTH_SHORT).show()
                        allGoodsList.remove(selGoodsList[position])
                        selGoodsList.removeAt(position)
                        goodsAdapter.notifyItemRemoved(position)
                    }
                    else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "메뉴 삭제 실패 > $t")
                Log.d(TAG, "메뉴 삭제 실패 > ${call.request()}")
            }
        })
    }

    fun udtSequence() {

    }

    fun setTablePass() {
        val pass = binding.etTablePass.text.toString()

        if(pass.isEmpty()) {
            Toast.makeText(mActivity, R.string.msg_no_pw, Toast.LENGTH_SHORT).show()
            return
        }

        ApiClient.service.udtTablePwd(useridx, storeidx, pass).enqueue(object : Callback<ResultDTO> {
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "테이블 비밀번호 설정 url : $response")
                if(!response.isSuccessful) return
                val result = response.body()
                if(result != null) {
                    when(result.status){
                        1 -> Toast.makeText(mActivity, R.string.msg_complete, Toast.LENGTH_SHORT).show()
                        else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "테이블 비밀번호 설정 오류 > $t")
            }
        })
    }

    val textWatcher = object : TextWatcher {
        var result = ""
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if(!s.isNullOrEmpty() && s.toString() != result) {
                result = AppHelper.price(s.toString().replace(",", "").toInt())
                binding.etPrice.setText(result)
                binding.etPrice.setSelection(result.length)
            }
        }
        override fun afterTextChanged(s: Editable?) {}
    }

    //이미지 권한 설정 및 가져오기
    // 외부저장소 권한 확인
    fun checkPms() {
//        if(MyApplication.osver >= Build.VERSION_CODES.Q) {
//
//        }else {
//
//        }
        when {
            ContextCompat.checkSelfPermission(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED -> {
                bisStorage = true
                getMedia()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                AlertDialog.Builder(mActivity)
                    .setTitle(R.string.pms_storage_title)
                    .setMessage(R.string.pms_storage_content)
                    .setPositiveButton(R.string.confirm) { dialog, _ ->
                        dialog.dismiss()
                        getPms()
                    }
                    .setNegativeButton(R.string.cancel) {dialog, _ -> dialog.dismiss()}
                    .show()
            }
            else -> getPms()
        }
    }

    // 외부저장소 권한 받아오기
    fun getPms() {
        ActivityCompat.requestPermissions(mActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_R_STORAGE)
    }

    fun getMedia() {
        if(selThum == binding.thum1)
            chooseImg.launch(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI))
        else
            chooseImg.launch(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI))
    }

    fun setImage(imgUri: Uri) {
        var path = ""
        var name = ""

        contentResolver.query(imgUri, null, null, null, null)?.use { cursor ->
            // Cache column indices.
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)

            while (cursor.moveToNext()) {
                path = cursor.getString(dataColumn)
                name = cursor.getString(nameColumn)
                Log.d(TAG, "name >>>>> $name")
                Log.d(TAG, "path >>>>> $path")
            }
        }
        val file = File(path)

        if(selThum != null) {
            Glide.with(mActivity)
                .load(imgUri)
                .transform(CenterCrop(), RoundedCorners(6))
                .into(selThum!!)
        }

        when(selThum) {
            binding.thum1 -> {
                goods.file1 = file
                binding.imgHint1.visibility = View.GONE
            }
            binding.thum2 -> {
                goods.file2 = file
                binding.imgHint2.visibility = View.GONE
            }
            binding.thum3 -> {
                goods.file3 = file
                binding.imgHint3.visibility = View.GONE
            }
        }
    }
}
