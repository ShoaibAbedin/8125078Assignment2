package com.s8125078.assignment2.data.repository

import com.s8125078.assignment2.data.model.Entity
import com.s8125078.assignment2.data.remote.ApiResult

interface DashboardRepository {
    suspend fun getEntities(keypass: String): ApiResult<DashboardData>
}

data class DashboardData(
    val entities: List<Entity>,
    val total: Int
)
