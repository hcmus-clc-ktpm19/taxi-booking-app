package com.example.wiberdriver.models

class CarRequest(
    val customerId: String,
    val customerPhone: String,
    val pickingAddress: String,
    val lngPickingAddress: String,
    val latPickingAddress: String
) {
}