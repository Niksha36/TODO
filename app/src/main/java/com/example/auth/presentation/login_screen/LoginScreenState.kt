package com.example.auth.presentation.login_screen

data class LoginScreenState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoginSuccess: Boolean = false,
    val isEmailCorrect: Boolean? = null,
    val emailFieldError: String? = null,
)
