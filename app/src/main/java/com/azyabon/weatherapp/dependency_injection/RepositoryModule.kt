package com.azyabon.weatherapp.dependency_injection

import com.azyabon.weatherapp.network.repository.WeatherDataRepository
import org.koin.dsl.module

val repositoryModule = module {
    single { WeatherDataRepository() }
}