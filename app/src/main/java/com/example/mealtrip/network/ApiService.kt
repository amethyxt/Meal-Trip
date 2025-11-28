package com.example.mealtrip.network

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("/api/users/register")
    suspend fun registerUser(@Body request: RegisterRequest): Response<UserResponse>

    @POST("/api/trips")
    suspend fun createTrip(@Body request: CreateTripRequest): Response<TripResponse>

    @POST("/api/trips/join")
    suspend fun joinTrip(@Body request: JoinTripRequest): Response<JoinTripResponse>

    @POST("/api/votes")
    suspend fun submitVote(@Body request: VoteRequest): Response<VoteResponse>

    @GET("/api/trips/{trip_id}/results")
    suspend fun getTripResults(@Path("trip_id") tripId: String): Response<TripPackageResponse>

    @POST("/api/users/login")
    suspend fun loginUser(@Body request: LoginRequest): Response<LoginResponse>

    @GET("/api/pois")
    suspend fun getRealPois(
        @Query("q") query: String
    ): Response<List<PoiItem>>

    // API 8: บันทึกความชอบ
    @PUT("/api/users/{id}/preferences")
    fun savePreferences(
        @Path("id") userId: String,
        @Body request: PreferencesRequest
    ): Call<UserResponse>

    // ▼▼▼ (API Lobby: ห้องรอ) ▼▼▼
    @GET("/api/trips/{id}/members")
    fun getTripMembers(@Path("id") tripId: String): Call<GetMembersResponse>

    @POST("/api/trips/{id}/start")
    fun startTrip(@Path("id") tripId: String): Call<StartTripResponse>
}