package com.example.todo.di

import com.example.todo.data.TodoRepositoryImpl
import com.example.todo.data.data_source.FirebaseMappersDataSource
import com.example.todo.data.data_source.FirebaseMappersDataSourceImpl
import com.example.todo.domain.TodoRepository
import com.example.todo.domain.use_cases.AddTaskToProjectUseCase
import com.example.todo.domain.use_cases.CreateProjectUseCase
import com.example.todo.domain.use_cases.GetCurrentUserFlowUseCase
import com.example.todo.domain.use_cases.GetCurrentUserUseCase
import com.example.todo.domain.use_cases.GetProjectByIdUseCase
import com.example.todo.domain.use_cases.GetTaskByIdUseCase
import com.example.todo.domain.use_cases.GetUserByEmailUseCase
import com.example.todo.domain.use_cases.GetUserProjectsUseCase
import com.example.todo.domain.use_cases.RemoveProjectUseCase
import com.example.todo.domain.use_cases.RemoveTaskFromProjectUseCase
import com.example.todo.domain.use_cases.SignOutUseCase
import com.example.todo.domain.use_cases.UpdateTaskUseCase
import com.example.todo.presentation.create_task_screen.CreateTaskScreenViewModel
import com.example.todo.presentation.main_screen.MainScreenViewModel
import com.example.todo.presentation.tasks_screen.TasksScreenViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val todoModule = module {
    single<FirebaseMappersDataSource> { FirebaseMappersDataSourceImpl(get(), chunkSize = 10) }

    single<TodoRepository> { TodoRepositoryImpl(get(), get(), get(), get()) }

    // UseCases
    singleOf(::AddTaskToProjectUseCase)
    singleOf(::CreateProjectUseCase)
    singleOf(::GetCurrentUserFlowUseCase)
    singleOf(::GetCurrentUserUseCase)
    singleOf(::GetProjectByIdUseCase)
    singleOf(::GetUserByEmailUseCase)
    singleOf(::GetUserProjectsUseCase)
    singleOf(::UpdateTaskUseCase)
    singleOf(::RemoveTaskFromProjectUseCase)
    singleOf(::GetTaskByIdUseCase)
    singleOf(::SignOutUseCase)
    singleOf(::RemoveProjectUseCase)
    // ViewModel
    viewModel { (navigateToAuth: () -> Unit) ->
        MainScreenViewModel(
            getCurrentUserFlowUseCase = get(),
            getUserProjectsUseCase = get(),
            createProjectUseCase = get(),
            getUserByEmailUseCase = get(),
            removeProjectUseCase = get(),
            SignOutUseCase = get(),
            getProjectByIdUseCase = get(),
            navigateToAuth = navigateToAuth
        )
    }
    viewModelOf(::TasksScreenViewModel)
    viewModelOf(::CreateTaskScreenViewModel)
}