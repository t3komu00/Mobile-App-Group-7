
package com.example.astrotrack.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val api: ApodApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.nasa.gov/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApodApiService::class.java)
    }
}
