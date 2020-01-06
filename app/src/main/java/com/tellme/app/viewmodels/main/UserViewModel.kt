/*
 * Copyright 2020 - André Thiele
 *
 * Fachbereich Informatik und Medien
 * Technische Hochschule Brandenburg
 */

package com.tellme.app.viewmodels.main

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.tellme.app.data.CoroutinesDispatcherProvider
import com.tellme.app.data.Result
import com.tellme.app.data.UserRepository
import com.tellme.app.model.User
import com.tellme.app.util.UserNotFoundException
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class UserViewModel(
    private val userRepository: UserRepository,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel() {

    private val _loggedInUser = MutableLiveData<User>()
    val loggedInUser: LiveData<User> = _loggedInUser

    init {
        viewModelScope.launch {
            getCurrentUserFirebase()?.let {
                postUserByUidFromDatabase(it.uid)
                getUserByUid(it.uid)
            }
        }
    }

    private suspend fun cacheUser(user: User) {
        withContext(dispatcherProvider.database) {
            userRepository.addUserLocal(user)
        }
    }

    private suspend fun updateCachedUser(user: User) {
        Timber.e(user.toString())
        withContext(dispatcherProvider.database) { userRepository.updateUserLocal(user) }
    }

    private fun postUserByUidFromDatabase(uid: String) {
        userRepository
            .getUserByUidLocal(uid)
            .observeForever { user ->
                user?.let { Timber.e(user.toString()) }
                _loggedInUser.postValue(user)
            }
    }

    fun getCurrentUserFirebase(): FirebaseUser? {
        return userRepository.getCurrentUserFirebase()
    }

    suspend fun updateUserProfileFirebase(profile: UserProfileChangeRequest): Boolean {
        val deferred = viewModelScope.async(dispatcherProvider.network) {
            userRepository.updateUserProfileFirebase(profile)
        }

        return when (val result = deferred.await()) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
        }
    }

    suspend fun uploadAvatarFirebase(path: String, userUid: String): Boolean {
        val deferred = viewModelScope.async(dispatcherProvider.network) {
            userRepository.uploadAvatarFirebase(Uri.parse(path), userUid)
        }

        return when (val result = deferred.await()) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
        }
    }

    suspend fun getAvatarFirebase(userUid: String): Uri {
        val deferred = viewModelScope.async(dispatcherProvider.network) {
            userRepository.getAvatarFirebase(userUid)
        }

        return when (val result = deferred.await()) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
        }
    }

    suspend fun getUserByUid(id: String): User {
        val deferred = viewModelScope.async(dispatcherProvider.network) {
            userRepository.getUserByUidRemote(id)
        }

        return when (val result = deferred.await()) {
            is Result.Success -> {
                val user = result.data
                cacheUser(user)
                user
            }
            is Result.Error -> throw result.exception
        }
    }

    suspend fun addUser(user: User): Boolean {
        val deferred = viewModelScope.async(dispatcherProvider.network) {
            userRepository.addUserRemote(user)
        }

        return when (val result = deferred.await()) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
        }
    }

    suspend fun isUsernameAlreadyInUse(username: String): Boolean {
        val deferred = viewModelScope.async(dispatcherProvider.network) {
            userRepository.getUserByUsernameRemote(username)
        }

        return when (val result = deferred.await()) {
            is Result.Success -> true
            is Result.Error -> {
                if (result.exception is UserNotFoundException) false
                else throw result.exception
            }
        }
    }

    suspend fun updateUser(updatedUser: User) {
        Timber.d(updatedUser.toString())

        val deferred = viewModelScope.async(dispatcherProvider.network) {
            userRepository.updateUserRemote(updatedUser)
        }

        return when (val result = deferred.await()) {
            is Result.Success -> updateCachedUser(updatedUser)
            is Result.Error -> throw result.exception
        }
    }

    suspend fun getFollowsByUid(uid: String) : List<User> {
        val deferred = viewModelScope.async(dispatcherProvider.network) {
            userRepository.getFollowsByUid(uid)
        }

        return when (val result = deferred.await()) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
        }
    }

    suspend fun followUserByUid(user: User, uidToFollow: String) {
        val updatedFollowsList = ArrayList(user.following)
            .filter { it != uidToFollow }
            .toMutableList()

        updatedFollowsList.add(uidToFollow)

        val updatedUser = user.copy(following = updatedFollowsList)
        updateUser(updatedUser)
    }

    suspend fun unfollowUserByUid(user: User, uidToFollow: String) {
        val updatedFollowsList = user.following
            .filter { it != uidToFollow }

        val updatedUser = user.copy(following = updatedFollowsList)
        updateUser(updatedUser)
    }

    fun logout() {
        return userRepository.logout()
    }

    suspend fun invalidateUserCache() {
        withContext(dispatcherProvider.database) {
            userRepository.invalidateUserCache()
        }
    }
}
