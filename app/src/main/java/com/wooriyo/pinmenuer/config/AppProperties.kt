package com.wooriyo.pinmenuer.config

class AppProperties {
    companion object {

        private const val TEST_SERVER: String = "http://app.pinmenu.biz/api/"
        private const val WEB_SERVER: String = "http://pinmenu.biz/api/"

        const val SERVER: String = TEST_SERVER
        const val IMG_SERVER: String = WEB_SERVER

        const val KAKAO_URL : String = "https://dapi.kakao.com"

        // 리사이클러뷰 멀티뷰 사용시 타입
        const val VIEW_TYPE_COM = 0
        const val VIEW_TYPE_ADD = 1
        const val VIEW_TYPE_EMPTY = 2

        // 권한 관련
        const val REQUEST_LOCATION = 0
        const val REQUEST_ENABLE_BT = 1

        //프린트 관련
        const val FONT_BIG = 37
        const val FONT_SMALL = 28
        const val FONT_WIDTH = 512

        const val HANGUL_SIZE_BIG = 3.6
        const val HANGUL_SIZE_SMALL = 3.5

        const val ONE_LINE_BIG = 41
        const val ONE_LINE_SMALL = 53

        const val HYPHEN_NUM_BIG = 50
        const val HYPHEN_NUM_SMALL = 66

        const val SPACE_BIG = 3
        const val SPACE_SMALL = 5

        const val TITLE_MENU = "메     뉴     명                                수량     구분"
    }
}