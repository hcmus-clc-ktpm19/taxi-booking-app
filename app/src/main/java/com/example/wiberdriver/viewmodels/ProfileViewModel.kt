package com.example.wiberdriver.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wiberdriver.activities.SigninActivity
import com.example.wiberdriver.activities.SigninActivity.Companion.driverInfoFromSignIn
import com.example.wiberdriver.api.AuthService
import com.example.wiberdriver.api.DriverService
import com.example.wiberdriver.models.entity.AuthToken
import com.example.wiberdriver.models.entity.DriverInfo
import com.example.wiberdriver.models.entity.roleEnum
import com.example.wiberdriver.models.enums.CarType
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
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

    private val _carTypeValue = MutableLiveData<String>().apply {
        value = CarType.FOUR_SEATS.status
    }

    val carTypeValue : LiveData<String> = _carTypeValue

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
                        val driverFromApi = response.body()
                        driverInfoFromSignIn = driverFromApi!!
                        _nameText.value = driverFromApi?.name
                        _carTypeValue.value = driverFromApi?.carType
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
    var editProfileStatus = MutableLiveData<String>()
    fun startEditingProfile(passWordString : String, nameString : String, carType: String) {
        if (nameString.isNotEmpty()) {
            GlobalScope.launch {
                val accountDetail = AuthService.authService.getAccountDetail(
                    SigninActivity.phoneNumberLoginFromSignIn,
                    "Bearer ${SigninActivity.authDriverTokenFromSignIn.accessToken}"
                )
                if (accountDetail != null) {
                    if (passWordString.isNotEmpty()) {
                        accountDetail.password = passWordString
                        try {
                            AuthService.authService.updatePasswordAPI(
                                accountDetail.id, accountDetail,
                                "Bearer ${SigninActivity.authDriverTokenFromSignIn.accessToken}"
                            )
                            val customerUpdate = DriverInfo(
                                accountDetail.id, SigninActivity.phoneNumberLoginFromSignIn,
                                nameString, carType, roleEnum.CUSTOMER
                            )
                            updateDriverInfo(customerUpdate)
                        } catch (e: Exception) {
                            editProfileStatus.postValue(
                                (e as? HttpException)?.response()?.errorBody()?.string()
                            )
                        }
                    } else {
                        val customerUpdate = DriverInfo(
                            accountDetail.id, SigninActivity.phoneNumberLoginFromSignIn,
                            nameString, carType, roleEnum.CUSTOMER
                        )
                        updateDriverInfo(customerUpdate)
                    }

                } else
                    editProfileStatus.postValue("Error while saving")
            }
        } else
            editProfileStatus.postValue("Please input name")
    }

    private fun updateDriverInfo(driverInfo: DriverInfo)
    {
        DriverService.driverService.updateDriverInfoAPI(driverInfo, "Bearer ${SigninActivity.authDriverTokenFromSignIn.accessToken}")
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful)
                    {
                        editProfileStatus.postValue("Update successfully")
                        getDriverInfo(driverInfo.phone,
                            SigninActivity.authDriverTokenFromSignIn
                        )
                    }
                    else
                    {
                        editProfileStatus.postValue("Error while updating")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    editProfileStatus.postValue("Unable to connect to server")
                }

            })
    }

}