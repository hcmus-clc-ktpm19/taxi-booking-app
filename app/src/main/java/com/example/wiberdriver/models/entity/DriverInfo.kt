package com.example.wiberdriver.models.entity

class DriverInfo  (
    var id: String,
    var phone: String,
    var name : String,
    var role : roleEnum = roleEnum.CUSTOMER
){

}