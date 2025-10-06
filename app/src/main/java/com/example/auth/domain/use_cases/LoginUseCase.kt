package com.example.auth.domain.use_cases

import com.example.auth.domain.repository.AuthRepository
import com.example.core.utils.Resource
import kotlinx.coroutines.flow.flow

class LoginUseCase(
    private val repository: AuthRepository
) {
    operator fun invoke(email: String, password: String) = flow {
        emit(Resource.Loading())
        try {
            val user = repository.login(email, password)
            emit(Resource.Success(user))
        }
        catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An unexpected error occurred"))
        }
    }
}