package com.example.multipart_form_data_ex.data

import android.provider.ContactsContract

data class RequestProfileData(
    val name : String,
    val email: String,
    val phone : String,
    val profile : String
)