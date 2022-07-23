package com.example.wiberdriver.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wiberdriver.api.DriverService
import com.example.wiberdriver.models.AuthToken
import com.example.wiberdriver.models.DriverInfo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileViewModel: ViewModel() {
    private val _nameText = MutableLiveData<String>().apply {
        value = ""
    }

    val nameText : LiveData<String> = _nameText


    private val _phoneNumberText = MutableLiveData<String>().apply {
        value = ""
    }

    val phoneNumberText : LiveData<String> = _phoneNumberText

    private val _newPasswordText = MutableLiveData<String>().apply {
        value = ""
    }

    val newPasswordText : LiveData<String> = _newPasswordText

    fun getDriverInfo(phoneNumber : String, token: AuthToken){
        DriverService.driverService.getAPIDriverInfo(phoneNumber, "Bearer ${token.accessToken}")
            .enqueue(object : Callback<DriverInfo> {
                override fun onResponse(
                    call: Call<DriverInfo>,
                    response: Response<DriverInfo>
                ) {
                    _phoneNumberText.value = phoneNumber
                    if (response.isSuccessful)
                    {
                        Log.i("CallApi", "true")
                        val customerFromApi = response.body()
                        _nameText.value = customerFromApi?.name
                    }
                    else
                    {
                        Log.i("CallApi", "false")
                        _nameText.value = ""
                    }
                }

                override fun onFailure(call: Call<DriverInfo>, t: Throwable) {
                    //server dead
                }
            })
    }
}