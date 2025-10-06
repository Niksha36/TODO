package com.example.core.data.mappers

import com.example.core.domain.model.User
import com.google.firebase.auth.FirebaseUser

fun FirebaseUser.toUser(): User {
    return User(
        id = this.uid,
        email = this.email ?: "",
        displayName = this.displayName ?: "",
        avatarUrl = this.photoUrl?.toString()
    )
}