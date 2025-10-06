package com.example.core.data.dto

import com.google.firebase.firestore.DocumentId

data class UserDto(
    @DocumentId
    val id: String = "",
    val email: String = "",
    val displayName: String = "",
    val avatarUrl: String? = null
)