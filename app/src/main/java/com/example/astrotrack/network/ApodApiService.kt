package com.example.astrotrack.network

import com.example.astrotrack.model.ApodItem
import retrofit2.http.GET
import retrofit2.http.Query

interface ApodApiService {
    @GET("planetary/apod")
    suspend fun getApodData(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("api_key") apiKey: String = "cMqg3msBFaDSXWYmTddcmUMAV8diW6ExrUS38alb"
    ): List<ApodItem>
}
