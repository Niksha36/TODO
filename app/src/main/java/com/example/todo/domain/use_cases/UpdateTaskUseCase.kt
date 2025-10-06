package com.example.todo.domain.use_cases

import com.example.core.utils.Resource
import com.example.todo.domain.TodoRepository
import com.example.todo.domain.model.Task
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.flow
import java.io.IOException

class UpdateTaskUseCase(private val repository: TodoRepository) {
    operator fun invoke(task: Task) = flow<Resource<Unit>> {
        emit(Resource.Loading())
        try {
            repository.updateTaskInProject(task)
            emit(Resource.Success(Unit))
        } catch (e: FirebaseFirestoreException) {
            emit(Resource.Error(e.localizedMessage ?: "A database error occurred"))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
    }
}