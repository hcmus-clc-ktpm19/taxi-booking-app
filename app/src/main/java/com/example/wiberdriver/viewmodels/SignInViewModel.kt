package com.example.wiberdriver.viewmodels

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wiberdriver.activities.HomeActivity
import com.example.wiberdriver.activities.SigninActivity
import com.example.wiberdriver.api.AuthService
import com.example.wiberdriver.models.AuthToken
import com.example.wiberdriver.models.roleEnum
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignInViewModel : ViewModel() {
    private val _phoneNumberText = MutableLiveData<String>().apply {
        value = ""
    }

    val phoneNumberText : LiveData<String> = _phoneNumberText

    private val _passwordText = MutableLiveData<String>().apply {
        value = ""
    }

    val passwordText : LiveData<String> = _passwordText

    var status = MutableLiveData<String>()
    fun signInGetToken(phoneNumber: String, password:String){
        AuthService.authService.loginThroughAPI(phoneNumber, password).enqueue(object :
            Callback<AuthToken> {
            override fun onResponse(call: Call<AuthToken>, response: Response<AuthToken>) {
                if (response.isSuccessful)
                {
                    SigninActivity.authDriverTokenFromSignIn = response.body()!!
                    SigninActivity.phoneNumberLoginFromSignIn = phoneNumber
                    GlobalScope.launch {
                        val accountDetail = AuthService.authService.getAccountDetail(
                            SigninActivity.phoneNumberLoginFromSignIn,
                            "Bearer ${SigninActivity.authDriverTokenFromSignIn.accessToken}"
                        )
                        if (!accountDetail.role.equals(roleEnum.DRIVER))
                        {
                            status.postValue("This account is not a driver")
                        }
                        else
                            status.postValue("Success")
                    }
                }
                else
                {
                    status.postValue("Wrong phone number or password")
                }
            }

            override fun onFailure(call: Call<AuthToken>, t: Throwable) {
                status.postValue(t.toString())
            }

        })
    }
}