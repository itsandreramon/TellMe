/*
 * Copyright © 2019 - André Thiele
 *
 * Fachbereich Informatik und Medien
 * Technische Hochschule Brandenburg
 */

package com.tellme.app.viewmodels.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tellme.app.dagger.qualifier.LoggedInUserUid
import com.tellme.app.data.CoroutinesDispatcherProvider
import com.tellme.app.data.TellRepository
import javax.inject.Inject

class InboxViewModelFactory @Inject constructor(
    @LoggedInUserUid private val loggedInUserUid: String,
    private val repository: TellRepository,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return InboxViewModel(
            loggedInUserUid,
            repository,
            dispatcherProvider
        ) as T
    }
}
