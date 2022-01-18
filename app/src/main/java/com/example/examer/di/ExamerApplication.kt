package com.example.examer.di

import android.app.Application
import com.google.firebase.BuildConfig
import com.google.firebase.FirebaseApp
import timber.log.Timber

class ExamerApplication : Application() {
    lateinit var appContainer: AppContainer

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
        appContainer = AppContainer()
    }
}