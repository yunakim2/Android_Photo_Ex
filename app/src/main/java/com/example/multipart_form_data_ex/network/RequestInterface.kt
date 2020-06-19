package com.example.multipart_form_data_ex.network

import com.example.multipart_form_data_ex.data.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


interface RequestInterface {


    //로그인

    @POST("/user/signin")
    fun login(@Body body: RequestLoginData) : Call<ResponseLoginData>

    //프로필 수정

    @Multipart
    @POST("/user/profile")
    fun profile(@Header("Contnet-Type") content : String, @Header("jwt") jwt : String, @Part file : MultipartBody.Part,@Part("profile") requestBody: RequestBody )
            : Call<ResponseProfileData>



    @GET("/user/profile")
    fun getUserProfile(@Header("Content-Type") content : String, @Header("jwt") jwt : String) : Call<ResponseUserData>

}