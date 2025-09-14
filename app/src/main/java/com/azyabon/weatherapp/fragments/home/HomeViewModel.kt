package com.azyabon.weatherapp.fragments.home

import android.location.Geocoder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azyabon.weatherapp.data.CurrentLocation
import com.azyabon.weatherapp.data.LiveDataEvent
import com.azyabon.weatherapp.network.repository.WeatherDataRepository
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.launch

class HomeViewModel(private val weatherDataRepository: WeatherDataRepository) : ViewModel() {

    private val _currentLocation = MutableLiveData<LiveDataEvent<CurrentLocationDataState>>()
    val currentLocation: LiveData<LiveDataEvent<CurrentLocationDataState>> get() = _currentLocation

    fun getCurrentLocation(
        fusedLocationProviderClient: FusedLocationProviderClient,
        geocoder: Geocoder,
    ) {
        viewModelScope.launch {
            emitCurrentLocationUIState(isLoading = true)
            weatherDataRepository.getCurrentLocation(
                fusedLocationProviderClient = fusedLocationProviderClient,
                onSuccess = { currentLocation ->
                    updateAddressText(currentLocation, geocoder)
                },
                onFailure = {
                    emitCurrentLocationUIState(error = "Unable to get current location")
                }
            )
        }
    }

    private fun updateAddressText(currentLocation: CurrentLocation, geocoder: Geocoder) {
        viewModelScope.launch {
            runCatching {
                weatherDataRepository.updateAddressText(currentLocation, geocoder)
            }.onSuccess { location ->
                emitCurrentLocationUIState(currentLocationData = location)
            }.onFailure {
                emitCurrentLocationUIState(
                    currentLocationData = currentLocation.copy(
                        location = "N/A"
                    )
                )
            }
        }
    }

    private fun emitCurrentLocationUIState(
        isLoading: Boolean = false,
        currentLocationData: CurrentLocation? = null,
        error: String? = null,
    ) {
        val currentLocationDataState =
            CurrentLocationDataState(isLoading, currentLocationData, error)
        _currentLocation.value = LiveDataEvent(currentLocationDataState)
    }

    data class CurrentLocationDataState(
        val isLoading: Boolean,
        val currentLocationData: CurrentLocation?,
        val error: String?,
    )
}