package com.chrisp.healthdetect.repository

import com.chrisp.healthdetect.model.FraminghamRequest
import com.chrisp.healthdetect.model.FraminghamResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface FraminghamApi {
    @POST("api/framingham")
    suspend fun submitFramingham(@Body request: FraminghamRequest): FraminghamResponse
}

class FraminghamRepository(private val api: FraminghamApi) {
    suspend fun submitFramingham(request: FraminghamRequest): FraminghamResponse {
        return api.submitFramingham(request)
    }
}