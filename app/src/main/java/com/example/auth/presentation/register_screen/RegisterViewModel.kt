package com.example.auth.presentation.register_screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.auth.domain.use_cases.RegisterUseCase
import com.example.core.domain.model.User
import com.example.core.utils.Resource
import com.example.todo.presentation.utils.Utils.isValidEmail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class RegisterViewModel(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {
    var state by mutableStateOf(RegisterScreenState())
        private set

    fun onEvent(event: RegisterScreenEvent) {
        when (event) {
            is RegisterScreenEvent.OnEmailChange -> {
                val email = event.email.filter { !it.isWhitespace() }
                state = state.copy(email = email)
                state = state.copy(
                    isEmailCorrect = isValidEmail(event.email),
                    emailFieldError = if (isValidEmail(email)) null else "Invalid email format"
                )
            }

            is RegisterScreenEvent.OnPasswordChange -> {
                val password = event.password
                state = state.copy(password = event.password)
                when {
                    password.length < 8 -> {
                        state =
                            state.copy(passwordFieldError = "Password must be at least 8 characters")
                    }

                    !password.any { it.isDigit() } -> {
                        state =
                            state.copy(passwordFieldError = "Password must contain at least one digit")
                    }

                    !password.any { it.isUpperCase() } -> {
                        state =
                            state.copy(passwordFieldError = "Password must contain at least one uppercase letter")
                    }

                    else -> {
                        state = state.copy(passwordFieldError = null)
                    }
                }
            }

            RegisterScreenEvent.OnRegisterClick -> {
                val user = User(
                    email = state.email,
                    displayName = "${state.name} ${state.surname}",
                    avatarUrl = state.avatarUrl
                )
                register(
                    user = user,
                    password = state.password
                )
            }

            is RegisterScreenEvent.OnRepeatPasswordChange -> {
                state = state.copy(repeatPassword = event.repeatPassword)
            }

            is RegisterScreenEvent.OnNameChange -> {
                state = state.copy(name = filterName(event.name))
            }

            is RegisterScreenEvent.OnSurnameChange -> {
                state = state.copy(surname = filterName(event.surname))
            }
        }
    }

    private fun register(user: User, password: String) {

        registerUseCase(
            user = user,
            password = password,
        ).onEach {
            state = state.copy(isLoading = true, error = null)
            state = when (it) {
                is Resource.Loading -> {
                    state.copy(isLoading = true, error = null)
                }

                is Resource.Success -> {
                    state.copy(
                        isLoading = false,
                        isRegisterSuccess = true,
                        error = null
                    )
                }

                is Resource.Error -> {
                    state.copy(isLoading = false, error = it.message)
                }
            }
        }.flowOn(Dispatchers.IO).launchIn(viewModelScope)
    }
}

private fun filterName(name: String): String {
    return name.replace(Regex("[^A-Za-z]"), "")
}
