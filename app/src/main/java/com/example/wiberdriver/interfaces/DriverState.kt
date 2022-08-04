package com.example.wiberdriver.interfaces

import com.example.wiberdriver.models.entity.Account
import com.example.wiberdriver.models.entity.DriverInfo

interface DriverState {
    fun nextStatusRequest(driverAccount: Account) : String
    fun isFree(driverAccount: Account) : Boolean
}