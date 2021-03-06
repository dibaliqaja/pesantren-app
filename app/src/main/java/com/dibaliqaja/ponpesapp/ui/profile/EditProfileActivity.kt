package com.dibaliqaja.ponpesapp.ui.profile

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.dibaliqaja.ponpesapp.LoginActivity
import com.dibaliqaja.ponpesapp.R
import com.dibaliqaja.ponpesapp.databinding.ActivityEditProfileBinding
import com.dibaliqaja.ponpesapp.helper.Constant
import com.dibaliqaja.ponpesapp.helper.FormattedDateMatcher
import com.dibaliqaja.ponpesapp.helper.PreferencesHelper
import com.dibaliqaja.ponpesapp.model.ProfileResponse
import com.dibaliqaja.ponpesapp.services.RetrofitClient
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.datepicker.MaterialDatePicker
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody.Part.Companion.createFormData
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var preferencesHelper: PreferencesHelper
    private var imageUri: Uri? = null
    private var currentSelectedDate: Long? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferencesHelper = PreferencesHelper(this)

        checkConnectivity()

        if (!preferencesHelper.getBoolean(Constant.prefIsLogin)) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            preferencesHelper.getString(Constant.prefToken)?.let { getProfile(it) }
        }

        binding.apply {
            fabPhoto.setOnClickListener {
                requestPermission()
                ImagePicker.with(this@EditProfileActivity)
                    .compress(1024)
                    .galleryMimeTypes(arrayOf("image/png", "image/jpeg", "image/jpg"))
                    .start()
            }
            btnDate.setOnClickListener { showDatePicker() }
            btnSave.setOnClickListener {
                if (checkFields()) updateProfile()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                val uri: Uri = data?.data!!
                imageUri = uri
                binding.ivPhoto.setImageURI(imageUri)
            }
            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showDatePicker() {
        val selectedDateInMillis = currentSelectedDate ?: System.currentTimeMillis()

        MaterialDatePicker.Builder.datePicker().setSelection(selectedDateInMillis).build().apply {
            addOnPositiveButtonClickListener { dateInMillis -> onDateSelected(dateInMillis) }
        }.show(supportFragmentManager, MaterialDatePicker::class.java.canonicalName)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun onDateSelected(dateTimeStampInMillis: Long) {
        currentSelectedDate = dateTimeStampInMillis
        val dateTime: LocalDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(currentSelectedDate!!), ZoneId.systemDefault())
        val dateAsFormattedText: String = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        binding.edtBirthDate.setText(dateAsFormattedText)
    }

    private fun getProfile(token: String) {
        RetrofitClient.apiService.getProfile("Bearer $token").enqueue(object :
            Callback<ProfileResponse> {
            @SuppressLint("SimpleDateFormat")
            override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                val listResponse = response.body()?.data
                val email: String = listResponse!!.email
                val name: String = listResponse.name
                val address: String = listResponse.address
                val birthPlace: String = listResponse.birthPlace
                val birthDate: Date = listResponse.birthDate
                val newBirthDate = SimpleDateFormat("yyyy-MM-dd").format(birthDate)
                val phone: String = listResponse.phone
                val schoolOld: String = listResponse.schoolOld
                val schoolAddressOld: String = listResponse.schoolAddressOld
                val schoolCurrent: String = listResponse.schoolCurrent
                val schoolAddressCurrent: String = listResponse.schoolAddressCurrent
                val fatherName: String = listResponse.fatherName
                val motherName: String = listResponse.motherName
                val fatherJob: String = listResponse.fatherJob
                val motherJob: String = listResponse.motherJob
                val parentPhone: String = listResponse.parentPhone
                val entryYear: Int = listResponse.entryYear
                val yearOut: Int? = listResponse.yearOut
                val photo: String? = listResponse.photo

                binding.apply {
                    edtEmail.setText(email)
                    edtName.setText(name)
                    edtAddress.setText(address)
                    edtBirthPlace.setText(birthPlace)
                    edtBirthDate.setText(newBirthDate)
                    edtPhone.setText(phone)
                    edtSchoolOld.setText(schoolOld)
                    edtSchoolAddressOld.setText(schoolAddressOld)
                    edtSchoolCurrent.setText(schoolCurrent)
                    edtSchoolAddressCurrent.setText(schoolAddressCurrent)
                    edtFatherName.setText(fatherName)
                    edtMotherName.setText(motherName)
                    edtFatherJob.setText(fatherJob)
                    edtMotherJob.setText(motherJob)
                    edtParentPhone.setText(parentPhone)
                    edtEntryYear.setText(entryYear.toString())
                    if(yearOut !== null) {
                        edtYearOut.setText(yearOut.toString())
                    } else { edtYearOut.setText("") }

                    Glide.with(this@EditProfileActivity)
                        .load(photo)
                        .placeholder(R.drawable.profile_placeholder)
                        .centerCrop()
                        .into(ivPhoto)
                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                Toast.makeText(applicationContext,"Something went wrong", Toast.LENGTH_SHORT).show()
                Log.e("Failure: ", t.message.toString())
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateProfile() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        progressDialog.show()

        val rEmail = binding.edtEmail.text.toString().trim().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val rName = binding.edtName.text.toString().trim().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val rAddress = binding.edtAddress.text.toString().trim().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val rBirthPlace = binding.edtBirthPlace.text.toString().trim().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val rBirthDate = binding.edtBirthDate.text.toString().trim().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val rPhone = binding.edtPhone.text.toString().trim().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val rSchoolOld = binding.edtSchoolOld.text.toString().trim().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val rSchoolAddressOld = binding.edtSchoolAddressOld.text.toString().trim().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val rSchoolCurrent = binding.edtSchoolCurrent.text.toString().trim().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val rSchoolAddressCurrent = binding.edtSchoolAddressCurrent.text.toString().trim().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val rFatherName = binding.edtFatherName.text.toString().trim().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val rMotherName = binding.edtMotherName.text.toString().trim().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val rFatherJob = binding.edtFatherJob.text.toString().trim().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val rMotherJob = binding.edtMotherJob.text.toString().trim().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val rParentPhone = binding.edtParentPhone.text.toString().trim().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val rEntryYear = binding.edtEntryYear.text.toString().trim().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val rYearOut = binding.edtYearOut.text.toString().trim().toRequestBody("multipart/form-data".toMediaTypeOrNull())

        var file: File? = null
        if (imageUri !== null) {
            val realPath = getRealPath(this, imageUri)
            file = File(realPath)
        }
        val requestFile = file?.asRequestBody("multipart/form-data".toMediaType())
        val imagePart = requestFile?.let { createFormData("photo", file?.name, it) }

        if (progressDialog.isShowing) progressDialog.dismiss()
        RetrofitClient.apiService.postUpdateProfile(
            "Bearer " + preferencesHelper.getString(Constant.prefToken),
            rEmail,
            rName,
            rAddress,
            rBirthPlace,
            rBirthDate,
            rPhone,
            rSchoolOld,
            rSchoolAddressOld,
            rSchoolCurrent,
            rSchoolAddressCurrent,
            rFatherName,
            rMotherName,
            rFatherJob,
            rMotherJob,
            rParentPhone,
            rEntryYear,
            rYearOut,
            imagePart,
        ).enqueue(object :
            Callback<ProfileResponse> {
            override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                if (response.isSuccessful) {
                    if (progressDialog.isShowing) progressDialog.dismiss()
                    finish()
                    startActivity(Intent(baseContext, ProfileActivity::class.java))
                    Toast.makeText(baseContext, "Successfully updated", Toast.LENGTH_LONG).show()
                } else {
                    try {
                        val jsonObject = JSONObject(response.errorBody()!!.string()).getJSONObject("data")
                        if (jsonObject.has("email")) {
                            val email: String = jsonObject.getJSONArray( "email")[0].toString()
                            binding.tilEmail.error = email
                        }
                        if (jsonObject.has("name")) {
                            val name: String = jsonObject.getJSONArray( "name")[0].toString()
                            binding.tilName.error = name
                        }
                        if (jsonObject.has("address")) {
                            val address: String = jsonObject.getJSONArray( "address")[0].toString()
                            binding.tilAddress.error = address
                        }
                        if (jsonObject.has("birth_place")) {
                            val birthPlace: String = jsonObject.getJSONArray( "birth_place")[0].toString()
                            binding.tilBirthPlace.error = birthPlace
                        }
                        if (jsonObject.has("birth_date")) {
                            val birthDate: String = jsonObject.getJSONArray( "birth_date")[0].toString()
                            binding.tilBirthDate.error = birthDate
                        }
                        if (jsonObject.has("phone")) {
                            val phone: String = jsonObject.getJSONArray( "phone")[0].toString()
                            binding.tilPhone.error = phone
                        }
                        if (jsonObject.has("school_old")) {
                            val schoolOld: String = jsonObject.getJSONArray( "school_old")[0].toString()
                            binding.tilSchoolOld.error = schoolOld
                        }
                        if (jsonObject.has("school_address_old")) {
                            val schoolAddressOld: String = jsonObject.getJSONArray( "school_address_old")[0].toString()
                            binding.tilSchoolAddressOld.error = schoolAddressOld
                        }
                        if (jsonObject.has("school_current")) {
                            val schoolCurrent: String = jsonObject.getJSONArray( "school_current")[0].toString()
                            binding.tilSchoolCurrent.error = schoolCurrent
                        }
                        if (jsonObject.has("school_address_current")) {
                            val schoolAddressCurrent: String = jsonObject.getJSONArray( "school_address_current")[0].toString()
                            binding.tilSchoolAddressCurrent.error = schoolAddressCurrent
                        }
                        if (jsonObject.has("father_name")) {
                            val fatherName: String = jsonObject.getJSONArray( "father_name")[0].toString()
                            binding.tilFatherName.error = fatherName
                        }
                        if (jsonObject.has("mother_name")) {
                            val motherName: String = jsonObject.getJSONArray( "mother_name")[0].toString()
                            binding.tilMotherName.error = motherName
                        }
                        if (jsonObject.has("father_job")) {
                            val fatherJob: String = jsonObject.getJSONArray( "father_job")[0].toString()
                            binding.tilFatherJob.error = fatherJob
                        }
                        if (jsonObject.has("mother_job")) {
                            val motherJob: String = jsonObject.getJSONArray( "mother_job")[0].toString()
                            binding.tilMotherJob.error = motherJob
                        }
                        if (jsonObject.has("parent_phone")) {
                            val parentPhone: String = jsonObject.getJSONArray( "parent_phone")[0].toString()
                            binding.tilParentPhone.error = parentPhone
                        }
                        if (jsonObject.has("entry_year")) {
                            val entryYear: String = jsonObject.getJSONArray( "entry_year")[0].toString()
                            binding.tilEntryYear.error = entryYear
                        }
                        if (jsonObject.has("year_out")) {
                            val yearOut: String = jsonObject.getJSONArray( "year_out")[0].toString()
                            binding.tilYearOut.error = yearOut
                        }
                    } catch (e: JSONException) {
                        Log.e("Response Error: ", e.toString())
                    }
                }

                if (response.code() == 401) {
                    preferencesHelper.clear()
                    Toast.makeText(applicationContext,"Token expired", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@EditProfileActivity, LoginActivity::class.java))
                    finish()
                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                if (progressDialog.isShowing) progressDialog.dismiss()
                Toast.makeText(applicationContext,"Something went wrong", Toast.LENGTH_SHORT).show()
                Log.e("Failure: ", t.message.toString())
            }

        })
    }

    private fun checkFields(): Boolean {
        binding.apply {
            if (edtEmail.text!!.isEmpty()) {
                tilEmail.error = "This field is required"
                return false
            } else { tilEmail.error = null }

            if (edtName.text!!.isEmpty()) {
                tilName.error = "This field is required"
                return false
            } else { tilName.error = null }

            if (edtAddress.text!!.isEmpty()) {
                tilAddress.error = "This field is required"
                return false
            } else { tilAddress.error = null }

            if (edtBirthPlace.text!!.isEmpty()) {
                tilBirthPlace.error = "This field is required"
                return false
            } else { tilBirthPlace.error = null }

            if (edtBirthDate.text!!.isEmpty()) {
                tilBirthDate.error = "This field is required"
                return false
            } else if (!FormattedDateMatcher().matches(edtBirthDate.text!!.toString())) {
                tilBirthDate.error = "Tanggal lahir tidak valid"
                return false
            } else { tilBirthDate.error = null }

            if (edtPhone.text!!.isEmpty()) {
                tilPhone.error = "This field is required"
                return false
            } else { tilPhone.error = null }

            if (edtSchoolOld.text!!.isEmpty()) {
                tilSchoolOld.error = "This field is required"
                return false
            } else { tilSchoolOld.error = null }

            if (edtSchoolAddressOld.text!!.isEmpty()) {
                tilSchoolAddressOld.error = "This field is required"
                return false
            } else { tilSchoolAddressOld.error = null }

            if (edtSchoolCurrent.text!!.isEmpty()) {
                tilSchoolCurrent.error = "This field is required"
                return false
            } else { tilSchoolCurrent.error = null }

            if (edtSchoolAddressCurrent.text!!.isEmpty()) {
                tilSchoolAddressCurrent.error = "This field is required"
                return false
            } else { tilSchoolAddressCurrent.error = null }

            if (edtFatherName.text!!.isEmpty()) {
                tilFatherName.error = "This field is required"
                return false
            } else { tilFatherName.error = null }

            if (edtMotherName.text!!.isEmpty()) {
                tilMotherName.error = "This field is required"
                return false
            } else { tilMotherName.error = null }

            if (edtFatherJob.text!!.isEmpty()) {
                tilFatherJob.error = "This field is required"
                return false
            } else { tilFatherJob.error = null }

            if (edtMotherJob.text!!.isEmpty()) {
                tilMotherJob.error = "This field is required"
                return false
            } else { tilMotherJob.error = null }

            if (edtParentPhone.text!!.isEmpty()) {
                tilParentPhone.error = "This field is required"
                return false
            } else { tilParentPhone.error = null }

            if (edtEntryYear.text!!.isEmpty()) {
                tilEntryYear.error = "This field is required"
                return false
            } else { tilEntryYear.error = null }

            return true
        }
    }

    private fun getRealPath(context: Context, uri: Uri?): String {
        var filePath = ""
        val wholeID = DocumentsContract.getDocumentId(uri)

        // Split at colon, use second item in the array
        val id = wholeID.split(":").toTypedArray()[1]
        val column = arrayOf(MediaStore.Images.Media.DATA)

        // where id is equal to
        val sel = MediaStore.Images.Media._ID + "=?"
        val cursor: Cursor? = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            column, sel, arrayOf(id), null
        )
        val columnIndex = cursor?.getColumnIndex(column[0])
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                filePath = columnIndex?.let { cursor.getString(it) }.toString()
            }
        }
        cursor?.close()
        return filePath
    }

    private fun hasPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        val permission = mutableListOf<String>()
        if (!hasPermission()) {
            permission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            permission.add(Manifest.permission.CAMERA)
        }
        if (permission.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permission.toTypedArray(), 0)
        }
    }

    private fun checkConnectivity() {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        val isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting
        if (!isConnected) {
            AlertDialog.Builder(this).setTitle("No Internet Connection")
                .setMessage("Please check your internet connection and try again")
                .setPositiveButton(android.R.string.ok) { _, _ -> }.show()
        }
    }
}