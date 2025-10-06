package com.example.core.domain

interface CoreRepository {
    fun checkIsUserAuthorized(): Boolean
}