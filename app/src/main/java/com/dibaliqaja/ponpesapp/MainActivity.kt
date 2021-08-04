package com.dibaliqaja.ponpesapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.dibaliqaja.ponpesapp.databinding.ActivityMainBinding
import com.dibaliqaja.ponpesapp.helper.Constant
import com.dibaliqaja.ponpesapp.helper.PreferencesHelper
import com.dibaliqaja.ponpesapp.model.ProfileResponse
import com.dibaliqaja.ponpesapp.services.RetrofitClient.apiService
import com.dibaliqaja.ponpesapp.ui.cashbook.CashBookActivity
import com.dibaliqaja.ponpesapp.ui.profile.ProfileActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var preferencesHelper: PreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferencesHelper = PreferencesHelper(this)

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
            rvProfile.setOnClickListener {
                startActivity(Intent(baseContext, ProfileActivity::class.java))
            }
            rvPass.setOnClickListener {
                startActivity(Intent(baseContext, PasswordActivity::class.java))
            }
            rvBukuKas.setOnClickListener {
                startActivity(Intent(baseContext, CashBookActivity::class.java))
            }
            rvSyahriah.setOnClickListener {
                startActivity(Intent(baseContext, SyahriahActivity::class.java))
            }
        }
    }

    private fun getProfile(token: String) {
        apiService.getProfile("Bearer $token").enqueue(object: Callback<ProfileResponse> {
            override fun onResponse(
                call: Call<ProfileResponse>,
                response: Response<ProfileResponse>
            ) {
                if (response.isSuccessful) {
                    val profileResponse = response.body()?.data
                    binding.apply {
                        tvName.text = profileResponse?.name
                        tvEmail.text = profileResponse?.email
                        Glide.with(this@MainActivity)
                            .load(profileResponse?.photo)
                            .placeholder(R.drawable.profile_placeholder)
                            .centerCrop()
                            .into(ivPhoto)
                    }
                }
                if (response.code() == 401) {
                    preferencesHelper.clear()
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                    finish()
                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                Toast.makeText(applicationContext,"Something went wrong", Toast.LENGTH_SHORT).show()
                Log.e("Failure: ", t.message.toString())
            }
        })
    }
}