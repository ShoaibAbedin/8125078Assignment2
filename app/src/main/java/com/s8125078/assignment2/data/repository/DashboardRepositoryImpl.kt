package com.s8125078.assignment2.data.repository

import com.s8125078.assignment2.data.mapper.EntityMapper
import com.s8125078.assignment2.data.remote.ApiError
import com.s8125078.assignment2.data.remote.ApiResult
import com.s8125078.assignment2.data.remote.ApiService
import com.s8125078.assignment2.data.remote.safeApiCall
import javax.inject.Inject

class DashboardRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val entityMapper: EntityMapper
) : DashboardRepository {

    override suspend fun getEntities(keypass: String): ApiResult<DashboardData> {
        return when (val result = safeApiCall { apiService.getDashboard(keypass) }) {
            is ApiResult.Success -> {
                val rawEntities = result.data.entities
                    ?: return ApiResult.Failure(ApiError.INVALID_RESPONSE)
                val entities = entityMapper.mapAll(rawEntities)
                // fall back to the list size if entityTotal is missing
                ApiResult.Success(DashboardData(entities, result.data.entityTotal ?: entities.size))
            }
            is ApiResult.Failure -> result
        }
    }
}
