package com.s8125078.assignment2.data.repository

import com.s8125078.assignment2.data.remote.ApiConfig
import com.s8125078.assignment2.data.remote.ApiError
import com.s8125078.assignment2.data.remote.ApiResult
import com.s8125078.assignment2.data.remote.ApiService
import com.s8125078.assignment2.data.remote.dto.LoginRequest
import com.s8125078.assignment2.data.remote.safeApiCall
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : AuthRepository {

    override suspend fun login(studentId: String, firstName: String): ApiResult<String> {
        val result = safeApiCall {
            apiService.login(ApiConfig.CAMPUS, LoginRequest(studentId, firstName))
        }
        return when (result) {
            is ApiResult.Success -> {
                val keypass = result.data.keypass
                if (keypass.isNullOrBlank()) {
                    ApiResult.Failure(ApiError.INVALID_RESPONSE)
                } else {
                    ApiResult.Success(keypass)
                }
            }
            is ApiResult.Failure -> result
        }
    }
}
