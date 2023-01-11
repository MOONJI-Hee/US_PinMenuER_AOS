package com.wooriyo.pinmenuer.util

import java.text.DecimalFormat

class AppHelper {
    companion object {

        val dec = DecimalFormat("#,##0.###")

        // 천 단위 콤마 찍기
        fun price(n: Int): String {
            val result = dec.format(n)
            return result
        }

    }
}