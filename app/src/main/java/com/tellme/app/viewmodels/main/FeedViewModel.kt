/*
 * Copyright 2020 - André Thiele
 *
 * Fachbereich Informatik und Medien
 * Technische Hochschule Brandenburg
 */

package com.tellme.app.viewmodels.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tellme.app.data.CoroutinesDispatcherProvider
import com.tellme.app.data.Result
import com.tellme.app.data.TellRepository
import com.tellme.app.model.FeedItem
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FeedViewModel(
    private val loggedInUserUid: String?,
    private val tellRepository: TellRepository,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel() {

    private val _feedItems = MutableLiveData<Result<List<FeedItem>>>()
    val feedItems: LiveData<Result<List<FeedItem>>> = _feedItems

    init {
        viewModelScope.launch {
            tellRepository.getFeedFromCache()
                .flowOn(dispatcherProvider.database)
                .collect { _feedItems.postValue(Result.Success(it)) }
        }

        loggedInUserUid?.let { uid ->
            viewModelScope.launch {
                getFeedItemsFromRemote(uid)
            }
        }
    }

    fun swipeRefreshFeed(callback: () -> Unit) {
        loggedInUserUid?.let { uid ->
            viewModelScope.launch {
                getFeedItemsFromRemote(uid)
                callback()
            }
        }
    }

    fun refreshFeed() {
        loggedInUserUid?.let { uid ->
            viewModelScope.launch {
                getFeedItemsFromRemote(uid)
            }
        }
    }

    suspend fun invalidateFeedCache() {
        withContext(dispatcherProvider.database) {
            tellRepository.invalidateFeedCache()
        }
    }

    private suspend fun cacheFeed(feed: List<FeedItem>) {
        withContext(dispatcherProvider.database) {
            tellRepository.cacheFeed(feed)
        }
    }

    private suspend fun getFeedItemsFromRemote(uid: String) {
        val deferred = viewModelScope.async(dispatcherProvider.network) { tellRepository.getFeedByUidRemote(uid) }

        return when (val result = deferred.await()) {
            is Result.Success -> cacheFeed(result.data)
            is Result.Error -> _feedItems.postValue(result)
        }
    }
}
