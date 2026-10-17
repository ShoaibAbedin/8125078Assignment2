package com.s8125078.assignment2.data.repository

import com.s8125078.assignment2.data.remote.ApiResult

// Interface so the ViewModel doesn't depend on Retrofit directly (and can be mocked in tests)
interface AuthRepository {

    // returns the keypass on success
    suspend fun login(studentId: String, firstName: String): ApiResult<String>
}
