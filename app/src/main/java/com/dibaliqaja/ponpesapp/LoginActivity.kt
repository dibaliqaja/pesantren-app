package com.dibaliqaja.ponpesapp

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.dibaliqaja.ponpesapp.databinding.ActivityLoginBinding
import com.dibaliqaja.ponpesapp.helper.Constant
import com.dibaliqaja.ponpesapp.helper.PreferencesHelper
import com.dibaliqaja.ponpesapp.model.LoginRequest
import com.dibaliqaja.ponpesapp.model.LoginResponse
import com.dibaliqaja.ponpesapp.services.RetrofitClient.apiService
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var preferencesHelper: PreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferencesHelper = PreferencesHelper(this)

        checkConnectivity()

        binding.btnLogin.setOnClickListener {
            if (checkFields()) login()
        }
    }

    override fun onStart() {
        super.onStart()
        if (preferencesHelper.getBoolean(Constant.prefIsLogin)) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun login() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        progressDialog.show()

        val rEmail = binding.edtEmail.text.toString()
        val rPassword = binding.edtPassword.text.toString()
        val loginRequest = LoginRequest(rEmail, rPassword)

        apiService.login(loginRequest).enqueue(object: Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (progressDialog.isShowing) progressDialog.dismiss()
                if (response.isSuccessful) {
                    val loginResponse = response.body()?.data
                    val token: String = loginResponse!!.token
                    preferencesHelper.put(Constant.prefIsLogin, true)
                    preferencesHelper.put(Constant.prefToken, token)

                    Toast.makeText(baseContext, "Login berhasil", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(baseContext, MainActivity::class.java))
                    finish()
                } else {
                    try {
                        val jsonObject = JSONObject(response.errorBody()!!.string()).getJSONObject("message")
                        if (jsonObject.has("email")) {
                            val email: String = jsonObject.getJSONArray( "email")[0].toString()
                            binding.tilEmail.error = email
                        }
                        if (jsonObject.has("password")) {
                            val password: String = jsonObject.getJSONArray( "password")[0].toString()
                            binding.tilPassword.error = password
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(this@LoginActivity, "Email atau password salah!", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                if (progressDialog.isShowing) progressDialog.dismiss()
                Toast.makeText(applicationContext,"Something went wrong", Toast.LENGTH_SHORT).show()
                Log.e("Failure: ", t.message.toString())
            }

        })
    }

    private fun checkFields(): Boolean {
        if (binding.edtEmail.text!!.isEmpty()) {
            binding.tilEmail.error = "This email field is required"
            return false
        } else { binding.tilEmail.error = null }

        if (binding.edtPassword.text!!.isEmpty()) {
            binding.tilPassword.error = "This password field is required"
            return false
        } else { binding.tilPassword.error = null }

        return true
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