/*
 * Copyright © 2019 - André Thiele
 *
 * Fachbereich Informatik und Medien
 * Technische Hochschule Brandenburg
 */

package com.tellme.app

import android.app.Application
import android.content.Context
import com.google.firebase.FirebaseApp
import com.jakewharton.threetenabp.AndroidThreeTen
import com.tellme.BuildConfig
import com.tellme.app.dagger.AppComponent
import com.tellme.app.dagger.DaggerAppComponent
import timber.log.Timber

class App : Application() {

    val appComponent: AppComponent by lazy {
        DaggerAppComponent
            .factory()
            .create(applicationContext)
    }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        AndroidThreeTen.init(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    companion object {
        @JvmStatic
        fun appComponent(context: Context): AppComponent {
            return (context.applicationContext as App).appComponent
        }
    }
}
