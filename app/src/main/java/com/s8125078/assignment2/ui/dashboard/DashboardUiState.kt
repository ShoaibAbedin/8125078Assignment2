package com.s8125078.assignment2.ui.dashboard

import androidx.annotation.StringRes
import com.s8125078.assignment2.data.model.Entity

data class DashboardUiState(
    val topic: String = "",
    val isLoading: Boolean = true,
    val showSlowHint: Boolean = false,
    val entities: List<Entity> = emptyList(),
    val total: Int = 0,
    @StringRes val errorMessage: Int? = null
) {
    val isEmpty: Boolean
        get() = !isLoading && errorMessage == null && entities.isEmpty()
}
