package com.example.auth.presentation.register_screen


data class RegisterScreenState(
    val email: String = "",
    val password: String = "",
    val repeatPassword: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val emailFieldError: String? = null,
    val isRegisterSuccess: Boolean = false,
    val isEmailCorrect: Boolean? = null,
    val passwordFieldError: String? = null,
    val repeatPasswordFieldError: String? = null,
    val name: String = "",
    val surname: String = "",
    val avatarUrl: String? = null
)
