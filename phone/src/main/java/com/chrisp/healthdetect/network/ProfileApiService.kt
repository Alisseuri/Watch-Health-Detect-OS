package com.chrisp.healthdetect.network

import com.chrisp.healthdetect.model.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ProfileApiService {
    // Pastikan path konsisten, misal: "api/users"
    @GET("api/users")
    suspend fun getAllUsers(): List<User>

    @POST("api/framingham")
    suspend fun submitFramingham(@Body request: FraminghamRequest): FraminghamResponse

    @POST("api/nutrition")
    suspend fun submitNutrition(@Body request: NutritionRequest): NutritionResponse

    @GET("api/nutrition/result")
    suspend fun getNutritionResult(@Query("userId") userId: String): NutritionResponse

    @GET("api/users/search")
    suspend fun searchUsers(@Query("query") query: String): SearchResponse
}