package com.example.multipart_form_data_ex.data

data class ResponseLoginData(
    val status : Int,
    val success : Boolean,
    val message : String,
    val data : ResponseLogin
)
data class ResponseLogin(
    val jwt : String
)