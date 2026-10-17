package com.s8125078.assignment2.ui.dashboard

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.s8125078.assignment2.R
import com.s8125078.assignment2.data.remote.ApiResult
import com.s8125078.assignment2.data.repository.DashboardRepository
import com.s8125078.assignment2.ui.common.toDashboardMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val dashboardRepository: DashboardRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // the Safe Args "keypass" argument ends up in the SavedStateHandle automatically
    private val keypass: String = savedStateHandle.get<String>(KEYPASS_ARG).orEmpty()

    private val _uiState = MutableStateFlow(DashboardUiState(topic = keypass))
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadEntities()
    }

    fun loadEntities() {
        if (keypass.isBlank()) {
            _uiState.update { it.copy(isLoading = false, errorMessage = R.string.error_dashboard_not_found) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, showSlowHint = false, errorMessage = null) }

            val slowHintJob = launch {
                delay(SLOW_RESPONSE_THRESHOLD_MS)
                _uiState.update { it.copy(showSlowHint = true) }
            }

            val result = dashboardRepository.getEntities(keypass)
            slowHintJob.cancel()

            _uiState.update {
                when (result) {
                    is ApiResult.Success -> it.copy(
                        isLoading = false,
                        showSlowHint = false,
                        entities = result.data.entities,
                        total = result.data.total
                    )
                    is ApiResult.Failure -> it.copy(
                        isLoading = false,
                        showSlowHint = false,
                        errorMessage = result.error.toDashboardMessage()
                    )
                }
            }
        }
    }

    companion object {
        const val KEYPASS_ARG = "keypass"
        const val SLOW_RESPONSE_THRESHOLD_MS = 5_000L
    }
}
