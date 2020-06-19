package com.example.multipart_form_data_ex

import android.Manifest
import android.Manifest.permission.CAMERA
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.example.multipart_form_data_ex.data.ResponseProfileData
import com.example.multipart_form_data_ex.data.ResponseUserData
import com.example.multipart_form_data_ex.network.RequestInterface
import com.example.multipart_form_data_ex.network.RequestToServer
import com.example.multipart_form_data_ex.network.RequestToServerOkHttp
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {


    private val PICK_FROM_CAMERA = 0
    private val PICK_FROM_ALBUM = 1
    private val REQUEST_IMAGE_CROP = 2

    lateinit var mFileName : String
    lateinit var mCurrentPhotoPath : String
    lateinit var imageUri : Uri
    lateinit var photoURI  : Uri
    lateinit var  albumURI : Uri
    lateinit var jwt : String
    lateinit var name : String
    lateinit var email : String
    lateinit var phone : String
    lateinit var  profile : String
    lateinit var file:File


    private val MY_PERMISSION_CAMERA = 4
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val builder : StrictMode.VmPolicy.Builder  =StrictMode.VmPolicy.Builder ()
        StrictMode.setVmPolicy(builder.build())

       // file = File(this.filesDir, this.filesDir.invariantSeparatorsPath+".png")
        tv_pw.text = intent.getStringExtra("pw").toString()
        tv_id.text = intent.getStringExtra("id").toString()
        jwt = intent.getStringExtra("jwt").toString()
        init()
        initPermission()

        btn_camera.setOnClickListener {
            capturCamera()
        }

        btn_gallery.setOnClickListener {
            getAlbum()
        }
        checkPermission()

    }
    fun init()
    {

        val requestToServer= RequestToServer
                requestToServer.service.getUserProfile(content = "multipart/form-data",jwt = jwt)
                    .enqueue(
                        object : Callback<ResponseUserData> {
                            override fun onFailure(call: Call<ResponseUserData>, t: Throwable) {
                                Log.d("통신실패", "${t}")
                            }

                            override fun onResponse(
                                call: Call<ResponseUserData>,
                                response: Response<ResponseUserData>
                            ) {
                                if (response.isSuccessful) {

                                    Log.d("login", "${response.body()!!.data.id.toString()}")
                                    name = response.body()!!.data.name
                                    email = response.body()!!.data.email
                                    phone = response.body()!!.data.phone
                                    profile = response.body()!!.data.image

                                    Glide.with(this@MainActivity).load(response.body()!!.data.image).into(img)
                                }

                            }
                        }
                    )

    }
    fun initPermission() {
        // 카메라 권한 설정
        val MY_CAMERA_REQUST_CODE = 100

        if (ContextCompat.checkSelfPermission(
                applicationContext,
                CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this ,CAMERA
                )
            ) {
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(CAMERA),MY_CAMERA_REQUST_CODE)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.size> 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show()

                // main logic
            } else {
            Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show()}

    }


    fun capturCamera()
    {
        val state = Environment.getExternalStorageState()
        if(Environment.MEDIA_MOUNTED.equals(state)) {
            val Photointent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (Photointent.resolveActivity(packageManager) != null)
            {
                var photoFile: File? = null
                try{
                    photoFile = createImageFile()
                }catch (ex : IOException)
                {
                    Log.e("errer","errer")
                }
                if(photoFile!=null)
                {
                    val providerURI = FileProvider.getUriForFile(applicationContext,"com.example.multipart_form_data_ex.fileprovider",photoFile)
                    imageUri = providerURI
                    Photointent.putExtra(MediaStore.EXTRA_OUTPUT,providerURI)
                    startActivityForResult(Photointent,PICK_FROM_CAMERA)
                }
                else
                {
                    Toast.makeText(this, "저장공간 접근 불가능한 기기입니다.", Toast.LENGTH_SHORT).show()
                    return
                }

            }
        }
    }

    @Throws(IOException::class)
    fun createImageFile(): File? {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_${timeStamp}.jpg"
        mFileName = imageFileName
        var imageFile: File? = null
        val storageDir = File(
            Environment.getExternalStorageDirectory().toString() + "/Pictures", "yuna"
        )
        if (!storageDir.exists()) {
            Log.i("mCurrentPhotoPath1", storageDir.toString())
            storageDir.mkdirs()
        }
        imageFile = File(storageDir, imageFileName)
        mCurrentPhotoPath = imageFile.absolutePath
        return imageFile
    }
    fun getAlbum()
    {
        Log.i("getAlbum","Call")
        val intent = Intent(Intent.ACTION_PICK)
        intent.setType("image/*")
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE)
        startActivityForResult(intent, PICK_FROM_ALBUM)

    }
    fun galleryAddPic()
    {
        val mediaIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val f :File = File(mCurrentPhotoPath)
        file = f
        Log.d("mediaIntent", "${f}")
        val contentUri = Uri.fromFile(f)
        mediaIntent.setData(contentUri)
        sendBroadcast(mediaIntent)
        Toast.makeText(this, "사진이 앨범에 저장되었습니다.", Toast.LENGTH_SHORT).show()
    }
    fun cropImage()
    {
        val cropIntent = Intent("com.android.camera.action.CROP")
        cropIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        cropIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        cropIntent.setDataAndType(photoURI, "image/*")
        //cropIntent.putExtra("outputX", 200) // crop한 이미지의 x축 크기, 결과물의 크기
        //cropIntent.putExtra("outputY", 200) // crop한 이미지의 y축 크기
        cropIntent.putExtra("aspectX", 1) // crop 박스의 x축 비율, 1&1이면 정사각형
        cropIntent.putExtra("aspectY", 1) // crop 박스의 y축 비율
        cropIntent.putExtra("scale", true)
        cropIntent.putExtra("output", albumURI) // 크랍된 이미지를 해당 경로에 저장
        startActivityForResult(cropIntent, REQUEST_IMAGE_CROP)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            PICK_FROM_CAMERA -> {
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        galleryAddPic()
                        Glide.with(this).load(imageUri).into(img)

                        postProfile(imageUri)


                    } catch (e: Exception) {

                    }
                } else {
                    Toast.makeText(this, "사진찍기를 취소하였습니다.", Toast.LENGTH_SHORT).show()
                }

            }
            PICK_FROM_ALBUM -> {
                if (resultCode == Activity.RESULT_OK) {
                    if (data!!.data != null) {
                        try {
                            var albumFile: File? = null
                            albumFile = createImageFile()
                            photoURI = data!!.data!!
                            albumURI = Uri.fromFile(albumFile)
                            cropImage()
                        } catch (e: Exception) {

                        }
                    }
                }

            }
            REQUEST_IMAGE_CROP -> {
                if (resultCode == Activity.RESULT_OK) {
                    galleryAddPic()
                    Glide.with(this).load(albumURI).into(img)


                    postProfile(albumURI)
                }
            }

        }
    }

    fun postProfile(uri: Uri)
    {


        val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,uri)
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG,100,stream)
        val byteArray = stream.toByteArray()


        val fileReqBody = RequestBody.create(MediaType.parse("image/png"),byteArray)

        val part = MultipartBody.Part.createFormData("profile",mFileName,fileReqBody)
        val description = RequestBody.create(MediaType.parse("text/plain"),"image-type")

        val requestToServer=RequestToServerOkHttp
                requestToServer.service.profile(content = "multipart/form-data",jwt = jwt,file = part,requestBody = description)
                    .enqueue(
                        object : Callback<ResponseProfileData> {
                            override fun onFailure(call: Call<ResponseProfileData>, t: Throwable) {
                                Log.d("통신실패", "${t}")
                            }

                            override fun onResponse(
                                call: Call<ResponseProfileData>,
                                response: Response<ResponseProfileData>
                            ) {
                                if (response.isSuccessful) {
                                    Log.d("이미지 서버연결 성공", "${response.body()!!.message}")
                                    if(response.body()!!.success)
                                    {
                                        Toast.makeText(applicationContext, "이미지 서버 업로드 성공", Toast.LENGTH_LONG).show()
                                    }

                                }

                            }
                        }
                    )




    }

        fun checkPermission() {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // 처음 호출시엔 if()안의 부분은 false로 리턴 됨 -> else{..}의 요청으로 넘어감
                if ((ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )) ||
                    (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.CAMERA
                    ))
                ) {

                } else {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA
                        ),
                        MY_PERMISSION_CAMERA
                    );
                }
            }
        }
    }













