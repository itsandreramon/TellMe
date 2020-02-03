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
import com.tellme.app.model.ReplyItem
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RepliesViewModel(
    private val loggedInUserUid: String?,
    private val tellRepository: TellRepository,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel() {

    private val _replies = MutableLiveData<List<ReplyItem>>()
    val replies: LiveData<List<ReplyItem>> = _replies

    init {
        viewModelScope.launch {
            postRepliesFromCache()
        }

        loggedInUserUid?.let { uid ->
            viewModelScope.launch {
                getRepliesFromRemote(uid)
            }
        }
    }

    private suspend fun postRepliesFromCache() {
        tellRepository.getRepliesFromCache()
            .flowOn(dispatcherProvider.database)
            .collect { _replies.postValue(it) }
    }

    fun swipeRefreshReplies(callback: () -> Unit) {
        loggedInUserUid?.let { uid ->
            viewModelScope.launch {
                getRepliesFromRemote(uid)
                callback()
            }
        }
    }

    fun refreshReplies() {
        loggedInUserUid?.let { uid ->
            viewModelScope.launch {
                getRepliesFromRemote(uid)
            }
        }
    }

    suspend fun invalidateRepliesCache() {
        withContext(dispatcherProvider.database) {
            tellRepository.invalidateRepliesCache()
        }
    }

    private suspend fun cacheReplies(replyItems: List<ReplyItem>) {
        withContext(dispatcherProvider.database) {
            tellRepository.cacheReplies(replyItems)
        }
    }

    private suspend fun getRepliesFromRemote(uid: String) {
        val deferred = viewModelScope.async(dispatcherProvider.network) { tellRepository.getRepliesByUidRemote(uid) }

        return when (val result = deferred.await()) {
            is Result.Success -> cacheReplies(result.data)
            is Result.Error -> postRepliesFromCache()
        }
    }
}
