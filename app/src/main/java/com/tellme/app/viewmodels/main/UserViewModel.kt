/*
 * Copyright 2020 - André Ramon Thiele
 *
 * Department of Computer Science and Media
 * University of Applied Sciences Brandenburg
 */

package com.tellme.app.viewmodels.main

import android.content.SharedPreferences
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.tellme.app.data.CoroutinesDispatcherProvider
import com.tellme.app.data.Result
import com.tellme.app.data.UserRepository
import com.tellme.app.model.User
import com.tellme.app.util.UserNotFoundException
import java.io.IOException
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class UserViewModel(
    private val sharedPreferences: SharedPreferences,
    private val userRepository: UserRepository,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel() {

    private val _loggedInUser = MutableLiveData<User>()
    val loggedInUser: LiveData<User> = _loggedInUser

    init {
        viewModelScope.launch {
            postLoggedInUserFromDatabase()
        }

        viewModelScope.launch {
            getCurrentUserFirebase()?.let {
                val user = getUserByUidRemote(it.uid)
                cacheUser(user)
            }
        }
    }

    // TODO Handle errors
    private suspend fun cacheUser(user: User) {
        withContext(dispatcherProvider.database) {
            userRepository.addUserLocal(user)
            Timber.e("Added user to db")
        }
    }

    private suspend fun updateCachedUser(user: User) {
        Timber.e(user.toString())
        withContext(dispatcherProvider.database) { userRepository.updateUserLocal(user) }
    }

    private fun postLoggedInUserFromDatabase() {
        getCurrentUserFirebase()?.let { user ->
            userRepository
                .getUserByUidLocal(user.uid)
                .observeForever {

                    // safe-calling because it can be null when logging out
                    it?.let { _loggedInUser.postValue(it) }
                }
        }
    }

    fun getCurrentUserFirebase(): FirebaseUser? {
        return userRepository.getCurrentUserFirebase()
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

    suspend fun getUserByUidRemote(id: String): User {
        val deferred = viewModelScope.async(dispatcherProvider.network) {
            userRepository.getUserByUidRemote(id)
        }

        return when (val result = deferred.await()) {
            is Result.Success -> {
                val user = result.data
                Timber.e(user.toString())
                cacheUser(user)
                user
            }
            is Result.Error -> throw result.exception
        }
    }

    suspend fun getUserByUsername(username: String): User {
        val deferred = viewModelScope.async(dispatcherProvider.network) {
            userRepository.getUserByUsernameRemote(username)
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

    fun getUserByUidLocal(uid: String): LiveData<User> {
        return userRepository.getUserByUidLocal(uid)
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

    suspend fun updateUser(updatedUser: User): Boolean {
        Timber.d(updatedUser.toString())

        val deferred = viewModelScope.async(dispatcherProvider.network) {
            userRepository.updateUserRemote(updatedUser)
        }

        return when (val result = deferred.await()) {
            is Result.Success -> {
                updateCachedUser(updatedUser)
                true
            }
            is Result.Error -> throw result.exception
        }
    }

    suspend fun followUser(user: User, userToFollow: User): Boolean {
        Timber.d("Unfollowing ${userToFollow.username}")

        val deferred = viewModelScope.async(dispatcherProvider.network) {

            // 1. add to following list at A
            val updatedUserFollowingList = addUidToList(userToFollow.uid, user.following)
            val updatedUser = user.copy(following = updatedUserFollowingList)

            val updateOneSuccess = try {
                updateUser(updatedUser)
            } catch (e: Exception) {
                false
            }

            // 2. add to follower list at B
            val updatedUserToFollowFollowerList = addUidToList(user.uid, userToFollow.followers)
            val updatedUserToFollow = userToFollow.copy(followers = updatedUserToFollowFollowerList)

            val updateTwoSuccess = try {
                updateUser(updatedUserToFollow)
            } catch (e: Exception) {
                false
            }

            if (updateOneSuccess && updateTwoSuccess) {
                return@async Result.Success(true)
            } else {
                // handle rollback
                return@async Result.Error(IOException("Error following user."))
            }
        }

        return when (val result = deferred.await()) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
        }
    }

    suspend fun unfollowUser(user: User, userToUnfollow: User): Boolean {
        Timber.d("Unfollowing ${userToUnfollow.username}")

        val deferred = viewModelScope.async(dispatcherProvider.network) {

            // 1. remove from following list at A
            val updatedUserFollowingList = removeUidFromList(userToUnfollow.uid, user.following)
            val updatedUser = user.copy(following = updatedUserFollowingList)

            val updateOneSuccess = try {
                updateUser(updatedUser)
            } catch (e: Exception) {
                false
            }

            // 2. remove from follower list at B
            val updatedUserToFollowFollowerList = removeUidFromList(user.uid, userToUnfollow.followers)
            val updatedUserToFollow = userToUnfollow.copy(followers = updatedUserToFollowFollowerList)

            val updateTwoSuccess = try {
                updateUser(updatedUserToFollow)
            } catch (e: Exception) {
                false
            }

            if (updateOneSuccess && updateTwoSuccess) {
                return@async Result.Success(true)
            } else {
                // handle rollback
                return@async Result.Error(IOException("Error following user."))
            }
        }

        return when (val result = deferred.await()) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
        }
    }

    private fun addUidToList(uid: String, list: List<String>): List<String> {
        val mutableList = list.toMutableList()
        mutableList.add(uid)
        return mutableList
            .filter { it.isNotEmpty() }
            .distinct()
    }

    private fun removeUidFromList(uid: String, list: List<String>): List<String> {
        val mutableList = list.toMutableList()
        mutableList.remove(uid)
        return mutableList
            .filter { it.isNotEmpty() }
            .distinct()
    }

    // TODO Remove duplicate
    fun logout() {
        // TODO Wipe shared prefs
        // TODO Clear database here
        return userRepository.logout()
    }

    suspend fun invalidateUserCache() {
        withContext(dispatcherProvider.database) {
            userRepository.invalidateUserCache()
        }
    }
}
