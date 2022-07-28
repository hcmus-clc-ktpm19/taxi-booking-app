package com.example.wiberdriver.viewmodels

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wiberdriver.api.AuthService
import com.example.wiberdriver.models.Account
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpViewModel : ViewModel() {

    private val _phoneNumberText = MutableLiveData<String>().apply {
        value = ""
    }

    val phoneNumberText : LiveData<String> = _phoneNumberText

    private val _passwordText = MutableLiveData<String>().apply {
        value = ""
    }

    val passwordText : LiveData<String> = _passwordText

    private val _confirmPasswordText = MutableLiveData<String>().apply {
        value = ""
    }

    val confirmPasswordText : LiveData<String> = _confirmPasswordText

    var status = MutableLiveData<String>()
    fun registerNewCustomer(account: Account)
    {
        AuthService.authService.registerCustomer(account).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful)
                {
                    status.postValue("Create account successfully")
                }
                else
                {
                    val dataFromResponse = response.errorBody()?.string()
                    val responseToJSON = JSONObject(dataFromResponse.toString())
                    if (responseToJSON.has("Error-Message"))
                        status.postValue(responseToJSON.getString("Error-Message").toString())
                    else if (responseToJSON.has("phone"))
                        status.postValue(responseToJSON.getString("phone").toString())
                    else if (responseToJSON.has("password"))
                        status.postValue(responseToJSON.getString("password").toString())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                status.postValue(t.toString())
            }

        })
    }
}