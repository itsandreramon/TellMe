/*
 * Copyright © 2019 - André Thiele
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
import com.tellme.app.network.ConnectivityChecker
import com.tellme.app.network.NetworkConnectionInterceptor
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
        fun provideNetworkConnectionInterceptor(
            connectivityChecker: ConnectivityChecker
        ): NetworkConnectionInterceptor {
            return object : NetworkConnectionInterceptor() {
                override fun isInternetAvailable(): Boolean {
                    return connectivityChecker.isConnected.value!!
                }

                override fun onInternetUnavailable() {
                    // TODO
                }
            }
        }

        @JvmStatic
        @Provides
        fun provideOkHttpClient(
            loggingInterceptor: HttpLoggingInterceptor,
            networkConnectionInterceptor: NetworkConnectionInterceptor
        ): OkHttpClient {
            return OkHttpClient.Builder()
                .addInterceptor(networkConnectionInterceptor)
                .addNetworkInterceptor(loggingInterceptor)
                .build()
        }

        @JvmStatic
        @Provides
        fun provideUserService(okHttpClient: OkHttpClient): UserService {
            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create().asLenient())
                .build()
                .create(UserService::class.java)
        }

        @JvmStatic
        @Provides
        fun provideTellService(okHttpClient: OkHttpClient): TellService {
            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create().asLenient())
                .build()
                .create(TellService::class.java)
        }
    }
}
