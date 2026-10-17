package com.s8125078.assignment2.data.remote

import com.s8125078.assignment2.data.remote.dto.DashboardResponse
import com.s8125078.assignment2.data.remote.dto.LoginRequest
import com.s8125078.assignment2.data.remote.dto.LoginResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("{campus}/auth")
    suspend fun login(
        @Path("campus") campus: String,
        @Body request: LoginRequest
    ): LoginResponse

    @GET("dashboard/{keypass}")
    suspend fun getDashboard(@Path("keypass") keypass: String): DashboardResponse
}
