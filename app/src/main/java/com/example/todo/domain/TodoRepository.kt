package com.example.todo.domain


import com.example.core.domain.model.User
import com.example.core.utils.Resource
import com.example.todo.domain.model.Project
import com.example.todo.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TodoRepository {
    fun getCurrentUser(): User
    fun currentUserFlow(): Flow<User?>
    fun getProjectByIdFlow(projectId: String): Flow<Project>
    fun getUserProjectsFlow(userId: String): Flow<List<Project>>
    suspend fun createProject(project: Project): Unit
    suspend fun addTaskToProject(projectId: String, task: Task): Unit
    suspend fun removeTaskFromProject(projectId: String, taskId: String): Unit
    suspend fun getUserByEmail(email: String): User?
    suspend fun updateTaskInProject(task: Task): Unit
    suspend fun getTaskById(taskId: String): Task
    fun signOut(): Unit
    suspend fun removeProject(projectId: String): Unit
}