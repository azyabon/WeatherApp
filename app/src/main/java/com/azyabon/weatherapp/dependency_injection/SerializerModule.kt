package com.azyabon.weatherapp.dependency_injection

import com.google.gson.Gson
import org.koin.dsl.module

var serializerModule = module {
    single { Gson() }
}