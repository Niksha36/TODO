package com.example.todo.data.data_source

import com.example.core.domain.model.User
import com.example.todo.domain.model.Project
import com.example.todo.domain.model.Task

interface FirebaseMappersDataSource {
    suspend fun mapUserIdToUser(userId: String): User
    suspend fun mapListOfUserIdsToUsers(userIds: List<String>): List<User>
    suspend fun mapListOfTaskIdsToTasks(taskIds: List<String>): List<Task>
    suspend fun mapProjectIdToProject(projectId: String): Project
    suspend fun mapListOfProjectIdsToProjects(projectIds: List<String>): List<Project>
}