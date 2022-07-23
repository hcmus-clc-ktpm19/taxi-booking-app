package com.example.wiberdriver.activities

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.wiberdriver.api.AuthService
import com.example.wiberdriver.databinding.ActivitySignupBinding
import com.example.wiberdriver.models.Account
import com.example.wiberdriver.viewmodels.SignUpViewModel
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
                registerNewCustomer(newCustomer)
            }
        }

        binding.signinbtn.setOnClickListener {
            finish()
        }
    }

    fun registerNewCustomer(account: Account)
    {
        AuthService.authService.registerCustomer(account).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful)
                {
                    Toast.makeText(this@SignupActivity, "Create account successfully",Toast.LENGTH_LONG).show()
                    finish()
                }
                else
                {
                    val dataFromResponse = response.errorBody()?.string()
                    val responseToJSON = JSONObject(dataFromResponse.toString())
                    if (responseToJSON.has("Error-Message"))
                        Toast.makeText(this@SignupActivity, responseToJSON.getString("Error-Message").toString(),Toast.LENGTH_LONG).show()
                    else if (responseToJSON.has("phone"))
                        Toast.makeText(this@SignupActivity, responseToJSON.getString("phone").toString(),Toast.LENGTH_LONG).show()
                    else if (responseToJSON.has("password"))
                        Toast.makeText(this@SignupActivity, responseToJSON.getString("password").toString(),Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@SignupActivity, t.toString(),Toast.LENGTH_LONG).show()
            }

        })
    }
}