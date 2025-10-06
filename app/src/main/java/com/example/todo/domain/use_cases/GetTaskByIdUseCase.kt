package com.example.todo.domain.use_cases

import com.example.core.utils.Resource
import com.example.todo.domain.TodoRepository
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.flow
import java.io.IOException

class GetTaskByIdUseCase(
    private val repository: TodoRepository
) {
    operator fun invoke(taskId: String) = flow {
        emit(Resource.Loading())
        try {
            val task = repository.getTaskById(taskId)
            emit(Resource.Success( task))
        } catch (e: FirebaseFirestoreException) {
            emit(Resource.Error(e.localizedMessage ?: "An error occurred with Firestore."))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred."))
        }
    }
}