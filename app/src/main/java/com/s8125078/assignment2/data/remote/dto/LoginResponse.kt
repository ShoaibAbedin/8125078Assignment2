package com.s8125078.assignment2.data.remote.dto

import com.google.gson.annotations.SerializedName

// nullable because Gson can still set it to null if the field is missing
data class LoginResponse(
    @SerializedName("keypass") val keypass: String?
)
