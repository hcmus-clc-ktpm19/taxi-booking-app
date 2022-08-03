package com.example.wiberdriver.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wiberdriver.activities.SigninActivity
import com.example.wiberdriver.api.CarRequestService
import com.example.wiberdriver.api.DriverService
import com.example.wiberdriver.models.entity.CarRequest
import com.example.wiberdriver.models.enums.CarRequestStatus
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel : ViewModel() {
    var acceptCarRequestStatus = MutableLiveData<String>()
    fun acceptTheCarRequest(carRequest: CarRequest) {
        CarRequestService.carRequestService.requestCarByAPI(
            carRequest,
            "Bearer ${SigninActivity.authDriverTokenFromSignIn.accessToken}"
        )
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        carRequest.id = response.body()
                            ?.string() //this consume that one line string so be careful to use this
                        Log.i("request car", carRequest.id.toString())
                        acceptCarRequestStatus.postValue("Accept car request successfully")
                    } else {
                        acceptCarRequestStatus.postValue(
                            "error: ${
                                response.errorBody().toString()
                            }"
                        )
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    //server Dead
                    acceptCarRequestStatus.postValue(t.toString())
                }
            })
    }
}