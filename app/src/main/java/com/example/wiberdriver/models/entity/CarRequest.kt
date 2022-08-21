package com.example.wiberdriver.models.entity

import org.osmdroid.util.Distance
import java.nio.DoubleBuffer

class CarRequest(
    var id: String?,
    var customerId: String,
    var customerPhone: String,
    var driverId: String?,
    var driverName: String?,
    var driverPhone: String?,
    var driverAvatar: String?,
    var pickingAddress: String,
    var arrivingAddress: String?,
    var lngPickingAddress: Double,
    var latPickingAddress: Double,
    var lngArrivingAddress: Double?,
    var latArrivingAddress: Double?,
    var carType: String,
    var status: String,
    var price: Double,
    var distance: Double,
) {
}