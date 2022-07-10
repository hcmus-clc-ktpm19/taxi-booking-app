package com.example.wiberdriver.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.wiberdriver.databinding.ActivityProfileBinding
import com.example.wiberdriver.viewmodels.ProfileViewModel

class ProfileActivity : AppCompatActivity() {
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }
}