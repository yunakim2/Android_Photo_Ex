package com.example.multipart_form_data_ex.data


data class ResponseProfileData(
    val status : Int,
    val success : Boolean,
    val message : String,
    val data : ArrayList<ResponseImg>
)
data class ResponseImg(
    val name : String,
    val email : String,
    val phone : String,
    val profile: String
)