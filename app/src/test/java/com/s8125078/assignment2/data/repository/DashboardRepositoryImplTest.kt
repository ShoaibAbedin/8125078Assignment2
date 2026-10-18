package com.s8125078.assignment2.data.repository

import com.s8125078.assignment2.data.mapper.EntityMapper
import com.s8125078.assignment2.data.remote.ApiError
import com.s8125078.assignment2.data.remote.ApiResult
import com.s8125078.assignment2.data.remote.ApiService
import com.s8125078.assignment2.data.remote.dto.DashboardResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

class DashboardRepositoryImplTest {

    private lateinit var apiService: ApiService
    private lateinit var repository: DashboardRepositoryImpl

    private val rawEntities = listOf(
        mapOf<String, Any?>("event" to "World War II", "startYear" to 1939.0, "endYear" to 1945.0),
        mapOf<String, Any?>("event" to "Cold War", "startYear" to 1947.0, "endYear" to 1991.0)
    )

    @Before
    fun setUp() {
        apiService = mockk()
        // real mapper - it has no dependencies so there's nothing to mock
        repository = DashboardRepositoryImpl(apiService, EntityMapper())
    }

    @Test
    fun `maps the entities and keeps the total`() = runTest {
        coEvery { apiService.getDashboard("history") } returns DashboardResponse(rawEntities, 7)

        val result = repository.getEntities("history")

        assertTrue(result is ApiResult.Success)
        val data = (result as ApiResult.Success).data
        assertEquals(listOf("World War II", "Cold War"), data.entities.map { it.title })
        assertEquals(7, data.total)
    }

    @Test
    fun `missing entityTotal falls back to the list size`() = runTest {
        coEvery { apiService.getDashboard(any()) } returns DashboardResponse(rawEntities, null)

        val result = repository.getEntities("history") as ApiResult.Success

        assertEquals(2, result.data.total)
    }

    @Test
    fun `missing entities list is an invalid response`() = runTest {
        coEvery { apiService.getDashboard(any()) } returns DashboardResponse(null, 0)

        assertEquals(ApiResult.Failure(ApiError.INVALID_RESPONSE), repository.getEntities("history"))
    }

    @Test
    fun `network errors are passed through`() = runTest {
        coEvery { apiService.getDashboard(any()) } throws IOException()

        assertEquals(ApiResult.Failure(ApiError.NETWORK), repository.getEntities("history"))
    }
}
