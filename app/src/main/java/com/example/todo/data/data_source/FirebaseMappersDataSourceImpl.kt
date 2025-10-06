package com.example.todo.data.data_source

import com.example.core.data.dto.UserDto
import com.example.core.data.mappers.toDomain
import com.example.core.domain.model.User
import com.example.todo.data.dto.ProjectDto
import com.example.todo.data.dto.TaskDto
import com.example.todo.data.mappers.toDomain
import com.example.todo.domain.model.Project
import com.example.todo.domain.model.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldPath
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

class FirebaseMappersDataSourceImpl(
    private val firestore: FirebaseFirestore,
    private val chunkSize: Int = 10
) : FirebaseMappersDataSource {

    private val userCache = mutableMapOf<String, User>()

    private suspend fun fetchProjectDtosByIds(projectIds: List<String>): List<ProjectDto> {
        if (projectIds.isEmpty()) return emptyList()
        return coroutineScope {
            projectIds.distinct()
                .chunked(chunkSize)
                .map { chunk ->
                    async {
                        firestore.collection("projects")
                            .whereIn(FieldPath.documentId(), chunk)
                            .get()
                            .await()
                            .toObjects(ProjectDto::class.java)
                    }
                }
                .awaitAll()
                .flatten()
        }
    }

    private suspend fun fetchTaskDtosByIds(taskIds: List<String>): List<TaskDto> {
        if (taskIds.isEmpty()) return emptyList()
        return coroutineScope {
            taskIds.distinct()
                .chunked(chunkSize)
                .map { chunk ->
                    async {
                        firestore.collection("tasks")
                            .whereIn(FieldPath.documentId(), chunk)
                            .get()
                            .await()
                            .toObjects(TaskDto::class.java)
                    }
                }
                .awaitAll()
                .flatten()
        }
    }

    private suspend fun fetchUsersAndCache(userIds: Collection<String>): Map<String, User> {
        val missing = userIds.filter { !userCache.containsKey(it) }.distinct()
        if (missing.isEmpty()) {
            return userCache.filterKeys { it in userIds }
        }

        val dtos = coroutineScope {
            missing.chunked(chunkSize)
                .map { chunk ->
                    async {
                        firestore.collection("users")
                            .whereIn(FieldPath.documentId(), chunk)
                            .get()
                            .await()
                            .toObjects(UserDto::class.java)
                    }
                }
                .awaitAll()
                .flatten()
        }

        dtos.forEach { dto ->
            val u = dto.toDomain()
            userCache[u.id] = u
        }

        return userIds.associateWithNotNull { userCache[it] }
    }

    private fun <K, V> Collection<K>.associateWithNotNull(transform: (K) -> V?): Map<K, V> {
        val m = mutableMapOf<K, V>()
        for (k in this) {
            val v = transform(k)
            if (v != null) m[k] = v
        }
        return m
    }

    override suspend fun mapListOfTaskIdsToTasks(taskIds: List<String>): List<Task> {
        if (taskIds.isEmpty()) return emptyList()

        val taskDtos = fetchTaskDtosByIds(taskIds)


        val userIds = mutableSetOf<String>()
        taskDtos.forEach { dto ->
            if (dto.userId.isNotBlank()) userIds.add(dto.userId)
            dto.assignedTo.forEach { userIds.add(it) }
        }

        val userMap = fetchUsersAndCache(userIds)

        return taskDtos.map { dto ->
            val owner = userMap[dto.userId]
                ?: throw Exception("User ${dto.userId} not found for task ${dto.id}")
            val assigned = dto.assignedTo.mapNotNull { userMap[it] }
            dto.toDomain(owner = owner, assignedTo = assigned)
        }
    }

    override suspend fun mapProjectIdToProject(projectId: String): Project {
        val projectDto = firestore.collection("projects").document(projectId)
            .get().await()
            .toObject(ProjectDto::class.java) ?: throw Exception("Project not found")

        val taskDtos = fetchTaskDtosByIds(projectDto.tasksIds)

        val userIds = mutableSetOf<String>()
        if (projectDto.ownerId.isNotBlank()) userIds.add(projectDto.ownerId)
        projectDto.users.forEach { if (it.isNotBlank()) userIds.add(it) }
        taskDtos.forEach {
            if (it.userId.isNotBlank()) userIds.add(it.userId)
            it.assignedTo.forEach { aid -> if (aid.isNotBlank()) userIds.add(aid) }
        }

        val userMap = fetchUsersAndCache(userIds)

        val tasks = taskDtos.map { dto ->
            val owner = userMap[dto.userId]
                ?: throw Exception("User ${dto.userId} not found for task ${dto.id}")
            val assigned = dto.assignedTo.mapNotNull { userMap[it] }
            dto.toDomain(owner = owner, assignedTo = assigned)
        }

        val owner =
            userMap[projectDto.ownerId] ?: throw Exception("Owner not found ${projectDto.ownerId}")
        val projUsers = projectDto.users.mapNotNull { userMap[it] }

        return projectDto.toDomain(owner = owner, users = projUsers, tasks = tasks)
    }

    override suspend fun mapListOfProjectIdsToProjects(projectIds: List<String>): List<Project> {
        if (projectIds.isEmpty()) return emptyList()

        val projectsDto = fetchProjectDtosByIds(projectIds)
        val allTaskIds = projectsDto.flatMap { it.tasksIds }.distinct()
        val allTaskDtos = fetchTaskDtosByIds(allTaskIds)
        val userIds = mutableSetOf<String>()

        projectsDto.forEach {
            if (it.ownerId.isNotBlank()) userIds.add(it.ownerId)
            it.users.forEach { id -> if (id.isNotBlank()) userIds.add(id) }
        }
        allTaskDtos.forEach { t ->
            if (t.userId.isNotBlank()) userIds.add(t.userId)
            t.assignedTo.forEach { userIds.add(it) }
        }

        val userMap = fetchUsersAndCache(userIds)

        val tasksByProject = allTaskDtos.mapNotNull { dto ->
            val owner = userMap[dto.userId] ?: return@mapNotNull null
            val assigned = dto.assignedTo.mapNotNull { userMap[it] }
            dto.toDomain(owner = owner, assignedTo = assigned)
        }.groupBy { it.projectId }


        return projectsDto.map { dto ->
            val owner = userMap[dto.ownerId] ?: throw Exception("Owner not found ${dto.ownerId}")
            val projUsers = dto.users.mapNotNull { userMap[it] }
            val tasks = tasksByProject[dto.id] ?: emptyList()
            dto.toDomain(owner = owner, users = projUsers, tasks = tasks)
        }
    }

    override suspend fun mapUserIdToUser(userId: String): User {
        userCache[userId]?.let { return it }
        val dto = firestore.collection("users").document(userId).get().await()
            .toObject(UserDto::class.java) ?: throw Exception("User not found")
        val user = dto.toDomain()
        userCache[userId] = user
        return user
    }

    override suspend fun mapListOfUserIdsToUsers(userIds: List<String>): List<User> {
        if (userIds.isEmpty()) return emptyList()
        val map = fetchUsersAndCache(userIds)
        return userIds.mapNotNull { map[it] }
    }
}
