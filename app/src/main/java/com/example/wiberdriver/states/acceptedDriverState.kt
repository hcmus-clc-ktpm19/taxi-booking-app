package com.example.wiberdriver.states

import com.example.wiberdriver.interfaces.DriverState
import com.example.wiberdriver.models.entity.Account
import com.example.wiberdriver.models.entity.DriverInfo
import com.example.wiberdriver.models.enums.CarRequestStatus

class acceptedDriverState : DriverState {
    override fun nextStatusRequest(driverAccount: Account): String {
        driverAccount.setRequestState(freeDriverState())
        driverAccount.driverStatus = CarRequestStatus.FREE.status
        return driverAccount.driverStatus
    }

    override fun isFree(driverAccount: Account): Boolean {
        return false
    }
}