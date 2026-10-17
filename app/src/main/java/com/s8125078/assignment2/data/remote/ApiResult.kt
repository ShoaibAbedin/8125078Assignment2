package com.s8125078.assignment2.data.remote

// Wraps an API call result so the ViewModels don't have to catch exceptions
sealed interface ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>
    data class Failure(val error: ApiError) : ApiResult<Nothing>
}

enum class ApiError {
    CLIENT, // 4xx - e.g. wrong login details
    SERVER, // 5xx
    TIMEOUT,
    NETWORK,
    INVALID_RESPONSE,
    UNKNOWN
}
