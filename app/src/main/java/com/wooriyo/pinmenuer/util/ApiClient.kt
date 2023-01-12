package com.wooriyo.pinmenuer.util

import com.wooriyo.pinmenuer.config.AppProperties
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    val service: Api = initService()

    private fun initService() : Api =
        Retrofit.Builder()
            .baseUrl(AppProperties.SERVER)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Api::class.java)
}