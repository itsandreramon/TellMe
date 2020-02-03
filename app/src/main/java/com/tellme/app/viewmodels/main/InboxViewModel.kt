/*
 * Copyright 2020 - André Ramon Thiele
 *
 * Department of Computer Science and Media
 * University of Applied Sciences Brandenburg
 */

package com.tellme.app.viewmodels.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tellme.app.data.CoroutinesDispatcherProvider
import com.tellme.app.data.Result
import com.tellme.app.data.TellRepository
import com.tellme.app.model.Tell
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InboxViewModel(
    private val loggedInUserUid: String?,
    private val tellRepository: TellRepository,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel() {

    private val _inbox = MutableLiveData<List<Tell>>()
    val inbox: LiveData<List<Tell>> = _inbox

    init {
        viewModelScope.launch {
            postInboxFromCache()
        }

        loggedInUserUid?.let { uid ->
            viewModelScope.launch {
                getInboxFromRemote(uid)
            }
        }
    }

    private suspend fun postInboxFromCache() {
        tellRepository.getInboxFromCache()
            .flowOn(dispatcherProvider.database)
            .collect { _inbox.postValue(it) }
    }

    fun swipeRefreshInbox(callback: () -> Unit) {
        loggedInUserUid?.let { uid ->
            viewModelScope.launch {
                getInboxFromRemote(uid)
                callback()
            }
        }
    }

    fun refreshInbox() {
        loggedInUserUid?.let { uid ->
            viewModelScope.launch {
                getInboxFromRemote(uid)
            }
        }
    }

    suspend fun invalidateInboxCache() {
        withContext(dispatcherProvider.database) {
            tellRepository.invalidateInboxCache()
        }
    }

    private suspend fun getInboxFromRemote(uid: String) {
        val deferred = viewModelScope.async(dispatcherProvider.network) { tellRepository.getInboxByUidRemote(uid) }

        return when (val result = deferred.await()) {
            is Result.Success -> tellRepository.cacheInbox(result.data)
            is Result.Error -> postInboxFromCache()
        }
    }
}
