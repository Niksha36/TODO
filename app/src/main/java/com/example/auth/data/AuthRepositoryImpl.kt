package com.example.auth.data

import androidx.core.net.toUri
import com.example.auth.domain.repository.AuthRepository
import com.example.core.data.dto.UserDto
import com.example.core.data.mappers.toDomain
import com.example.core.data.mappers.toDto
import com.example.core.domain.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {
    override suspend fun login(
        email: String,
        password: String
    ): User {
        val authResult = auth.signInWithEmailAndPassword(email, password).await()
        val firebaseUser = authResult.user ?: throw Exception("Login failed: Firebase user is null")

        val userDocument = firestore.collection("users")
            .document(firebaseUser.uid)
            .get()
            .await()

        return userDocument.toObject(UserDto::class.java)?.toDomain()
            ?: throw Exception("User data not found in Firestore")
    }


    override suspend fun register(
        user: User,
        password: String
    ): User {
        val authResult = auth.createUserWithEmailAndPassword(user.email, password).await()
        val firebaseUser =
            authResult.user ?: throw Exception("Registration succeeded but Firebase user is null")

        val profileBuilder = UserProfileChangeRequest.Builder()
            .setDisplayName(user.displayName)

        user.avatarUrl?.let { avatar ->
            profileBuilder.setPhotoUri(avatar.toUri())
        }

        firebaseUser.updateProfile(profileBuilder.build()).await()

        val profileData = user.copy(id = firebaseUser.uid)
        firestore.collection("users")
            .document(firebaseUser.uid)
            .set(profileData.toDto())
            .await()

        return profileData
    }
}