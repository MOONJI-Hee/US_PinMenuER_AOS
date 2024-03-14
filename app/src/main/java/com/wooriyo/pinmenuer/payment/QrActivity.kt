package com.wooriyo.pinmenuer.payment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.wooriyo.pinmenuer.BaseActivity
import com.wooriyo.pinmenuer.MyApplication
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.config.AppProperties
import com.wooriyo.pinmenuer.databinding.ActivityQrBinding
import com.wooriyo.pinmenuer.history.ByHistoryActivity
import java.net.URISyntaxException


class QrActivity : BaseActivity() {
    lateinit var binding: ActivityQrBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val ordcode_key = intent.getStringExtra("ordcode_key")

        val uriData = intent.data
        val status = uriData?.getQueryParameter("status")?.toInt()

        if(status != null) {
            when(status) {
                1 -> {
                    finish()
                }
                2 -> Toast.makeText(this@QrActivity, R.string.msg_payment_cancel, Toast.LENGTH_SHORT).show()
            }
        }

        binding.webview.run {
            webViewClient = customWebClient(this@QrActivity)
            webChromeClient = WebChromeClient()
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true
            settings.supportZoom()
            settings.javaScriptEnabled = true
            settings.supportMultipleWindows()
            settings.javaScriptCanOpenWindowsAutomatically = true
        }

        binding.webview.loadUrl(AppProperties.SERVER + "m/pg.php?useridx=${MyApplication.useridx}&ordcode=$ordcode_key")

        binding.back.setOnClickListener { finish() }

    }

    class customWebClient(val context: Context) : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            val intent = parse(url)
            if (intent != null) {
                val uriData = intent.data
                val status = uriData?.getQueryParameter("status")?.toInt()

                if (status != null) {
                    when (status) {
                        1 -> {
                            context.startActivity(Intent(context, ByHistoryActivity::class.java))
                        }
                        2 -> Toast.makeText(
                            context,
                            R.string.msg_payment_cancel,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            return true
        }
        private fun parse(url: String): Intent? {
            return try {
                Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
            } catch (e: URISyntaxException) {
                null
            }
        }
    }
}