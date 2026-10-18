package com.s8125078.assignment2.ui.login

import com.s8125078.assignment2.R
import com.s8125078.assignment2.data.remote.ApiError
import com.s8125078.assignment2.data.remote.ApiResult
import com.s8125078.assignment2.data.repository.AuthRepository
import com.s8125078.assignment2.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.advanceTimeBy
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
class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var authRepository: AuthRepository
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        authRepository = mockk()
        viewModel = LoginViewModel(authRepository)
    }

    @Test
    fun `empty fields show both errors and do not call the API`() = runTest {
        viewModel.login("", "")

        val state = viewModel.uiState.value
        assertEquals(R.string.error_student_id_required, state.studentIdError)
        assertEquals(R.string.error_password_required, state.passwordError)
        coVerify(exactly = 0) { authRepository.login(any(), any()) }
    }

    @Test
    fun `student id with letters shows the numbers only error`() = runTest {
        viewModel.login("s8125078", "Shoaib Abedin")

        assertEquals(R.string.error_student_id_digits, viewModel.uiState.value.studentIdError)
        coVerify(exactly = 0) { authRepository.login(any(), any()) }
    }

    @Test
    fun `successful login sets the keypass and trims the input`() = runTest {
        coEvery { authRepository.login("8125078", "Shoaib Abedin") } returns ApiResult.Success("history")

        viewModel.login(" 8125078 ", " Shoaib Abedin ")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("history", state.keypass)
        assertFalse(state.isLoading)
        assertNull(state.errorMessage)
        coVerify(exactly = 1) { authRepository.login("8125078", "Shoaib Abedin") }
    }

    @Test
    fun `wrong details show the invalid credentials message`() = runTest {
        coEvery { authRepository.login(any(), any()) } returns ApiResult.Failure(ApiError.CLIENT)

        viewModel.login("8125078", "wrong")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(R.string.error_login_invalid_credentials, state.errorMessage)
        assertNull(state.keypass)
        assertFalse(state.isLoading)
    }

    @Test
    fun `network failure shows the connection message`() = runTest {
        coEvery { authRepository.login(any(), any()) } returns ApiResult.Failure(ApiError.NETWORK)

        viewModel.login("8125078", "Shoaib Abedin")
        advanceUntilIdle()

        assertEquals(R.string.error_network, viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `slow response shows the waking up hint while loading`() = runTest {
        coEvery { authRepository.login(any(), any()) } coAnswers {
            delay(10_000)
            ApiResult.Success("history")
        }

        viewModel.login("8125078", "Shoaib Abedin")
        advanceTimeBy(LoginViewModel.SLOW_RESPONSE_THRESHOLD_MS + 1)

        assertTrue(viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.showSlowHint)

        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.showSlowHint)
        assertEquals("history", viewModel.uiState.value.keypass)
    }

    @Test
    fun `navigation handled clears the keypass`() = runTest {
        coEvery { authRepository.login(any(), any()) } returns ApiResult.Success("history")
        viewModel.login("8125078", "Shoaib Abedin")
        advanceUntilIdle()

        viewModel.onNavigationHandled()

        assertNull(viewModel.uiState.value.keypass)
    }

    @Test
    fun `editing a field clears the errors`() = runTest {
        viewModel.login("", "")

        viewModel.onInputChanged()

        val state = viewModel.uiState.value
        assertNull(state.studentIdError)
        assertNull(state.passwordError)
        assertNull(state.errorMessage)
    }
}
