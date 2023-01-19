package com.wooriyo.pinmenuer.util

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.wooriyo.pinmenuer.config.AppProperties
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    val gson = GsonBuilder().setLenient().create()

    val service: Api = initService()

    private fun initService() : Api =
        Retrofit.Builder()
            .baseUrl(AppProperties.SERVER)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(Api::class.java)
}