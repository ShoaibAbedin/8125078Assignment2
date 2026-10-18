package com.s8125078.assignment2.ui.dashboard

import androidx.lifecycle.SavedStateHandle
import com.s8125078.assignment2.R
import com.s8125078.assignment2.data.model.Entity
import com.s8125078.assignment2.data.model.EntityField
import com.s8125078.assignment2.data.remote.ApiError
import com.s8125078.assignment2.data.remote.ApiResult
import com.s8125078.assignment2.data.repository.DashboardData
import com.s8125078.assignment2.data.repository.DashboardRepository
import com.s8125078.assignment2.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: DashboardRepository

    private val worldWarTwo = Entity(
        title = "World War II",
        period = "1939 – 1945",
        durationYears = 6,
        fields = listOf(EntityField("Location", "Global"), EntityField("Key figure", "Winston Churchill")),
        description = "A global war."
    )

    @Before
    fun setUp() {
        repository = mockk()
    }

    private fun createViewModel(keypass: String = "history") =
        DashboardViewModel(repository, SavedStateHandle(mapOf(DashboardViewModel.KEYPASS_ARG to keypass)))

    @Test
    fun `loads the entities for the keypass on init`() = runTest {
        val entities = listOf(worldWarTwo, worldWarTwo.copy(title = "Cold War"))
        coEvery { repository.getEntities("history") } returns ApiResult.Success(DashboardData(entities, 2))

        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(entities, state.entities)
        assertEquals(2, state.total)
        assertEquals("history", state.topic)
        assertNull(state.errorMessage)
        coVerify(exactly = 1) { repository.getEntities("history") }
    }

    @Test
    fun `unknown keypass shows the not found message`() = runTest {
        coEvery { repository.getEntities(any()) } returns ApiResult.Failure(ApiError.CLIENT)

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(R.string.error_dashboard_not_found, viewModel.uiState.value.errorMessage)
        assertTrue(viewModel.uiState.value.entities.isEmpty())
    }

    @Test
    fun `server error shows the server message`() = runTest {
        coEvery { repository.getEntities(any()) } returns ApiResult.Failure(ApiError.SERVER)

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(R.string.error_server, viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `missing keypass shows an error without calling the API`() = runTest {
        val viewModel = createViewModel(keypass = "")
        advanceUntilIdle()

        assertEquals(R.string.error_dashboard_not_found, viewModel.uiState.value.errorMessage)
        coVerify(exactly = 0) { repository.getEntities(any()) }
    }

    @Test
    fun `retry after a failure loads the entities`() = runTest {
        coEvery { repository.getEntities(any()) } returns ApiResult.Failure(ApiError.NETWORK)
        val viewModel = createViewModel()
        advanceUntilIdle()
        assertEquals(R.string.error_network, viewModel.uiState.value.errorMessage)

        coEvery { repository.getEntities(any()) } returns ApiResult.Success(DashboardData(listOf(worldWarTwo), 1))
        viewModel.loadEntities()
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.errorMessage)
        assertEquals(1, viewModel.uiState.value.entities.size)
    }

    @Test
    fun `empty list counts as empty state`() = runTest {
        coEvery { repository.getEntities(any()) } returns ApiResult.Success(DashboardData(emptyList(), 0))

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isEmpty)
    }
}
