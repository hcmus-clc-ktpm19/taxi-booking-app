package com.example.wiberdriver.models

import com.google.gson.annotations.SerializedName

class AuthToken (
    @SerializedName("Access-Token") val accessToken: String,
        ) {
}