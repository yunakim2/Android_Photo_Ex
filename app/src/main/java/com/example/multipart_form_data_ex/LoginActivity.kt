package com.example.multipart_form_data_ex

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.multipart_form_data_ex.data.RequestLoginData
import com.example.multipart_form_data_ex.data.ResponseLoginData
import com.example.multipart_form_data_ex.network.RequestToServer
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {


    lateinit var loginData : RequestLoginData
    var id : String? = null
    var pw : String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val pref : SharedPreferences = getSharedPreferences("pref", Context.MODE_PRIVATE)
        val editor : SharedPreferences.Editor = pref.edit()
        btn_login.setOnClickListener {


            val requestToServer= RequestToServer
            requestToServer.service.login( RequestLoginData(
                id = edit_id.text.toString() , password = edit_pw.text.toString()
            ))
                .enqueue(
                    object : Callback<ResponseLoginData> {
                        override fun onFailure(call: Call<ResponseLoginData>, t: Throwable) {
                            Log.d("통신실패", "${t}")
                        }

                        override fun onResponse(
                            call: Call<ResponseLoginData>,
                            response: Response<ResponseLoginData>
                        ) {
                            if (response.isSuccessful) {

                                if(response.body()!!.success==true)
                                {
                                    id = edit_id.text.toString()
                                    pw = edit_pw.text.toString()
                                    val intent = Intent(applicationContext, MainActivity::class.java)
                                    intent.putExtra("id",id)
                                    intent.putExtra("pw",pw)
                                    intent.putExtra("jwt",response.body()!!.data.jwt)


                                    Log.d("jwt", "${response.body()!!.data.jwt}")
                                    //값 저장
                                //    editor.putString("jwt",response.body()!!.data.jwt)
                                    startActivity(intent)
                                }

                            }

                        }
                    }
                )


        }



    }
}
