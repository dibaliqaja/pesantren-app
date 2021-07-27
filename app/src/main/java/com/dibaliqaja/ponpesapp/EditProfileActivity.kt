package com.dibaliqaja.ponpesapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.dibaliqaja.ponpesapp.databinding.ActivityEditProfileBinding
import com.dibaliqaja.ponpesapp.helper.Constant
import com.dibaliqaja.ponpesapp.helper.PreferencesHelper
import com.dibaliqaja.ponpesapp.model.ProfileResponse
import com.dibaliqaja.ponpesapp.services.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var preferencesHelper: PreferencesHelper
    // private var currentSelectedDate: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferencesHelper = PreferencesHelper(this)

        getProfile()
    }

    private fun getProfile() {
        RetrofitClient.apiService.getProfile("Bearer " + preferencesHelper.getString(Constant.prefToken)).enqueue(object :
            Callback<ProfileResponse> {
            override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                val listResponse = response.body()?.data
                val name: String = listResponse!!.name
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
                    } else {
                        edtYearOut.setText("")
                    }

                    Glide.with(this@EditProfileActivity)
                        .load(photo)
                        .placeholder(R.drawable.profile_placeholder)
                        .centerCrop()
                        .into(ivPhoto)
                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                Log.d("Failure ", t.message.toString())
            }
        })
    }
}