package com.s8125078.assignment2.ui.login

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.s8125078.assignment2.R
import com.s8125078.assignment2.data.remote.ApiResult
import com.s8125078.assignment2.data.repository.AuthRepository
import com.s8125078.assignment2.ui.common.toLoginMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(studentId: String, firstName: String) {
        if (_uiState.value.isLoading) return

        val trimmedId = studentId.trim()
        val trimmedName = firstName.trim()
        val studentIdError = validateStudentId(trimmedId)
        val passwordError = validatePassword(trimmedName)

        if (studentIdError != null || passwordError != null) {
            _uiState.update {
                it.copy(
                    studentIdError = studentIdError,
                    passwordError = passwordError,
                    errorMessage = null
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.value = LoginUiState(isLoading = true)

            // show a hint if the server is taking ages (it sleeps when idle)
            val slowHintJob = launch {
                delay(SLOW_RESPONSE_THRESHOLD_MS)
                _uiState.update { it.copy(showSlowHint = true) }
            }

            val result = authRepository.login(trimmedId, trimmedName)
            slowHintJob.cancel()

            _uiState.value = when (result) {
                is ApiResult.Success -> LoginUiState(keypass = result.data)
                is ApiResult.Failure -> LoginUiState(errorMessage = result.error.toLoginMessage())
            }
        }
    }

    fun onInputChanged() {
        _uiState.update { it.copy(studentIdError = null, passwordError = null, errorMessage = null) }
    }

    // stops the fragment navigating twice
    fun onNavigationHandled() {
        _uiState.update { it.copy(keypass = null) }
    }

    @StringRes
    private fun validateStudentId(studentId: String): Int? = when {
        studentId.isEmpty() -> R.string.error_student_id_required
        !studentId.all { it.isDigit() } -> R.string.error_student_id_digits
        else -> null
    }

    @StringRes
    private fun validatePassword(firstName: String): Int? =
        if (firstName.isEmpty()) R.string.error_password_required else null

    companion object {
        const val SLOW_RESPONSE_THRESHOLD_MS = 5_000L
    }
}
