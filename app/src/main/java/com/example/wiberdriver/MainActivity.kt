package com.example.wiberdriver

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.wiberdriver.databinding.ActivitySigninBinding
import com.example.wiberdriver.viewmodels.SignInViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySigninBinding
    private lateinit var loginviewModel: SignInViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        binding = ActivitySigninBinding.inflate(layoutInflater)
//
//        setContentView(binding.root)
//
//        loginviewModel = ViewModelProvider(this).get(SignInViewModel::class.java)
//
//        loginviewModel.phoneNumberText.observe(this){
//            binding.phoneNumberInputLayout.editText?.setText(it)
//        }
//
//
//        loginviewModel.passwordText.observe(this){
//            binding.password.editText?.setText(it)
//        }
    }
}