package com.example.multipart_form_data_ex.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RequestToServerOkHttp {
    val okHttpClient = OkHttpClient.Builder().build()
    val retrofit = Retrofit.Builder().baseUrl(BASE_URL).client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create()).build()
    var service: RequestInterface = retrofit.create(RequestInterface::class.java)

}