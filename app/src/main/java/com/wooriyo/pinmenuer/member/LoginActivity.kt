package com.wooriyo.pinmenuer.member

import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    val TAG = "LoginActivity"
    var waitTime = 0L

    // 뒤로가기 눌렀을 때 처리
    override fun onBackPressed() {
        if(System.currentTimeMillis() - waitTime > 2500) {
            waitTime = System.currentTimeMillis()
            Toast.makeText(this@LoginActivity, R.string.backpress, Toast.LENGTH_LONG).show()
        } else {
            finishAffinity()
        }
    }
    // 바깥화면 터치하면 키보드 내리기
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val focusView = this.currentFocus
        if (focusView != null) {
            val rect = Rect()
            focusView.getGlobalVisibleRect(rect)
            val x = ev.x.toInt()
            val y = ev.y.toInt()
            if (!rect.contains(x, y)) {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(focusView.windowToken, 0)
                focusView.clearFocus()
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}