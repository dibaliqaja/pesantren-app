package com.dibaliqaja.ponpesapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dibaliqaja.ponpesapp.databinding.ActivityProfileBinding
import com.dibaliqaja.ponpesapp.helper.PreferencesHelper

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var preferencesHelper: PreferencesHelper
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferencesHelper = PreferencesHelper(this)
    }
}