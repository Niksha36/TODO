package com.example.app

import android.app.Application
import com.example.auth.di.authModule
import com.example.core.di.coreModule
import com.example.todo.di.todoModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class TodoApp: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@TodoApp)
            modules( authModule, coreModule, todoModule)
        }
    }
}