package com.chrisp.healthdetect.network

import com.chrisp.healthdetect.model.*
import retrofit2.http.Body
import retrofit2.http.POST

interface ProfileApiService {
    @POST("api/framingham")
    suspend fun submitFramingham(@Body request: FraminghamRequest): FraminghamResponse

    @POST("api/ascvd")
    suspend fun submitAscvd(@Body request: FraminghamRequest): FraminghamResponse

    @POST("api/nutrition")
    suspend fun submitNutrition(@Body request: NutritionRequest): NutritionResponse
}