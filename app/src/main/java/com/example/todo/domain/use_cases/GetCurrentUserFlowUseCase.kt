package com.example.todo.domain.use_cases

import com.example.core.domain.model.User
import com.example.core.utils.Resource
import com.example.todo.domain.TodoRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class GetCurrentUserFlowUseCase(private val repository: TodoRepository) {
    operator fun invoke() =
        repository.currentUserFlow()
            .map<User?, Resource<User?>> { user -> Resource.Success(user) }
            .onStart { emit(Resource.Loading()) }
            .catch { e ->
                if (e is CancellationException) throw e
                emit(Resource.Error(e.message ?: "Unknown error occurred"))
            }
            .distinctUntilChanged()
}
