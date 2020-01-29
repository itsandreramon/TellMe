/*
 * Copyright 2020 - André Ramon Thiele
 *
 * Department of Computer Science and Media
 * University of Applied Sciences Brandenburg
 */

package com.tellme.app.viewmodels.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tellme.app.data.CoroutinesDispatcherProvider
import com.tellme.app.data.Result
import com.tellme.app.data.TellRepository
import com.tellme.app.model.Tell
import kotlinx.coroutines.async

class TellViewModel(
    private val tellRepository: TellRepository,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel() {

    suspend fun addTell(tell: Tell): Boolean {
        val deferred = viewModelScope.async(dispatcherProvider.network) { tellRepository.insertTellRemote(tell) }

        return when (val result = deferred.await()) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
        }
    }

    suspend fun deleteTell(id: String): Boolean {
        val deferred = viewModelScope.async(dispatcherProvider.network) { tellRepository.deleteTellRemote(id) }

        return when (val result = deferred.await()) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
        }
    }

    suspend fun updateTell(tell: Tell): Boolean {
        val deferred = viewModelScope.async(dispatcherProvider.network) { tellRepository.updateTellRemote(tell) }

        return when (val result = deferred.await()) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
        }
    }

    suspend fun findTellsByReceiverUid(uid: String): List<Tell> {
        val deferred = viewModelScope.async(dispatcherProvider.network) { tellRepository.findTellsByReceiverUid(uid) }

        return when (val result = deferred.await()) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
        }
    }
}
