package com.example.auth.presentation.login_screen

sealed interface LoginScreenEvent {
    data class OnEmailChange(val email: String) : LoginScreenEvent
    data class OnPasswordChange(val password: String) : LoginScreenEvent
    data object OnLoginClick : LoginScreenEvent
}