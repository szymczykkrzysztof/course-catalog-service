package com.komy.coursecatalogservice.security

data class RegisterRequest(
    val username: String,
    val password: String
)

data class RegisterResponse(
    val id: Long,
    val username: String,
    val roles: Set<Role>,
    val token: String
)