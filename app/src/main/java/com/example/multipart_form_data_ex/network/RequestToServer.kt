package com.example.multipart_form_data_ex.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val BASE_URL = "http://13.209.144.115:3002"
object RequestToServer {

    var retrofit = Retrofit.Builder()
        .baseUrl("http://13.209.144.115:3002") .addConverterFactory(GsonConverterFactory.create()) .build()
    var service: RequestInterface = retrofit.create(RequestInterface::class.java)


}
