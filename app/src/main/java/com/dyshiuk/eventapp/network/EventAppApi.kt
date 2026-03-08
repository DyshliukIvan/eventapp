package com.dyshiuk.eventapp.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface EventAppApi {

    @POST("auth/fake-login")
    suspend fun fakeLogin(
        @Body request: FakeLoginRequest
    ): AuthResponse

    @GET("auth/me")
    suspend fun getCurrentUser(
        @Header("Authorization") authorization: String
    ): CurrentUserResponse

    @GET("events")
    suspend fun getEvents(): List<EventDto>

    @POST("auth/google")
    suspend fun googleLogin(
        @Body request: GoogleLoginRequest
    ): AuthResponse
}