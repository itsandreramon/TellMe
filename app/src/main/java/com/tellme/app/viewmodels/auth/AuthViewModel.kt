/*
 * Copyright 2020 - André Ramon Thiele
 *
 * Department of Computer Science and Media
 * University of Applied Sciences Brandenburg
 */

package com.tellme.app.viewmodels.auth

import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.security.crypto.EncryptedSharedPreferences
import com.google.firebase.auth.FirebaseUser
import com.tellme.app.data.CoroutinesDispatcherProvider
import com.tellme.app.data.Result
import com.tellme.app.data.UserRepository
import com.tellme.app.model.User
import com.tellme.app.util.API_TOKEN
import com.tellme.app.util.UserNotFoundException
import kotlinx.coroutines.async

class AuthViewModel(
    private val sharedPreferences: EncryptedSharedPreferences,
    private val userRepository: UserRepository,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel() {

    suspend fun register(email: String, password: String): String {
        val deferred = viewModelScope.async(dispatcherProvider.network) { userRepository.register(email, password) }

        return when (val result = deferred.await()) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
        }
    }

    suspend fun login(email: String, password: String): Boolean {
        val deferred = viewModelScope.async(dispatcherProvider.network) { userRepository.login(email, password) }

        return when (val result = deferred.await()) {
            is Result.Success -> {
                // user is logged in now
                // retrieve token to authenticate requests
                // retrieveIdToken()
                result.data
            }
            is Result.Error -> throw result.exception
        }
    }

    fun getCurrentUserFirebase(): FirebaseUser? {
        return userRepository.getCurrentUserFirebase()
    }

    suspend fun sendEmailVerification(user: FirebaseUser, language: String): Boolean {
        val deferred =
            viewModelScope.async(dispatcherProvider.network) {
                userRepository.sendEmailVerificationFirebase(
                    user,
                    language
                )
            }

        return when (val result = deferred.await()) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
        }
    }

    suspend fun addUserToDatabase(user: User): Boolean {
        val deferred = viewModelScope.async(dispatcherProvider.network) { userRepository.addUserRemote(user) }

        return when (val result = deferred.await()) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
        }
    }

    suspend fun getUserByUsername(username: String): User {
        val deferred =
            viewModelScope.async(dispatcherProvider.network) { userRepository.getUserByUsernameRemote(username) }

        return when (val result = deferred.await()) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
        }
    }

    suspend fun isUsernameAlreadyInUse(username: String): Boolean {
        val deferred =
            viewModelScope.async(dispatcherProvider.network) { userRepository.getUserByUsernameRemote(username) }

        return when (val result = deferred.await()) {
            is Result.Success -> true
            is Result.Error -> {
                if (result.exception is UserNotFoundException) false
                else throw result.exception
            }
        }
    }

    suspend fun isEmailAlreadyInUse(email: String): Boolean {
        val deferred =
            viewModelScope.async(dispatcherProvider.network) { userRepository.isEmailAlreadyInUseFirebase(email) }

        return when (val result = deferred.await()) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
        }
    }

    suspend fun retrieveIdToken() {
        getCurrentUserFirebase()?.let { firebaseUser ->
            when (val result = userRepository.retrieveIdToken(firebaseUser)) {
                is Result.Success -> sharedPreferences.edit { putString(API_TOKEN, result.data) }
                is Result.Error -> {
                    // TODO Handle error
                }
            }
        }
    }

    fun logout() {
        // TODO Wipe shared prefs
        // TODO Clear database here
        return userRepository.logout()
    }

    suspend fun deleteUserByUid(uid: String): Boolean {
        val deferred = viewModelScope.async(dispatcherProvider.network) { userRepository.deleteUserByUidRemote(uid) }

        return when (val result = deferred.await()) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
        }
    }
}
