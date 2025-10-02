package com.azyabon.weatherapp.network.repository

import android.annotation.SuppressLint
import android.location.Geocoder
import com.azyabon.weatherapp.data.CurrentLocation
import com.azyabon.weatherapp.data.RemoteLocation
import com.azyabon.weatherapp.data.RemoteWeatherData
import com.azyabon.weatherapp.network.api.WeatherAPI
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

class WeatherDataRepository(private val weatherApi: WeatherAPI) {

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(
        fusedLocationProviderClient: FusedLocationProviderClient,
        onSuccess: (currentLocation: CurrentLocation) -> Unit,
        onFailure: () -> Unit,
    ) {
        fusedLocationProviderClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            CancellationTokenSource().token,
        ).addOnSuccessListener { location ->
            location ?: onFailure()
            onSuccess(
                CurrentLocation(
                    latitude = location.latitude,
                    longitude = location.longitude,
                )
            )
        }.addOnFailureListener { onFailure() }
    }

    @Suppress("DEPRECATION")
    fun updateAddressText(
        currentLocation: CurrentLocation,
        geocoder: Geocoder,
    ): CurrentLocation {
        val latitude = currentLocation.latitude ?: return currentLocation
        val longitude = currentLocation.longitude ?: return currentLocation

        return geocoder.getFromLocation(latitude, longitude, 1)?.let { addresses ->
            val address = addresses[0]
            val addressText = StringBuilder()
            addressText.append(address.locality).append(", ")
            addressText.append(address.adminArea).append(", ")
            addressText.append(address.countryName).append(", ")

            currentLocation.copy(
                location = addressText.toString(),
            )
        } ?: currentLocation
    }

    suspend fun searchLocation(query: String): List<RemoteLocation>? {
        val response = weatherApi.searchLocation(query = query)

        return if (response.isSuccessful) response.body() else null
    }

    suspend fun getWeatherData(latitude: Double, longitude: Double): RemoteWeatherData? {
        val response = weatherApi.getWeatherData(query = "$latitude,$longitude")

        return if (response.isSuccessful) response.body() else null
    }
}