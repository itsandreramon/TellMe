/*
 * Copyright 2020 - André Thiele
 *
 * Fachbereich Informatik und Medien
 * Technische Hochschule Brandenburg
 */

package com.tellme.app.dagger

import android.content.Context
import android.net.ConnectivityManager
import com.tellme.BuildConfig
import com.tellme.app.data.api.TellService
import com.tellme.app.data.api.UserService
import com.tellme.app.network.ConnectivityInterceptor
import com.tellme.app.util.BASE_URL
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber

@Module
abstract class NetworkModule {

    @Module
    companion object {

        @JvmStatic
        @Provides
        fun provideConnectivityInterceptor(connectivityManager: ConnectivityManager) : ConnectivityInterceptor {
            return ConnectivityInterceptor(connectivityManager)
        }

        @JvmStatic
        @Provides
        fun providesConnectivityManager(context: Context): ConnectivityManager =
            context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE)
                    as ConnectivityManager

        @JvmStatic
        @Provides
        fun provideLoggingInterceptor(): HttpLoggingInterceptor {
            return HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
                override fun log(message: String) {
                    Timber.d(message)
                }
            }).apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            }
        }

        @JvmStatic
        @Provides
        fun provideOkHttpClient(
            loggingInterceptor: HttpLoggingInterceptor,
            connectivityInterceptor: ConnectivityInterceptor
        ): OkHttpClient {
            return OkHttpClient.Builder()
                .addNetworkInterceptor(connectivityInterceptor)
                .addNetworkInterceptor(loggingInterceptor)
                .build()
        }

        @JvmStatic
        @Provides
        fun provideUserService(okHttpClient: OkHttpClient): UserService {
            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(UserService::class.java)
        }

        @JvmStatic
        @Provides
        fun provideTellService(okHttpClient: OkHttpClient): TellService {
            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(TellService::class.java)
        }
    }
}
