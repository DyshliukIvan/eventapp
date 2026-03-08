package com.dyshiuk.eventapp.network

data class FakeLoginRequest(
    val email: String,
    val name: String
)

data class AuthResponse(
    val accessToken: String,
    val tokenType: String,
    val userId: Long,
    val email: String,
    val name: String,
    val message: String
)

data class CurrentUserResponse(
    val userId: Long,
    val email: String,
    val name: String,
    val role: String
)