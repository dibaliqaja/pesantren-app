package com.dibaliqaja.ponpesapp.ui.profile

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.dibaliqaja.ponpesapp.LoginActivity
import com.dibaliqaja.ponpesapp.R
import com.dibaliqaja.ponpesapp.databinding.ActivityProfileBinding
import com.dibaliqaja.ponpesapp.helper.Constant
import com.dibaliqaja.ponpesapp.helper.PreferencesHelper
import com.dibaliqaja.ponpesapp.helper.formatDate
import com.dibaliqaja.ponpesapp.model.ProfileResponse
import com.dibaliqaja.ponpesapp.services.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var preferencesHelper: PreferencesHelper
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferencesHelper = PreferencesHelper(this)

        preferencesHelper.getString(Constant.prefToken)?.let { getProfile(it) }

        swipeRefreshLayout = binding.swipeRefresh
        swipeRefreshLayout.setOnRefreshListener {
            try {
                preferencesHelper.getString(Constant.prefToken)?.let { getProfile(it) }
                swipeRefreshLayout.isRefreshing = false
            } catch (e: Exception) {
                swipeRefreshLayout.isRefreshing = false
                preferencesHelper.clear()
                Log.e("Failure: ", e.message.toString())
            }
        }

        binding.apply {
            btnEditProfil.setOnClickListener {
                startActivity(Intent(baseContext, EditProfileActivity::class.java))
            }
            btnLogout.setOnClickListener { logout() }
        }
    }

    override fun onStart() {
        super.onStart()
        if (!preferencesHelper.getBoolean(Constant.prefIsLogin)) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun getProfile(token: String) {
        RetrofitClient.apiService.getProfile("Bearer $token").enqueue(object:
            Callback<ProfileResponse> {
            @SuppressLint("SimpleDateFormat")
            override fun onResponse(
                call: Call<ProfileResponse>,
                response: Response<ProfileResponse>
            ) {
                val profileResponse = response.body()?.data
                val email: String = profileResponse!!.email
                val name: String = profileResponse!!.name
                val address: String = profileResponse.address
                val birthPlace: String = profileResponse.birthPlace
                val birthDate: Date = profileResponse.birthDate
                val newBirthDate = formatDate(birthDate)
                val phone: String = profileResponse.phone
                val schoolOld: String = profileResponse.schoolOld
                val schoolAddressOld: String = profileResponse.schoolAddressOld
                val schoolCurrent: String = profileResponse.schoolCurrent
                val schoolAddressCurrent: String = profileResponse.schoolAddressCurrent
                val fatherName: String = profileResponse.fatherName
                val motherName: String = profileResponse.motherName
                val fatherJob: String = profileResponse.fatherJob
                val motherJob: String = profileResponse.motherJob
                val parentPhone: String = profileResponse.parentPhone
                val entryYear: Int = profileResponse.entryYear
                val yearOut: Int? = profileResponse.yearOut
                val photo: String? = profileResponse.photo

                binding.apply {
                    tvEmail.text = email
                    tvName.text = name
                    tvAddress.text = address
                    tvBirthPlace.text = birthPlace
                    tvBirthDate.text = newBirthDate
                    tvPhone.text = phone
                    tvSchoolOld.text = schoolOld
                    tvSchoolAddressOld.text = schoolAddressOld
                    tvSchoolCurrent.text = schoolCurrent
                    tvSchoolAddressCurrent.text = schoolAddressCurrent
                    tvFatherName.text = fatherName
                    tvMotherName.text = motherName
                    tvFatherJob.text = fatherJob
                    tvMotherJob.text = motherJob
                    tvParentPhone.text = parentPhone
                    tvEntryYear.text = entryYear.toString()
                    if(yearOut !== null) {
                        tvYearOut.text = yearOut.toString()
                    } else {
                        tvYearOut.text = "-"
                    }

                    Glide.with(this@ProfileActivity)
                        .load(photo)
                        .placeholder(R.drawable.profile_placeholder)
                        .centerCrop()
                        .into(ivPhoto)
                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                preferencesHelper.clear()
                Log.e("Failure: ", t.message.toString())
            }

        })
    }

    private fun logout() {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Apakah anda yakin?")
        alertDialog.setPositiveButton("Ya") { _, _ ->
            preferencesHelper.clear()
            Toast.makeText(this, "Logout success.", Toast.LENGTH_LONG).show()
            startActivity(Intent(baseContext, LoginActivity::class.java))
            finish()
        }
        alertDialog.setNegativeButton("Tidak") { dialog, _ ->
            dialog.cancel()
        }
        alertDialog.show()
    }
}