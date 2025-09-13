package com.azyabon.weatherapp.dependency_injection

import com.azyabon.weatherapp.storage.SharedPreferencesManager
import org.koin.dsl.module

val storageModule = module {
    single { SharedPreferencesManager(context = get(), gson = get()) }
}