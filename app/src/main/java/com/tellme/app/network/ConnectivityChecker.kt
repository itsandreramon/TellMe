/*
 * Copyright 2020 - André Thiele
 *
 * Fachbereich Informatik und Medien
 * Technische Hochschule Brandenburg
 */

package com.tellme.app.network

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import javax.inject.Inject

class ConnectivityChecker @Inject constructor(
    private val connectivityManager: ConnectivityManager
) : LifecycleObserver {

    private val _isConnected = MutableLiveData<Boolean>()
    val isConnected: LiveData<Boolean>
        get() = _isConnected

    init {
        _isConnected.postValue(false)
    }

    private val connectivityCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onLost(network: Network) {
            _isConnected.postValue(false)
        }

        override fun onAvailable(network: Network) {
            _isConnected.postValue(true)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun stopMonitoringConnectivity() {
        connectivityManager.unregisterNetworkCallback(connectivityCallback)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun startMonitoringConnectivity() {
        connectivityManager.registerNetworkCallback(
            NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build(), connectivityCallback
        )
    }
}
