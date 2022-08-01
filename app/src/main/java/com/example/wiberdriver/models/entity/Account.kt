package com.example.wiberdriver.models.entity

enum class roleEnum {
    CUSTOMER,
    DRIVER
}

class Account (
    var id : String,
    var phone: String,
    var password : String,
    var role : roleEnum = roleEnum.DRIVER
){

}