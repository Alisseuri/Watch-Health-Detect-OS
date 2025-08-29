package com.chrisp.healthdetect.repository

import com.chrisp.healthdetect.model.*
import com.chrisp.healthdetect.network.ProfileApiService

class ProfileRepository(private val api: ProfileApiService) {
    suspend fun submitFramingham(request: FraminghamRequest) = api.submitFramingham(request)
    suspend fun submitAscvd(request: FraminghamRequest) = api.submitAscvd(request)
    suspend fun submitNutrition(request: NutritionRequest) = api.submitNutrition(request)
}