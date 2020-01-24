/*
 * Copyright 2020 - André Ramon Thiele
 *
 * Department of Computer Science and Media
 * University of Applied Sciences Brandenburg
 */

package com.tellme.app.viewmodels.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tellme.app.data.CoroutinesDispatcherProvider
import com.tellme.app.data.TellRepository
import javax.inject.Inject

class TellViewModelFactory @Inject constructor(
    private val repository: TellRepository,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TellViewModel(
            repository,
            dispatcherProvider
        ) as T
    }
}
