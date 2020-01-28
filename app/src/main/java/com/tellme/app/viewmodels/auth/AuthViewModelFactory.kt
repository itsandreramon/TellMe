/*
 * Copyright 2020 - André Ramon Thiele
 *
 * Department of Computer Science and Media
 * University of Applied Sciences Brandenburg
 */

package com.tellme.app.viewmodels.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tellme.app.data.CoroutinesDispatcherProvider
import com.tellme.app.data.UserRepository
import javax.inject.Inject

class AuthViewModelFactory @Inject constructor(
    private val repository: UserRepository,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AuthViewModel(repository, dispatcherProvider) as T
    }
}
