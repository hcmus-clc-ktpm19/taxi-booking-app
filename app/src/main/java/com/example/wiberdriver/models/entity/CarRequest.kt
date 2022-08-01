package com.example.wiberdriver.models.entity

class CarRequest(
    var id: String,
    var customerId: String,
    var customerPhone: String,
    var pickingAddress: String,
    var arrivingAddress: String,
    var lngPickingAddress: Double,
    var latPickingAddress: Double,
    var lngArrivingAddress: Double,
    var latArrivingAddress: Double,
    var status: String
) {
}