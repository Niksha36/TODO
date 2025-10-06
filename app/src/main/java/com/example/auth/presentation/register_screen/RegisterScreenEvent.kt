package com.example.auth.presentation.register_screen

sealed interface RegisterScreenEvent {
    data class OnEmailChange(val email: String) : RegisterScreenEvent
    data class OnPasswordChange(val password: String) : RegisterScreenEvent
    data class OnRepeatPasswordChange(val repeatPassword: String) : RegisterScreenEvent
    data object OnRegisterClick : RegisterScreenEvent
    data class OnNameChange(val name: String) : RegisterScreenEvent
    data class OnSurnameChange(val surname: String) : RegisterScreenEvent
}