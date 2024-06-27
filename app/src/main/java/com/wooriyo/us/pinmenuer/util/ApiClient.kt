package com.wooriyo.us.pinmenuer.util

import com.wooriyo.us.pinmenuer.config.AppProperties
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    val service: Api = initService()
    val imgService: Api = imgService()

    private fun initService() : Api =
        Retrofit.Builder()
            .baseUrl(AppProperties.SERVER)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Api::class.java)

    private fun imgService() : Api =
        Retrofit.Builder()
            .baseUrl(AppProperties.IMG_SERVER)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Api::class.java)
}