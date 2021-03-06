package com.dibaliqaja.ponpesapp.ui.profile

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
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
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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

        checkConnectivity()

        if (!preferencesHelper.getBoolean(Constant.prefIsLogin)) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            preferencesHelper.getString(Constant.prefToken)?.let { getProfile(it) }
        }

        swipeRefreshLayout = binding.swipeRefresh
        swipeRefreshLayout.setOnRefreshListener {
            try {
                preferencesHelper.getString(Constant.prefToken)?.let { getProfile(it) }
                swipeRefreshLayout.isRefreshing = false
            } catch (e: Exception) {
                swipeRefreshLayout.isRefreshing = false
                Toast.makeText(applicationContext,"Something went wrong", Toast.LENGTH_SHORT).show()
                Log.e("Failure: ", e.message.toString())
            }
        }

        binding.apply {
            btnEditProfil.setOnClickListener {
                startActivity(Intent(baseContext, EditProfileActivity::class.java))
                finish()
            }
            btnLogout.setOnClickListener { logout() }
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
                if (response.isSuccessful) {
                    val profileResponse = response.body()?.data
                    val email: String = profileResponse!!.email
                    val name: String = profileResponse.name
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
                        if (yearOut !== null) {
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
                } else {
                    Log.e("Response Error: ", response.errorBody().toString())
                }

                if (response.code() == 401) {
                    preferencesHelper.clear()
                    Toast.makeText(applicationContext,"Token expired", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@ProfileActivity, LoginActivity::class.java))
                    finish()
                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                Toast.makeText(applicationContext,"Something went wrong", Toast.LENGTH_SHORT).show()
                Log.e("Failure: ", t.message.toString())
            }

        })
    }

    private fun getLogout(token: String) {
        RetrofitClient.apiService.logout("Bearer $token").enqueue(object:
            Callback<JSONObject> {
            override fun onResponse(
                call: Call<JSONObject>,
                response: Response<JSONObject>
            ) {
                if (response.isSuccessful) {
                    preferencesHelper.clear()
                    Toast.makeText(applicationContext, "Logout berhasil.", Toast.LENGTH_LONG).show()
                    startActivity(Intent(baseContext, LoginActivity::class.java))
                    finish()
                } else {
                    Log.e("Response Error: ", response.errorBody().toString())
                }
            }

            override fun onFailure(call: Call<JSONObject>, t: Throwable) {
                Toast.makeText(applicationContext,"Something went wrong", Toast.LENGTH_SHORT).show()
                Log.e("Failure: ", t.message.toString())
            }

        })
    }

    private fun logout() {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Apakah anda yakin?")
        alertDialog.setPositiveButton("Ya") { _, _ ->
            preferencesHelper.getString(Constant.prefToken)?.let { getLogout(it) }
        }
        alertDialog.setNegativeButton("Tidak") { dialog, _ ->
            dialog.cancel()
        }
        alertDialog.show()
    }

    private fun checkConnectivity() {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        val isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting
        if (!isConnected) {
            android.app.AlertDialog.Builder(this).setTitle("No Internet Connection")
                .setMessage("Please check your internet connection and try again")
                .setPositiveButton(android.R.string.ok) { _, _ -> }.show()
        }
    }
}