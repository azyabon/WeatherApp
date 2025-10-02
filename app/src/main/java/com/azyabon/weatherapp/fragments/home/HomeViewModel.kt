package com.azyabon.weatherapp.fragments.home

import android.location.Geocoder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azyabon.weatherapp.data.CurrentLocation
import com.azyabon.weatherapp.data.CurrentWeather
import com.azyabon.weatherapp.data.Forecast
import com.azyabon.weatherapp.data.LiveDataEvent
import com.azyabon.weatherapp.network.repository.WeatherDataRepository
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class HomeViewModel(private val weatherDataRepository: WeatherDataRepository) : ViewModel() {

    //region Current Location
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
    //endregion

    //region Weather Data
    private val _weatherData = MutableLiveData<LiveDataEvent<WeatherDataState>>()
    val weatherData: LiveData<LiveDataEvent<WeatherDataState>> get() = _weatherData

    fun getWeatherData(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            emitWeatherDataUIState(isLoading = true)
            weatherDataRepository.getWeatherData(latitude, longitude)?.let { weatherData ->
                emitWeatherDataUIState(currentWeather = CurrentWeather(
                    icon = weatherData.current.condition.icon,
                    temperature = weatherData.current.temperature,
                    wind = weatherData.current.wind,
                    humidity = weatherData.current.humidity,
                    chanceOfRain = weatherData.forecast.forecastDay.first().day.chanceOfRain
                    ),
                    forecast = weatherData.forecast.forecastDay.first().hour.map {
                        Forecast(
                            time = getForecastTime(it.time),
                            temperature = it.temperature,
                            feelsLikeTemperature = it.feelsLikeTemperature,
                            icon = it.condition.icon
                        )
                    }
                )
            } ?: emitWeatherDataUIState(error = "Unable to fetch weather data")
        }
    }

    private fun emitWeatherDataUIState(
        isLoading: Boolean = false,
        currentWeather: CurrentWeather? = null,
        forecast: List<Forecast>? = null,
        error: String? = null,
    ) {
        val weatherDataState = WeatherDataState(isLoading, currentWeather, forecast, error)
        _weatherData.value = LiveDataEvent(weatherDataState)
    }

    data class WeatherDataState(
        val isLoading: Boolean,
        val currentWeather: CurrentWeather?,
        val forecast: List<Forecast>?,
        val error: String?,
    )

    private fun getForecastTime(dateTime: String): String {
        val pattern = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val date = pattern.parse(dateTime) ?: return  dateTime

        return SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
    }
    //endregion


}