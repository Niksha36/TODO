package com.example.todo.domain.use_cases

import com.example.core.utils.Resource
import com.example.todo.domain.TodoRepository
import com.example.todo.domain.model.Project
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class GetUserProjectsUseCase(
    private val repository: TodoRepository
) {
    operator fun invoke(userId: String) =
        repository.getUserProjectsFlow(
            userId
        ).map<List<Project>, Resource<List<Project>>> { projectList -> Resource.Success(projectList) }
            .onStart { emit(Resource.Loading()) }
            .catch { e ->
                if (e is CancellationException) throw e
                emit(Resource.Error(e.message ?: "Unknown error occurred"))
            }
            .distinctUntilChanged()

}