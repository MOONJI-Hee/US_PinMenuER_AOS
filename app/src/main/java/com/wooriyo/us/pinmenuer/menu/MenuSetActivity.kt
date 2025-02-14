package com.wooriyo.us.pinmenuer.menu

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.Companion.isPhotoPickerAvailable
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.wooriyo.us.pinmenuer.BaseActivity
import com.wooriyo.us.pinmenuer.MyApplication
import com.wooriyo.us.pinmenuer.MyApplication.Companion.allCateList
import com.wooriyo.us.pinmenuer.MyApplication.Companion.store
import com.wooriyo.us.pinmenuer.MyApplication.Companion.storeidx
import com.wooriyo.us.pinmenuer.MyApplication.Companion.useridx
import com.wooriyo.us.pinmenuer.R
import com.wooriyo.us.pinmenuer.config.AppProperties
import com.wooriyo.us.pinmenuer.databinding.ActivityMenuSetBinding
import com.wooriyo.us.pinmenuer.listener.DialogListener
import com.wooriyo.us.pinmenuer.listener.ItemClickListener
import com.wooriyo.us.pinmenuer.menu.adpter.CateAdapter
import com.wooriyo.us.pinmenuer.menu.adpter.GoodsAdapter
import com.wooriyo.us.pinmenuer.menu.adpter.OptAdapter
import com.wooriyo.us.pinmenuer.menu.dialog.OptionDialog
import com.wooriyo.us.pinmenuer.model.GoodsDTO
import com.wooriyo.us.pinmenuer.model.GoodsListDTO
import com.wooriyo.us.pinmenuer.util.ApiClient
import com.wooriyo.us.pinmenuer.util.AppHelper
import com.wooriyo.us.pinmenuer.model.OptionDTO
import com.wooriyo.us.pinmenuer.model.ResultDTO
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class MenuSetActivity : BaseActivity(), View.OnClickListener {
    lateinit var binding: ActivityMenuSetBinding
    lateinit var cateAdapter : CateAdapter

    private val permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

    val radius = (6 * MyApplication.density).toInt()

    val allGoodsList = ArrayList<GoodsDTO>()
    val selGoodsList = ArrayList<GoodsDTO>()
    val goodsAdapter = GoodsAdapter(selGoodsList)

    val optList = ArrayList<OptionDTO>()
    val optAdapter = OptAdapter(optList)

//    var optDialog : OptionDialog ?= null

    // Api 보낼 때 옵션
    var optCode = ""
    var optName = ""
    var optValue = ""
    var optMark = ""
    var optPrice = ""
    var optReq = ""

    var mode : Int = 0      // 0: 저장, 1: 모드, 3: 순서변경, 4: 삭제
    var selCate = "001"
    var goods = GoodsDTO()

    var selThum: ImageView ?= null

    val mmtp = MediaType.parse("image/*")

    var media1: MultipartBody.Part? = null
    var media2: MultipartBody.Part? = null
    var media3: MultipartBody.Part? = null

    var delImg1 = 0
    var delImg2 = 0
    var delImg3 = 0


    private val setCate = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if(it.resultCode == Activity.RESULT_OK) {
            if(allCateList.isNotEmpty()) {
                cateAdapter.notifyDataSetChanged()
            }
        }
    }

    private val pickImg = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        if(it != null) {
            Log.d(TAG, "이미지 Uri >> $it")
            setImage(it)
        }
    }

    private val pickImg_lgc = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
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

        setCateAdapter()

        binding.etPrice.addTextChangedListener(textWatcher)

        binding.back.setOnClickListener{finish()}
        binding.btnCateUdt.setOnClickListener(this)     // 카테고리 수정 버튼

        // 좌측 메뉴 리스트 관련
        binding.btnSeq.setOnClickListener(this)
        binding.btnDel.setOnClickListener(this)

        // 중앙 메뉴 상세 관련
        binding.menuSave.setOnClickListener{save()}
        binding.optRequire.setOnClickListener(this)
        binding.optChoice.setOnClickListener(this)
        binding.thum1.setOnClickListener(this)
        binding.thum2.setOnClickListener(this)
        binding.thum3.setOnClickListener(this)
        binding.regImg1.setOnClickListener(this)
        binding.regImg2.setOnClickListener(this)
        binding.regImg3.setOnClickListener(this)
        binding.del1.setOnClickListener {
            clearImage(binding.thum1)
            delImg1 = 1
        }
        binding.del2.setOnClickListener {
            clearImage(binding.thum2)
            delImg2 = 1
        }
        binding.del3.setOnClickListener {
            clearImage(binding.thum3)
            delImg3 = 1
        }

        // 우측 메뉴 삭제 확인창 관련
        binding.delCancel.setOnClickListener(this)
        binding.delSave.setOnClickListener(this)

        getMenu()
    }

    override fun onClick(v: View?) {
        when(v) {
            binding.btnCateUdt -> {     // 카테고리 수정 버튼 > 카테고리 설정 페이지로 이동
                val intent = Intent(mActivity, CategorySetActivity::class.java)
                setCate.launch(intent)
            }
            // 좌측 메뉴 리스트 관련
            binding.btnSeq -> {
                v as TextView
                if(mode == 3) { // 순서변경모드일 때 > 완료 수정모드로 변경
                    udtSequence()
                    v.setBackgroundResource(R.drawable.bg_btn_r6)
                    v.text = getString(R.string.change_seq)
                    mode = 1

                    binding.headerArea.visibility = View.GONE
                    binding.rightArea.visibility = View.GONE
                    binding.btnDel.isEnabled = true

                    goods = selGoodsList[0]
                    if(selGoodsList.size > 1) {
                        mode = 1
                        setDetail()
                    }else
                        mode = 0
                    goodsAdapter.selPos = 0
                    goodsAdapter.setMenuMode(mode)

                    itemTouchHelper.setDefaultDragDirs(0)
                }else {         // 순서변경모드 아닐 때 > 순서변경모드로 변경
                    v.setBackgroundResource(R.drawable.bg_btn_r6_grd)
                    v.text = getString(R.string.change_seq_cmplt)
                    mode = 3

                    binding.headerArea.visibility = View.VISIBLE
                    binding.rightArea.visibility = View.VISIBLE
                    binding.seqInfo.visibility = View.VISIBLE
                    binding.delConfirm.visibility = View.GONE
                    binding.btnDel.isEnabled = false

                    clearDetail()
                    goodsAdapter.selPos = -1
                    goodsAdapter.setMenuMode(3)

                    itemTouchHelper.setDefaultDragDirs(ItemTouchHelper.UP or ItemTouchHelper.DOWN)
                }
            }
            binding.btnDel -> {
                v as TextView
                if(mode == 4) { // 삭제모드일 때 > 완료 수정모드로 변경
                    v.setBackgroundResource(R.drawable.bg_btn_r6)
                    v.text = getString(R.string.menu_delete)
                    mode = 1

                    binding.headerArea.visibility = View.GONE
                    binding.rightArea.visibility = View.GONE
                    binding.btnSeq.isEnabled = true

                    goods = selGoodsList[0]
                    if(selGoodsList.size > 1) {
                        mode = 1
                        setDetail()
                    }else
                        mode = 0
                    goodsAdapter.selPos = 0
                    goodsAdapter.setMenuMode(mode)
                }else {         // 삭제모드 아닐 때 > 삭제모드로 변경
                    v.setBackgroundResource(R.drawable.bg_btn_r6_grd)
                    v.text = getString(R.string.menu_delete_cmplt)
                    mode = 4

                    binding.headerArea.visibility = View.VISIBLE
                    binding.rightArea.visibility = View.VISIBLE
                    binding.seqInfo.visibility = View.GONE
                    binding.delConfirm.visibility = View.GONE
                    binding.btnSeq.isEnabled = false

                    clearDetail()
                    goodsAdapter.selPos = -1
                    goodsAdapter.setMenuMode(mode)
                }
            }
            // 우측 메뉴 삭제 확인창 관련
            binding.delCancel -> {
                binding.delConfirm.visibility = View.GONE

                val delPos = goodsAdapter.selPos
                goodsAdapter.selPos = -1
                goodsAdapter.notifyItemChanged(delPos)
            }

            binding.delSave -> {
                val delPos = goodsAdapter.selPos
                delGoods(selGoodsList[delPos].idx, delPos)
            }

            // 중앙 메뉴 상세 관련
            // 이미지 썸네일 > 이미지 등록
            binding.regImg1, binding.thum1 -> {
                selThum = binding.thum1
                checkUsePay()
            }
            binding.regImg2, binding.thum2 -> {
                selThum = binding.thum2
                checkUsePay()
            }
            binding.regImg3, binding.thum3 -> {
                selThum = binding.thum3
                checkUsePay()
            }

            // 옵션 추가
            binding.optRequire -> { showOptDialog(-1, OptionDTO(1)) }
            binding.optChoice -> { showOptDialog(-1, OptionDTO(0)) }
        }
    }

    fun checkUsePay() {
        if((store.payuse == "Y" && AppHelper.dateNowCompare(store.endDate))) {
            checkPermissions()
        }else {
            Toast.makeText(mActivity, R.string.msg_no_pay, Toast.LENGTH_SHORT).show()
        }
    }

    // 외부저장소 접근 권한 확인
    fun checkPermissions() {
        val deniedPms = ArrayList<String>()

        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            for (pms in permission) {
                if(ActivityCompat.checkSelfPermission(mActivity, pms) != PackageManager.PERMISSION_GRANTED) {
                    if(ActivityCompat.shouldShowRequestPermissionRationale(mActivity, pms)) {
                        AlertDialog.Builder(mActivity)
                            .setTitle(R.string.pms_storage_title)
                            .setMessage(R.string.pms_storage_content)
                            .setPositiveButton(R.string.confirm) { dialog, _ ->
                                dialog.dismiss()
                                getStoragePms()
                            }
                            .setNegativeButton(R.string.cancel) {dialog, _ -> dialog.dismiss()}
                            .show()
                        return
                    }else {
                        deniedPms.add(pms)
                    }
                }
            }
        }

        if(deniedPms.isNotEmpty()) {
            getStoragePms()
        }else {
            getImage()
        }
    }

    // 외부저장소 권한 받아오기
    fun getStoragePms() {
        ActivityCompat.requestPermissions(mActivity, permission, AppProperties.REQUEST_STORAGE)
    }

    fun getImage() {
        if(isPhotoPickerAvailable(mActivity)) {
            pickImg.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }else {
            pickImg_lgc.launch(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI))
        }
    }

    fun setImage(imgUri: Uri) {
        var path = ""

        contentResolver.query(imgUri, null, null, null, null)?.use { cursor ->
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)

            while (cursor.moveToNext()) {
                path = cursor.getString(dataColumn)
                Log.d(TAG, "path >>>>> $path")
            }
        }

        if(selThum != null) {
            Glide.with(mActivity)
                .load(imgUri)
                .transform(CenterCrop(), RoundedCorners(radius))
                .into(selThum!!)
        }

        when(selThum) {
            binding.thum1 -> {
                goods.file1 = File(path)
                delImg1 = 0
                binding.imgHint1.visibility = View.GONE
                binding.del1.visibility = View.VISIBLE
            }
            binding.thum2 -> {
                goods.file2 = File(path)
                delImg2 = 0
                binding.imgHint2.visibility = View.GONE
                binding.del2.visibility = View.VISIBLE
            }
            binding.thum3 -> {
                goods.file3 = File(path)
                delImg3 = 0
                binding.imgHint3.visibility = View.GONE
                binding.del3.visibility = View.VISIBLE
            }
        }
    }

    fun setCateAdapter() {
        cateAdapter = CateAdapter(allCateList, 1)
        cateAdapter.setOnItemClickListener(object: ItemClickListener {
            override fun onItemClick(position: Int) {
                selCate = allCateList[position].code
                setMenuList()
            }
        })
        setAdapter()

        binding.run {
            rvCate.layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false)
            rvCate.adapter = cateAdapter

            rvMenu.layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
            rvMenu.adapter = goodsAdapter

            rvOption.layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
            rvOption.adapter = optAdapter
        }

        if(allCateList.isNotEmpty())
            selCate = allCateList[0].code
    }

    fun setAdapter() {
        ItemTouchHelper(itemTouchHelper).attachToRecyclerView(binding.rvMenu)

        goodsAdapter.setOnItemClickListener(object : ItemClickListener {
            override fun onItemClick(position: Int) {
                clearDetail()
                goods = selGoodsList[position]
                if(position < selGoodsList.size-1) {

                    selGoodsList.lastIndex
                    mode = 1
                    setDetail()
                }else
                    mode = 0
            }
        })

        goodsAdapter.setOnDeleteListener(object : ItemClickListener {
            override fun onItemClick(position: Int) {
                binding.delConfirm.visibility = View.VISIBLE
                binding.delName.text = getString(R.string.menu_delete_name).format(selGoodsList[position].name)
            }
        })

        goodsAdapter.setOnMoveListener(object : ItemClickListener {
            override fun onItemClick(position: Int) {
                itemTouchHelper.setDefaultDragDirs(ItemTouchHelper.UP or ItemTouchHelper.DOWN)
            }
        })

        optAdapter.setOnItemClickListener(object : ItemClickListener {
            override fun onItemClick(position: Int) {
                val copyOpt = optList[position].deepCopy()  // 옵션 수정 사항 저장하지 않을수도 있기 때문에 깊은 복사 진행
                showOptDialog(position, copyOpt)
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
        }else
            mode = 0
    }

    fun showOptDialog(position: Int, optionDTO: OptionDTO) {
        val optDialog = OptionDialog(mActivity, position, optionDTO)
        optDialog.setOnDialogListener(object : DialogListener {
            override fun onOptAdd(option: OptionDTO) {
                super.onOptAdd(option)
                if(goods.opt == null) {
                    goods.opt = ArrayList<OptionDTO>()
                }
                goods.opt?.add(option)
                optList.add(option)
                optAdapter.notifyItemInserted((goods.opt?.size?: 1) - 1)

                if(optList.size == 1) { // 옵션 리스트 크기가 1일 때 == 처음 옵션이 등록되었을 때 > 리사이클러뷰 보이도록
                    binding.rvOption.visibility = View.VISIBLE
                    binding.optionEmpty.visibility = View.GONE
                }
            }

            override fun onOptSet(position: Int, option: OptionDTO) {
                super.onOptSet(position, option)
                goods.opt?.set(position, option)
                optList[position] = option
                optAdapter.notifyItemChanged(position)
            }

            override fun onItemDelete(position: Int) {
                super.onItemDelete(position)
                goods.opt?.removeAt(position)
                optList.removeAt(position)
                optAdapter.notifyItemRemoved(position)

                if(optList.size == 0) {
                    binding.rvOption.visibility = View.GONE
                    binding.optionEmpty.visibility = View.VISIBLE
                }
            }
        })
        optDialog.show()
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
            etCookingTimeMin.setText(goods.cooking_time_min)
            etCookingTimeMax.setText(goods.cooking_time_max)
            etPrice.setText(goods.price.toString())

            val img1 = goods.img1
            val img2 = goods.img2
            val img3 = goods.img3

            if(!(img1.isNullOrEmpty() || img1.contains("noimage.png"))) {
                Glide.with(mActivity)
                    .load(goods.img1)
                    .transform(CenterCrop(), RoundedCorners(radius))
                    .into(thum1)
                imgHint1.visibility = View.GONE
                del1.visibility = View.VISIBLE
            }

            if(!(img2.isNullOrEmpty() || img2.contains("noimage.png"))) {
                Glide.with(mActivity)
                    .load(goods.img2)
                    .transform(CenterCrop(), RoundedCorners(radius))
                    .into(thum2)
                imgHint2.visibility = View.GONE
                del2.visibility = View.VISIBLE
            }

            if(!(img3.isNullOrEmpty() || img3.contains("noimage.png"))) {
                Glide.with(mActivity)
                    .load(goods.img3)
                    .transform(CenterCrop(), RoundedCorners(radius))
                    .into(thum3)
                imgHint3.visibility = View.GONE
                del3.visibility = View.VISIBLE
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

            if(!goods.opt.isNullOrEmpty()) {
                optList.addAll(goods.opt!!)
                optAdapter.notifyDataSetChanged()
                rvOption.visibility = View.VISIBLE
                optionEmpty.visibility = View.GONE
            }
        }
    }

    fun clearDetail() {
        optList.clear()
        optAdapter.notifyDataSetChanged()
        binding.run {
            etName.text.clear()
            etContent.text.clear()
            etCookingTimeMin.text.clear()
            etCookingTimeMax.text.clear()
            etPrice.text.clear()

            clearImage(thum1)
            clearImage(thum2)
            clearImage(thum3)

            toggleSleep.isChecked = false
            rbNone.isChecked = true
            toggleOption.isChecked = false

            rvOption.visibility = View.GONE
            optionEmpty.visibility = View.VISIBLE
        }
        delImg1 = 0
        delImg2 = 0
        delImg3 = 0
    }

    fun clearImage(v: ImageView) {
        Glide.with(mActivity)
            .load(R.drawable.bg_r6w)
            .transform(RoundedCorners(radius))
            .into(v)

        when(v) {
            binding.thum1 -> {
                binding.imgHint1.visibility = View.VISIBLE
                binding.del1.visibility = View.GONE
                goods.file1 = null
            }
            binding.thum2 -> {
                binding.imgHint2.visibility = View.VISIBLE
                binding.del2.visibility = View.GONE
                goods.file2 = null
            }
            binding.thum3 -> {
                binding.imgHint3.visibility = View.VISIBLE
                binding.del3.visibility = View.GONE
                goods.file3 = null
            }
        }
    }

    fun save() {
        media1 = null
        media2 = null
        media3 = null

        val jsonArray = JSONArray()

        binding.run {
            val strName = etName.text.toString()
            var strCookTimeMin = etCookingTimeMin.text.toString()
            var strCookTimeMax = etCookingTimeMax.text.toString()
            var strPrice = etPrice.text.toString().replace(",", "")

            if(strName.isEmpty()){
                Toast.makeText(mActivity, R.string.msg_no_goods_name, Toast.LENGTH_SHORT).show()
                return
            }

            if(strCookTimeMin.isEmpty())
                strCookTimeMin = "0"

            if(strCookTimeMax.isEmpty())
                strCookTimeMax = "0"

            if(strPrice.isEmpty())
                strPrice = "0"

            goods.name = strName                            // 상품명
            goods.content = etContent.text.toString()       // 상품설명
            goods.cooking_time_min = strCookTimeMin         // 조리시간 최소
            goods.cooking_time_max = strCookTimeMax         // 조리시간 최대
            goods.price = strPrice.toDouble()                  // 가격

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

            goods.opt?.clear()
            goods.opt?.addAll(optList)

            if(!goods.opt.isNullOrEmpty()) {
                goods.opt!!.forEach {
                    val jsonObject = JSONObject()

                    jsonObject.put("optidx", it.idx)
                    jsonObject.put("optionTitle", it.title)

                    if(it.optreq == 0)
                        jsonObject.put("optionType", "선택")
                    else
                        jsonObject.put("optionType", "필수")

                    val jsonArray2 = JSONArray()
                    it.optval?.forEach { value ->
                        val jsonObject2 = JSONObject()
                        jsonObject2.put("optionidx", value.idx)
                        jsonObject2.put("optionName", value.name)
                        jsonObject2.put("optionMark", value.mark)
                        jsonObject2.put("optionPrice", value.price)

                        jsonArray2.put(jsonObject2)
                    }
                    jsonObject.put("optionDetails", jsonArray2)

                    jsonArray.put(jsonObject)
                }
            }
        }

        if(mode == 0)
            insGoods(jsonArray.toString())
        else if(mode == 1)
            udtGoods(jsonArray.toString())
    }

    fun insGoods(strJson : String) {
        goods.let {
            ApiClient.service.insGoods(useridx, storeidx, selCate, it.name, it.content?:"", it.cooking_time_min, it.cooking_time_max, it.price, it.adDisplay, it.icon, it.boption, strJson)
                .enqueue(object : Callback<ResultDTO> {
                    override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                        Log.d(TAG, "메뉴 등록 url : $response")
                        if(!response.isSuccessful) return

                        val result = response.body()
                        if(result != null) {
                            Log.d(TAG, "ins_Goods Result >> ${response.body()}")

                            when(result.status){
                                1 -> {
                                    it.idx = result.idx
                                    it.category = selCate
                                    it.opt = result.opt

                                    allGoodsList.add(goods)
                                    selGoodsList.add(GoodsDTO())
                                    goodsAdapter.notifyItemRangeChanged(selGoodsList.size-2, 2)

                                    optList.clear()
                                    optList.addAll(result.opt?:ArrayList())

                                    uploadImage(it.idx, media1, media2, media3)
                                    // 저장되었으니 수정모드로 변경
                                    mode = 1
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

    fun udtGoods(strJson : String) {
        Log.d(TAG, "send option >> $strJson")

        goods.let {
            ApiClient.service.udtGoods(useridx, storeidx, it.idx, selCate, it.name, it.content?:"", it.cooking_time_min, it.cooking_time_max, it.price, it.icon, delImg1, delImg2, delImg3, it.adDisplay, it.boption, strJson)
                .enqueue(object : Callback<ResultDTO> {
                    override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                        Log.d(TAG, "메뉴 수정 url : $response")
                        if(!response.isSuccessful) return

                        val result = response.body()
                        if(result != null) {
                            Log.d(TAG, "udt_Goods Result >> ${response.body()}")

                            when(result.status){
                                1 -> {
                                    Log.d(TAG, "result option >> ${result.opt}")
                                    it.opt = result.opt
                                    goodsAdapter.notifyItemChanged(goodsAdapter.selPos)

                                    optList.clear()
                                    optList.addAll(result.opt?:ArrayList())

                                    uploadImage(it.idx, media1, media2, media3)
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
                            1 -> {
                                Toast.makeText(mActivity, R.string.msg_complete, Toast.LENGTH_SHORT).show()
                                goods.img1 = result.img1
                                goods.img2 = result.img2
                                goods.img3 = result.img3
                            }
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
        ApiClient.imgService.delGoods(useridx, storeidx, gidx).enqueue(object : Callback<ResultDTO>{
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "메뉴 삭제 url : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return
                when(result.status) {
                    1 -> {
                        Toast.makeText(mActivity, R.string.msg_complete, Toast.LENGTH_SHORT).show()
                        allGoodsList.remove(selGoodsList[position])
                        selGoodsList.removeAt(position)
                        goodsAdapter.selPos = -1
                        goodsAdapter.notifyItemRemoved(position)

                        binding.delConfirm.visibility = View.GONE
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
        val JSON = JSONArray()
        selGoodsList.forEach {
            val json = JSONObject()
            json.put("idx", it.idx)

            JSON.put(json)
        }

        ApiClient.service.udtGoodSeq(useridx, storeidx, JSON.toString()).enqueue(object: Callback<ResultDTO>{
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "메뉴 순서변경 url : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return
                when(result.status) {
                    1 -> {
                        Toast.makeText(this@MenuSetActivity, R.string.msg_complete, Toast.LENGTH_SHORT).show()

                    }
                    else -> Toast.makeText(this@MenuSetActivity, result.msg, Toast.LENGTH_SHORT).show()
                }

            }

            override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "메뉴 순서변경 실패 > $t")
                Log.d(TAG, "메뉴 순서변경 실패 > ${call.request()}")
            }
        })
    }

    val textWatcher = object : TextWatcher {
        var result = ""
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if(!s.isNullOrEmpty() && s.toString() != result) {
                val num = s.toString().replace(",", "")
//                result = AppHelper.price(num)
//                result = AppHelper.price(s.toString().replace(",", "").toInt())
//                binding.etPrice.setText(result)
//                binding.etPrice.setSelection(result.length)
            }
        }
        override fun afterTextChanged(s: Editable?) {}
    }

    val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(0, 0) {
        override fun setDefaultDragDirs(defaultDragDirs: Int) {
            super.setDefaultDragDirs(defaultDragDirs)
        }
        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            val fromPos = viewHolder.absoluteAdapterPosition
            val toPos = target.absoluteAdapterPosition
            goodsAdapter.swapData(fromPos, toPos)
            return true
        }
        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
    }

}
