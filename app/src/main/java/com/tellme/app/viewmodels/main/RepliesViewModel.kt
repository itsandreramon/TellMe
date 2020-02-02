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
import kotlinx.coroutines.launch

class RepliesViewModel(
    private val loggedInUserUid: String?,
    private val tellRepository: TellRepository,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel() {

    private val _replies = MutableLiveData<List<ReplyItem>>()
    val replies: LiveData<List<ReplyItem>> = _replies

    init {
        loggedInUserUid?.let { uid ->
            viewModelScope.launch {
                getRepliesFromRemote(uid)
            }
        }
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

    private suspend fun getRepliesFromRemote(uid: String) {
        val deferred = viewModelScope.async(dispatcherProvider.network) { tellRepository.getRepliesByUidRemote(uid) }

        return when (val result = deferred.await()) {
            is Result.Success -> _replies.postValue(result.data)
            is Result.Error -> _replies.postValue(emptyList())
        }
    }
}
