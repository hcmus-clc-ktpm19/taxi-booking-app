package com.example.wiberdriver.models.entity

import com.example.wiberdriver.interfaces.DriverState
import com.example.wiberdriver.models.enums.CarRequestStatus
import com.example.wiberdriver.states.freeDriverState

class DriverInfo  (
    var id: String,
    var phone: String,
    var name : String,
    var carType: String,
    var role : roleEnum = roleEnum.DRIVER
){


}