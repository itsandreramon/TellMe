package com.tellme.app.network

import android.net.ConnectivityManager
import com.tellme.app.util.NoConnectivityException
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject


class ConnectivityInterceptor @Inject constructor(
    private val connectivityManager: ConnectivityManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!isOnline()) {
            throw NoConnectivityException()
        }

        return chain.proceed(chain.request())
    }

    private fun isOnline(): Boolean {
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}