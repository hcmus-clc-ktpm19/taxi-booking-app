package com.example.wiberdriver.models.entity

import androidx.lifecycle.Observer
import com.example.wiberdriver.interfaces.DriverState
import com.example.wiberdriver.models.enums.CarRequestStatus
import com.example.wiberdriver.states.freeDriverState

enum class roleEnum {
    CUSTOMER,
    DRIVER
}

class Account (
    var id : String,
    var phone: String,
    var password : String,
    var role : roleEnum = roleEnum.DRIVER
) {

    lateinit var currentDriverState : DriverState
    lateinit var driverStatus: String

    fun setRequestState (state : DriverState)
    {
        currentDriverState = state
    }


    fun nextStatusRequest() : String{
        return currentDriverState.nextStatusRequest(this)
    }

    fun isFree() : Boolean{
        return currentDriverState.isFree(this)
    }
}