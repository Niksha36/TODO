package com.example.todo.data

import com.example.core.data.dto.UserDto
import com.example.core.data.mappers.toDomain
import com.example.core.data.mappers.toUser
import com.example.core.domain.model.User
import com.example.todo.data.data_source.FirebaseMappersDataSource
import com.example.todo.data.dto.ProjectDto
import com.example.todo.data.dto.TaskDto
import com.example.todo.data.mappers.toDomain
import com.example.todo.data.mappers.toDto
import com.example.todo.domain.TodoRepository
import com.example.todo.domain.model.Project
import com.example.todo.domain.model.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class TodoRepositoryImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val firebaseMappers: FirebaseMappersDataSource,
    private val appScope: CoroutineScope
) : TodoRepository {
    override fun getCurrentUser(): User {
        val firebaseUser = auth.currentUser
        return firebaseUser?.toUser() ?: throw IllegalStateException("User not logged in")
    }

    private val _raw = callbackFlow<User?> {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser?.toUser()).isSuccess
        }
        auth.addAuthStateListener(listener)
        trySend(auth.currentUser?.toUser()).isSuccess
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    private val shared: Flow<User?> =
        _raw.shareIn(appScope, SharingStarted.WhileSubscribed(5_000), replay = 1)

    override fun currentUserFlow(): Flow<User?> = shared


    override fun getProjectByIdFlow(projectId: String): Flow<Project> = callbackFlow {
        var emitJob: Job? = null
        fun scheduleEmit() {
            emitJob?.cancel()
            emitJob = launch(Dispatchers.IO) {
                try {
                    val project = firebaseMappers
                        .mapListOfProjectIdsToProjects(listOf(projectId))
                        .firstOrNull() ?: throw NoSuchElementException("Project not found")
                    trySend(project).isSuccess
                } catch (e: Throwable) {
                    if (e is CancellationException) return@launch
                    close(e)
                }
            }
        }

        val projectListener = firestore.collection("projects")
            .document(projectId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error); return@addSnapshotListener
                }
                if (snapshot == null || !snapshot.exists()) {
                    close(NoSuchElementException("Project not found")); return@addSnapshotListener
                }
                scheduleEmit()
            }

        val tasksListener = firestore.collection("tasks")
            .whereEqualTo("projectId", projectId)
            .addSnapshotListener { _, error ->
                if (error != null) {
                    close(error); return@addSnapshotListener
                }

                scheduleEmit()
            }

        scheduleEmit()

        awaitClose {
            projectListener.remove()
            tasksListener.remove()
            emitJob?.cancel()
        }
    }


    override fun getUserProjectsFlow(userId: String): Flow<List<Project>> = callbackFlow {
        var ownerProjects = listOf<ProjectDto>()
        var memberProjects = listOf<ProjectDto>()
        var emitJob: Job? = null
        val scope = this

        fun scheduleEmit() {
            emitJob?.cancel()
            emitJob = scope.launch {
                try {
                    val allProjectDtos = (ownerProjects + memberProjects).distinctBy { it.id }
                    val projectIds = allProjectDtos.map { it.id }
                    val projects = if (projectIds.isEmpty()) {
                        emptyList()
                    } else {
                        withContext(Dispatchers.IO) {
                            firebaseMappers.mapListOfProjectIdsToProjects(projectIds)
                        }
                    }
                    trySend(projects).isSuccess
                } catch (e: Throwable) {
                    // Отмена — штатная ситуация при переподписке/дебаунсе: flow не закрываем
                    if (e is CancellationException) return@launch
                    close(e)
                }
            }
        }

        val ownerListener = firestore.collection("projects")
            .whereEqualTo("ownerId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error); return@addSnapshotListener
                }
                ownerProjects = snapshot?.toObjects(ProjectDto::class.java) ?: emptyList()
                scheduleEmit()
            }

        val memberListener = firestore.collection("projects")
            .whereArrayContains("users", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error); return@addSnapshotListener
                }
                memberProjects = snapshot?.toObjects(ProjectDto::class.java) ?: emptyList()
                scheduleEmit()
            }

        awaitClose {
            ownerListener.remove()
            memberListener.remove()
            emitJob?.cancel()
        }
    }
    // .shareIn(appScope, SharingStarted.WhileSubscribed(5000), replay = 1)


    override suspend fun createProject(project: Project) {
        val projectDto = project.toDto()
        firestore.collection("projects").add(projectDto).await()
    }

    override suspend fun addTaskToProject(projectId: String, task: Task) {
        val taskDto = task.toDto()

        firestore.runTransaction { transaction ->
            val taskRef = firestore.collection("tasks").document()
            transaction.set(taskRef, taskDto)

            val projectRef = firestore.collection("projects").document(projectId)
            transaction.update(projectRef, "tasksIds", FieldValue.arrayUnion(taskRef.id))
        }.await()
    }


    override suspend fun removeTaskFromProject(projectId: String, taskId: String) {
        firestore.runTransaction { transaction ->
            val projectRef = firestore.collection("projects").document(projectId)
            val taskRef = firestore.collection("tasks").document(taskId)

            transaction.update(projectRef, "tasksIds", FieldValue.arrayRemove(taskId))
            transaction.delete(taskRef)
        }.await()
    }

    override suspend fun getUserByEmail(email: String): User? {
        val querySnapshot = firestore.collection("users")
            .whereEqualTo("email", email)
            .limit(1)
            .get()
            .await()

        if (querySnapshot.isEmpty) {
            return null
        }

        val document = querySnapshot.documents.first()
        return document.toObject(UserDto::class.java)?.toDomain()
    }

    override suspend fun updateTaskInProject(task: Task) {
        val taskDto = task.toDto()
        val taskRef = firestore.collection("tasks").document(task.id)
        taskRef.set(taskDto).await()
    }

    override suspend fun getTaskById(taskId: String): Task {
        val taskDto = firestore.collection("tasks")
            .document(taskId)
            .get()
            .await()
            .toObject(TaskDto::class.java)
            ?: throw NoSuchElementException("Task with id $taskId not found")

        val owner = firebaseMappers.mapUserIdToUser(taskDto.userId)
        val assignedToIds =
            taskDto.assignedTo.map { userId -> firebaseMappers.mapUserIdToUser(userId) }

        return taskDto.toDomain(
            owner = owner,
            assignedTo = assignedToIds
        )
    }

    override fun signOut() {
        auth.signOut()
    }

    override suspend fun removeProject(projectId: String) {
        val tasksSnapshot = firestore.collection("tasks")
            .whereEqualTo("projectId", projectId)
            .get()
            .await()

        val batch = firestore.batch()
        for (document in tasksSnapshot.documents) {
            batch.delete(document.reference)
        }
        val projectRef = firestore.collection("projects").document(projectId)
        batch.delete(projectRef)

        batch.commit().await()
    }
}