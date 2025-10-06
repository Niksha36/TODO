package com.example.todo.domain.use_cases

import com.example.core.utils.Resource
import com.example.todo.domain.TodoRepository
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.flow
import java.io.IOException

class RemoveProjectUseCase(private val repository: TodoRepository) {
    operator fun invoke(projectId: String) = flow {
        emit(Resource.Loading())
        try {
            repository.removeProject(projectId)
            emit(Resource.Success(Unit))
        } catch (e: CancellationException) {
            throw e
        } catch (e: FirebaseFirestoreException) {
            val message = when (e.code) {
                FirebaseFirestoreException.Code.PERMISSION_DENIED -> "Permission denied to remove the project"
                FirebaseFirestoreException.Code.NOT_FOUND -> "Project not found"
                FirebaseFirestoreException.Code.UNAVAILABLE -> "Firestore service is unavailable. Please try again"
                FirebaseFirestoreException.Code.DEADLINE_EXCEEDED -> "Request timed out. Please try again"
                FirebaseFirestoreException.Code.ABORTED -> "Operation aborted. Please retry"
                FirebaseFirestoreException.Code.FAILED_PRECONDITION -> "Precondition failed for this operation"
                FirebaseFirestoreException.Code.RESOURCE_EXHAUSTED -> "Quota exceeded. Try again later"
                FirebaseFirestoreException.Code.UNAUTHENTICATED -> "User is not authenticated"
                FirebaseFirestoreException.Code.INTERNAL -> "Internal Firestore error. Try again later"
                FirebaseFirestoreException.Code.CANCELLED -> "Operation cancelled"
                else -> "Database error"
            }
            emit(Resource.Error(message))
        } catch (e: IOException) {
            emit(Resource.Error("Network error. Check your connection and try again"))
        } catch (e: Exception) {
            emit(Resource.Error("Unexpected error occurred"))
        }
    }
}