package com.example.wiberdriver.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.wiberdriver.databinding.ActivitySigninBinding
import com.example.wiberdriver.models.entity.AuthToken
import com.example.wiberdriver.viewmodels.SignInViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SigninActivity : AppCompatActivity() {

    companion object{
        lateinit var authDriverTokenFromSignIn: AuthToken
        lateinit var phoneNumberLoginFromSignIn: String
    }


    private lateinit var binding: ActivitySigninBinding
    private lateinit var loginviewModel: SignInViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loginviewModel = ViewModelProvider(this).get(SignInViewModel::class.java)

        val phoneIL = binding.phoneNumberInputLayout
        val passwordIL = binding.paswordInputLayout




        binding.signinbtn.setOnClickListener {
            val phoneNumber = phoneIL.editText?.text.toString()
            val password = passwordIL.editText?.text.toString()
            loginviewModel.signInGetToken(phoneNumber, password)
        }
        val statusObserver = Observer<String>{ status ->
            when (status) {
                "This account is not a driver" -> {
                    MaterialAlertDialogBuilder(this)
                        .setTitle("Error")
                        .setMessage("This account is not a driver")
                        .setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                }
                "Wrong phone number or password" -> {
                    MaterialAlertDialogBuilder(this)
                        .setTitle("Error")
                        .setMessage("Invalid phone number or password")
                        .setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                }
                "Success" -> {
                    startActivity(Intent(this@SigninActivity, HomeActivity::class.java))
                }
                else -> {
                    MaterialAlertDialogBuilder(this)
                        .setTitle("Error")
                        .setMessage(status)
                        .setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                }
            }
        }
        loginviewModel.status.observe(this, statusObserver)

        binding.signupbtn.setOnClickListener {
            //driver can't create account on their own
            //this is for test only
            startActivity(Intent(this@SigninActivity, SignupActivity::class.java))
        }
    }

}