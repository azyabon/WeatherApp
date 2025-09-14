package com.azyabon.weatherapp.dependency_injection

import com.azyabon.weatherapp.fragments.home.HomeViewModel
import com.azyabon.weatherapp.fragments.location.LocationViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { HomeViewModel(weatherDataRepository = get()) }
    viewModel { LocationViewModel(weatherDataRepository = get())}
}