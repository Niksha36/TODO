package com.example.todo.domain.use_cases

import com.example.core.utils.Resource
import com.example.todo.domain.TodoRepository
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.flow
import java.io.IOException

class RemoveTaskFromProjectUseCase(
    private val repository: TodoRepository
) {
    operator fun invoke(projectId: String, taskId: String) = flow {
        emit(Resource.Loading())
        try {
            repository.removeTaskFromProject(projectId, taskId)
            emit(Resource.Success( Unit))
        } catch (e: FirebaseFirestoreException) {
            emit(Resource.Error(e.localizedMessage ?: "An error occurred with Firestore."))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred."))
        }
    }
}