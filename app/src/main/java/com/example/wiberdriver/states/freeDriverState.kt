package com.example.wiberdriver.states

import com.example.wiberdriver.interfaces.DriverState
import com.example.wiberdriver.models.entity.Account
import com.example.wiberdriver.models.entity.DriverInfo
import com.example.wiberdriver.models.enums.CarRequestStatus

class freeDriverState : DriverState {
    override fun nextStatusRequest(driverAccount: Account): String {
        driverAccount.setRequestState(acceptedDriverState())
        driverAccount.driverStatus = CarRequestStatus.ACCEPTED.status
        return driverAccount.driverStatus
    }

    override fun isFree(driverAccount: Account): Boolean {
        return true
    }
}