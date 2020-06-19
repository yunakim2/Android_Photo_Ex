package com.example.multipart_form_data_ex.data

data class ResponseUserData(
    val status : Int,
    val success : Boolean,
    val message : String,
    val data  : User
)
data class  User(
    val id : String,
    val name : String,
    val email : String,
    val phone : String,
    val image : String
)