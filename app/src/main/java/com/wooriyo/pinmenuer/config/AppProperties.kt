package com.wooriyo.pinmenuer.config

class AppProperties {
    companion object {

        private const val TEST_SERVER: String = "http://app.pinmenu.biz/api/"
        private const val WEB_SERVER: String = "http://pinmenu.biz/api/"

        const val SERVER: String = TEST_SERVER
        const val IMG_SERVER: String = WEB_SERVER

        const val KAKAO_URL : String = "https://dapi.kakao.com"
    }
}