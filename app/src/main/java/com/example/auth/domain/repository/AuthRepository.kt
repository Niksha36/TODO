package com.example.auth.domain.repository

import com.example.core.domain.model.User


interface AuthRepository {
    suspend fun login(email: String, password: String): User
    suspend fun register(user: User, password: String): User
}