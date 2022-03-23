package com.example.examer.di

import android.app.Application
import com.example.examer.BuildConfig
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import timber.log.Timber

class ExamerApplication : Application() {
    lateinit var appContainer: AppContainer

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
        appContainer = AppContainer(this)
        Firebase.messaging.token.addOnCompleteListener {
            // get the current registration token
            Timber.d(it.result)
        }
    }


}