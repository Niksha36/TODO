package com.example.auth.presentation.login_screen

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.auth.domain.use_cases.LoginUseCase
import com.example.todo.presentation.utils.Utils.isValidEmail
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class LoginViewModel(
    private val loginUseCase: LoginUseCase
): ViewModel() {
    var state by mutableStateOf(LoginScreenState())
        private set

    fun onEvent(event: LoginScreenEvent) {
        when (event) {
            is LoginScreenEvent.OnEmailChange -> {
                state = state.copy(email = event.email)
                state = state.copy(isEmailCorrect = isValidEmail(event.email))
            }

            is LoginScreenEvent.OnPasswordChange -> {
                state = state.copy(password = event.password)
            }

            LoginScreenEvent.OnLoginClick -> {
                login(state.email, state.password)
            }
        }
    }

    private fun login(email: String, password: String) {
        loginUseCase(email, password).onEach { result ->
            when (result) {
                is com.example.core.utils.Resource.Loading -> {
                    state = state.copy(isLoading = true, error = null, isLoginSuccess = false)
                }

                is com.example.core.utils.Resource.Success -> {
                    state = state.copy(
                        isLoading = false,
                        error = null,
                        isLoginSuccess = true
                    )
                }

                is com.example.core.utils.Resource.Error -> {
                    state = state.copy(
                        isLoading = false,
                        error = result.message,
                        isLoginSuccess = false
                    )
                }
            }
        }.launchIn(viewModelScope)
    }


}