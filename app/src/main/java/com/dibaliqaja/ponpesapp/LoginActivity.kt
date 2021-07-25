package com.dibaliqaja.ponpesapp

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.dibaliqaja.ponpesapp.databinding.ActivityLoginBinding
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            if (checkFields()) login()
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
                    Log.e("Response", loginResponse.toString())
                    finish()
                    startActivity(Intent(baseContext, MainActivity::class.java))
                }
                else {
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
                        Toast.makeText(this@LoginActivity, "Wrong email or password!", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                if (progressDialog.isShowing) progressDialog.dismiss()
                Toast.makeText(baseContext, "Login failed", Toast.LENGTH_SHORT).show()
                Log.e("Failure", t.message.toString())
            }

        })
    }

    private fun checkFields(): Boolean {
        if (binding.edtEmail.text!!.isEmpty()) {
            binding.tilEmail.error = "This field is required"
            return false
        } else {
            binding.tilEmail.error = null
        }
        if (binding.edtPassword.text!!.isEmpty()) {
            binding.tilPassword.error = "This field is required"
            return false
        } else {
            binding.tilPassword.error = null
        }
        return true
    }
}