package com.example.core.data.mappers

import com.example.core.data.dto.UserDto
import com.example.core.domain.model.User

fun UserDto.toDomain(): User {
    return User(
        id = this.id,
        email = this.email,
        displayName = this.displayName,
        avatarUrl = this.avatarUrl
    )
}

fun User.toDto(): UserDto {
    return UserDto(
        id = this.id,
        email = this.email,
        displayName = this.displayName,
        avatarUrl = this.avatarUrl
    )
}