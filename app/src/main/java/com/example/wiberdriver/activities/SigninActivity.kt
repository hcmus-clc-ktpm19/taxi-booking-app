package com.example.wiberdriver.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.wiberdriver.api.AuthService
import com.example.wiberdriver.databinding.ActivitySigninBinding
import com.example.wiberdriver.models.AuthToken
import com.example.wiberdriver.models.roleEnum
import com.example.wiberdriver.viewmodels.SignInViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SigninActivity : AppCompatActivity() {

    companion object{
        lateinit var authCustomerTokenFromSignIn: AuthToken
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
            signInGetToken(phoneNumber, password)
        }
        binding.signupbtn.setOnClickListener {
            startActivity(Intent(this@SigninActivity, SignupActivity::class.java))
        }
    }

    fun signInGetToken(phoneNumber: String, password:String){
        AuthService.authService.loginAsCustomer(phoneNumber, password).enqueue(object :
            Callback<AuthToken> {
            override fun onResponse(call: Call<AuthToken>, response: Response<AuthToken>) {
                if (response.isSuccessful)
                {
                    authCustomerTokenFromSignIn = response.body()!!
                    phoneNumberLoginFromSignIn = phoneNumber
                    GlobalScope.launch {
                        val accountDetail = AuthService.authService.getAccountDetail(
                            phoneNumberLoginFromSignIn,
                            "Bearer ${authCustomerTokenFromSignIn.accessToken}"
                        )
                        if (!accountDetail.role.equals(roleEnum.DRIVER))
                        {
                            Handler(Looper.getMainLooper()).post {
                                Toast.makeText(this@SigninActivity, "This account is not a driver", Toast.LENGTH_SHORT).show()
                            }
                        }
                        else
                            startActivity(Intent(this@SigninActivity, HomeActivity::class.java))
                    }
                }
                else
                {
                    Toast.makeText(this@SigninActivity, "Wrong phone number or password", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<AuthToken>, t: Throwable) {
                Toast.makeText(this@SigninActivity, t.toString(), Toast.LENGTH_LONG).show()
            }

        })
    }
}