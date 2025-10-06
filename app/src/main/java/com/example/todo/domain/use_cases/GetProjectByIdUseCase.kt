package com.example.todo.domain.use_cases

import com.example.core.utils.Resource
import com.example.todo.domain.model.Project
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class GetProjectByIdUseCase(
    private val repository: com.example.todo.domain.TodoRepository
) {
    operator fun invoke(projectId: String) =
        repository.getProjectByIdFlow(projectId)
            .map<Project, Resource<Project>> { project ->
                Resource.Success(project)
            }
            .onStart { emit(Resource.Loading()) }
            .catch { e ->
                if (e is CancellationException) throw e
                emit(Resource.Error(e.message ?: "Unknown error occurred"))
            }
            .distinctUntilChanged()
}