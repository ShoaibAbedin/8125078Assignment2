package com.s8125078.assignment2.ui.login

import androidx.annotation.StringRes

data class LoginUiState(
    val isLoading: Boolean = false,
    val showSlowHint: Boolean = false,
    @StringRes val studentIdError: Int? = null,
    @StringRes val passwordError: Int? = null,
    @StringRes val errorMessage: Int? = null,
    val keypass: String? = null // set when login succeeds -> fragment navigates
)
