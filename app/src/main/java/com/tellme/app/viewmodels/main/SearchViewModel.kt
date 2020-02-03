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
import com.tellme.app.data.UserRepository
import com.tellme.app.model.User
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class SearchViewModel(
    private val userRepository: UserRepository,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel() {

    private val _searchResults = MutableLiveData<List<User>>()
    val searchResults: LiveData<List<User>> = _searchResults

    private var _searchLatest = MutableLiveData<List<User>>()
    val searchLatest: LiveData<List<User>> = _searchLatest

    init {
        viewModelScope.launch {
            userRepository.getLatestUserSearchesLocal()
                .flowOn(dispatcherProvider.database)
                .collect { _searchLatest.postValue(it) }
        }
    }

    suspend fun getAllUsersByQuery(query: String, limit: Int) {
        val deferred = viewModelScope.async(dispatcherProvider.network) {
            userRepository.getUsersByQueryRemote(query, limit)
        }

        return when (val result = deferred.await()) {
            is Result.Success -> _searchResults.postValue(result.data)
            is Result.Error -> _searchResults.postValue(emptyList())
        }
    }

    fun clearLatestUserSearches() {
        viewModelScope.launch(dispatcherProvider.database) {
            userRepository.clearLatestUserSearchesLocal()
        }
    }

    suspend fun cacheLatestUserSearched(user: User) {
        viewModelScope.launch(dispatcherProvider.database) {
            userRepository.addUserLocal(user)
        }
    }
}
