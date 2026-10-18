package com.s8125078.assignment2.data.repository

import com.s8125078.assignment2.data.remote.ApiConfig
import com.s8125078.assignment2.data.remote.ApiError
import com.s8125078.assignment2.data.remote.ApiResult
import com.s8125078.assignment2.data.remote.ApiService
import com.s8125078.assignment2.data.remote.dto.LoginRequest
import com.s8125078.assignment2.data.remote.dto.LoginResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

class AuthRepositoryImplTest {

    private lateinit var apiService: ApiService
    private lateinit var repository: AuthRepositoryImpl

    @Before
    fun setUp() {
        apiService = mockk()
        repository = AuthRepositoryImpl(apiService)
    }

    private fun httpError(code: Int) = HttpException(Response.error<Any>(code, "".toResponseBody(null)))

    @Test
    fun `returns the keypass and calls the footscray endpoint`() = runTest {
        coEvery { apiService.login(any(), any()) } returns LoginResponse("history")

        val result = repository.login("8125078", "Shoaib Abedin")

        assertEquals(ApiResult.Success("history"), result)
        coVerify { apiService.login(ApiConfig.CAMPUS, LoginRequest("8125078", "Shoaib Abedin")) }
    }

    @Test
    fun `blank keypass is treated as an invalid response`() = runTest {
        coEvery { apiService.login(any(), any()) } returns LoginResponse(null)

        assertEquals(ApiResult.Failure(ApiError.INVALID_RESPONSE), repository.login("8125078", "Shoaib Abedin"))
    }

    @Test
    fun `401 response is a client error`() = runTest {
        coEvery { apiService.login(any(), any()) } throws httpError(401)

        assertEquals(ApiResult.Failure(ApiError.CLIENT), repository.login("8125078", "wrong"))
    }

    @Test
    fun `500 response is a server error`() = runTest {
        coEvery { apiService.login(any(), any()) } throws httpError(500)

        assertEquals(ApiResult.Failure(ApiError.SERVER), repository.login("8125078", "Shoaib Abedin"))
    }

    @Test
    fun `timeout is reported separately from other network errors`() = runTest {
        coEvery { apiService.login(any(), any()) } throws SocketTimeoutException()

        assertEquals(ApiResult.Failure(ApiError.TIMEOUT), repository.login("8125078", "Shoaib Abedin"))
    }

    @Test
    fun `no connection is a network error`() = runTest {
        coEvery { apiService.login(any(), any()) } throws IOException("offline")

        assertEquals(ApiResult.Failure(ApiError.NETWORK), repository.login("8125078", "Shoaib Abedin"))
    }
}
