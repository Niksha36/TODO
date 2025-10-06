package com.example.core.data

import com.example.core.domain.CoreRepository
import com.google.firebase.auth.FirebaseAuth

class CoreRepositoryImpl(
    private val auth: FirebaseAuth
): CoreRepository {
    override fun checkIsUserAuthorized(): Boolean {
        return auth.currentUser != null
    }
}