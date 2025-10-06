package com.example.todo.domain.use_cases

import com.example.core.utils.Resource
import com.example.todo.domain.TodoRepository
import com.example.todo.domain.model.Task
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException

class AddTaskToProjectUseCase(
    private val repository: TodoRepository
) {
    suspend operator fun invoke(projectId: String, task: Task): Resource<Unit> {
        return try {
            repository.addTaskToProject(
                projectId = projectId,
                task = task
            )
            Resource.Success(Unit)
        }  catch (e: FirebaseFirestoreException) {
            val message = when (e.code) {
                FirebaseFirestoreException.Code.PERMISSION_DENIED -> "Permission denied to add the task."
                FirebaseFirestoreException.Code.NOT_FOUND -> "Project not found."
                FirebaseFirestoreException.Code.UNAVAILABLE -> "The service is currently unavailable. Please try again later."
                else -> "Database error: ${e.localizedMessage}"
            }
            Resource.Error(message)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "An unknown error occurred")
        }
    }
}