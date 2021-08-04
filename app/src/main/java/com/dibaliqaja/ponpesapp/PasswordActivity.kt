package com.dibaliqaja.ponpesapp

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.dibaliqaja.ponpesapp.databinding.ActivityPasswordBinding
import com.dibaliqaja.ponpesapp.helper.Constant
import com.dibaliqaja.ponpesapp.helper.PreferencesHelper
import com.dibaliqaja.ponpesapp.model.PasswordResponse
import com.dibaliqaja.ponpesapp.services.RetrofitClient.apiService
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPasswordBinding
    private lateinit var preferencesHelper: PreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferencesHelper = PreferencesHelper(this)

        if (!preferencesHelper.getBoolean(Constant.prefIsLogin)) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.btnSave.setOnClickListener {
            if (checkFields()) preferencesHelper.getString(Constant.prefToken)?.let { update(it) }
        }
    }

    private fun update(token: String) {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        progressDialog.show()

        val rOldPassword = binding.edtOldPassword.text.toString()
        val rNewPassword = binding.edtNewPassword.text.toString()
        val rNewPasswordConfirm = binding.edtPasswordConfirmation.text.toString()

        apiService.updatePassword("Bearer $token", rOldPassword, rNewPassword, rNewPasswordConfirm).enqueue(object: Callback<PasswordResponse> {
            override fun onResponse(call: Call<PasswordResponse>, response: Response<PasswordResponse>) {
                if (progressDialog.isShowing) progressDialog.dismiss()
                if (response.isSuccessful) {
                    Toast.makeText(baseContext, "Update password berhasil", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(baseContext, MainActivity::class.java))
                    finish()
                }
                else {
                    try {
                        val jsonObject = JSONObject(response.errorBody()!!.string()).getJSONObject("data")
                        if (jsonObject.has("current_password")) {
                            val oldPassword: String = jsonObject.getJSONArray( "current_password")[0].toString()
                            binding.tilOldPassword.error = oldPassword
                        }
                        if (jsonObject.has("password")) {
                            val newPassword: String = jsonObject.getJSONArray( "password")[0].toString()
                            binding.tilNewPassword.error = newPassword
                        }
                        if (jsonObject.has("password_confirmation")) {
                            val newPasswordConfirm: String = jsonObject.getJSONArray( "password_confirmation")[0].toString()
                            binding.tilPasswordConfirmation.error = newPasswordConfirm
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(this@PasswordActivity, "Password Lama Salah!", Toast.LENGTH_SHORT).show()
                    }
                }
                if (response.code() == 401) {
                    preferencesHelper.clear()
                    Toast.makeText(applicationContext,"Token expired", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@PasswordActivity, LoginActivity::class.java))
                    finish()
                }
            }

            override fun onFailure(call: Call<PasswordResponse>, t: Throwable) {
                if (progressDialog.isShowing) progressDialog.dismiss()
                Toast.makeText(applicationContext,"Something went wrong", Toast.LENGTH_SHORT).show()
                Log.e("Failure: ", t.message.toString())
            }

        })
    }

    private fun checkFields(): Boolean {
        if (binding.edtOldPassword.text!!.isEmpty()) {
            binding.tilOldPassword.error = "This old password field is required"
            return false
        } else { binding.tilOldPassword.error = null }

        if (binding.edtNewPassword.text!!.isEmpty()) {
            binding.tilNewPassword.error = "This current password field is required"
            return false
        } else { binding.tilNewPassword.error = null }

        if (binding.edtPasswordConfirmation.text!!.isEmpty()) {
            binding.tilPasswordConfirmation.error = "This current password confirm field is required"
            return false
        } else { binding.tilPasswordConfirmation.error = null }

        return true
    }
}