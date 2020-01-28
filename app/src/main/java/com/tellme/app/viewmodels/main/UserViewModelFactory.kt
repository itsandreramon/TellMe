/*
 * Copyright 2020 - André Ramon Thiele
 *
 * Department of Computer Science and Media
 * University of Applied Sciences Brandenburg
 */

package com.tellme.app.viewmodels.main

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.security.crypto.EncryptedSharedPreferences
import com.tellme.app.data.CoroutinesDispatcherProvider
import com.tellme.app.data.UserRepository
import javax.inject.Inject

class UserViewModelFactory @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val repository: UserRepository,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return UserViewModel(sharedPreferences, repository, dispatcherProvider) as T
    }
}
