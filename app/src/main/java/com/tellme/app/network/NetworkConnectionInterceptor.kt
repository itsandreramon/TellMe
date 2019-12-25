/*
 * Copyright © 2019 - André Thiele
 *
 * Fachbereich Informatik und Medien
 * Technische Hochschule Brandenburg
 */

package com.tellme.app.network

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

abstract class NetworkConnectionInterceptor : Interceptor {

    abstract fun isInternetAvailable(): Boolean

    abstract fun onInternetUnavailable()

    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        if (!isInternetAvailable()) {
            onInternetUnavailable()
        }
        return chain.proceed(request)
    }
}
