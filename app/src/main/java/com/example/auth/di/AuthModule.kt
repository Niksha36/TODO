package com.example.auth.di

import com.example.auth.data.AuthRepositoryImpl
import com.example.auth.domain.repository.AuthRepository
import com.example.auth.domain.use_cases.LoginUseCase
import com.example.auth.domain.use_cases.RegisterUseCase
import com.example.auth.presentation.login_screen.LoginViewModel
import com.example.auth.presentation.register_screen.RegisterViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val authModule = module {
    viewModelOf(::LoginViewModel)
    viewModelOf(::RegisterViewModel)
    singleOf(::RegisterUseCase)
    singleOf(::LoginUseCase)
    singleOf(::AuthRepositoryImpl).bind<AuthRepository>()
}