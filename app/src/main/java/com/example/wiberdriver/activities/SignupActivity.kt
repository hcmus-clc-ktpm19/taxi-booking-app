package com.example.wiberdriver.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.wiberdriver.databinding.ActivitySignupBinding
import com.example.wiberdriver.models.entity.Account
import com.example.wiberdriver.viewmodels.SignUpViewModel

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var signUpviewModel: SignUpViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        signUpviewModel = ViewModelProvider(this).get(SignUpViewModel::class.java)
        val phoneNumberIL = binding.phoneNumberInputLayout
        val pwdIL = binding.paswordInputLayout
        val confirmPwdIL = binding.confirmPaswordInputLayout

        binding.signUpBtn.setOnClickListener {
            val phoneNumber = phoneNumberIL.editText?.text.toString()
            val password = pwdIL.editText?.text.toString()
            val confirmPassword = confirmPwdIL.editText?.text.toString()
            if (password.equals(confirmPassword) != true)
            {
                Toast.makeText(this, "Confirm Password does not match",Toast.LENGTH_LONG).show()
            }
            else
            {
                val newCustomer = Account("", phoneNumber, password)
                signUpviewModel.registerNewCustomer(newCustomer)
            }
        }

        val statusObserver = Observer<String>{ status ->
            when (status) {
                "Create account successfully" -> {
                    Toast.makeText(this@SignupActivity, status, Toast.LENGTH_LONG).show()
                    finish()
                }
                else -> {
                    Toast.makeText(this@SignupActivity, status, Toast.LENGTH_LONG).show()
                }
            }
        }
        signUpviewModel.status.observe(this, statusObserver)

        binding.signinbtn.setOnClickListener {
            finish()
        }
    }
}