package com.example.todo.domain.use_cases

import com.example.core.utils.Resource
import com.example.todo.domain.TodoRepository
import com.example.todo.domain.model.Project
import com.google.firebase.FirebaseException
import kotlinx.coroutines.flow.flow
import java.io.IOException

class CreateProjectUseCase(private val repository: TodoRepository) {
    operator fun invoke(project: Project) = flow {
        emit(Resource.Loading())
        try {
            repository.createProject(project)
            emit(Resource.Success(Unit))
        } catch (e: FirebaseException) {
            val message = when (e.message) {
                "PERMISSION_DENIED" -> "Permission denied. You don't have access to create a project."
                else -> e.localizedMessage ?: "Firebase error occurred."
            }
            emit(Resource.Error(message))
        } catch (e: IOException) {
            emit(Resource.Error("Network error. Please check your internet connection."))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Unknown error occurred while creating project."))
        }
    }
}