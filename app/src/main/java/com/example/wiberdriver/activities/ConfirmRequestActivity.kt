package com.example.wiberdriver.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.wiberdriver.databinding.ActivityConfirmRequestBinding

class ConfirmRequestActivity : AppCompatActivity() {
    private lateinit var binding: ActivityConfirmRequestBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfirmRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}