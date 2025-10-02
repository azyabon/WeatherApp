package com.azyabon.weatherapp.network.api

import com.azyabon.weatherapp.data.RemoteLocation
import com.azyabon.weatherapp.data.RemoteWeatherData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherAPI {
    companion object{
        const val BASE_URL = "https://api.weatherapi.com/v1/"
        const val API_KEY = "35db481e95214f3bab1195452251409"
    }

    @GET("search.json")
    suspend fun searchLocation(
        @Query("key") key: String = API_KEY,
        @Query("q") query: String,
    ): Response<List<RemoteLocation>>

    @GET("forecast.json")
    suspend fun getWeatherData(
        @Query("key") key: String = API_KEY,
        @Query("q") query: String,
    ): Response<RemoteWeatherData>
}