package com.s8125078.assignment2.ui.common

import androidx.annotation.StringRes
import com.s8125078.assignment2.R
import com.s8125078.assignment2.data.remote.ApiError

// Error messages shared between screens

// on login a 4xx means the ID or name was wrong
@StringRes
fun ApiError.toLoginMessage(): Int = when (this) {
    ApiError.CLIENT -> R.string.error_login_invalid_credentials
    else -> toGeneralMessage()
}

@StringRes
fun ApiError.toGeneralMessage(): Int = when (this) {
    ApiError.NETWORK -> R.string.error_network
    ApiError.TIMEOUT -> R.string.error_timeout
    ApiError.SERVER -> R.string.error_server
    ApiError.INVALID_RESPONSE -> R.string.error_invalid_response
    ApiError.CLIENT, ApiError.UNKNOWN -> R.string.error_unknown
}
