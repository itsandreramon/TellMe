/*
 * Copyright © 2019 - André Thiele
 *
 * Fachbereich Informatik und Medien
 * Technische Hochschule Brandenburg
 */

package com.tellme.app.viewmodels.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tellme.app.data.CoroutinesDispatcherProvider
import com.tellme.app.data.UserRepository
import javax.inject.Inject

class SearchViewModelFactory @Inject constructor(
    private val repository: UserRepository,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SearchViewModel(
            repository,
            dispatcherProvider
        ) as T
    }
}
