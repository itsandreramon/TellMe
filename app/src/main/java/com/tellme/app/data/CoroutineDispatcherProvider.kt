/*
 * Copyright 2020 - André Thiele
 *
 * Fachbereich Informatik und Medien
 * Technische Hochschule Brandenburg
 */

package com.tellme.app.data

import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.rx2.asCoroutineDispatcher

data class CoroutinesDispatcherProvider(
    val main: CoroutineDispatcher,
    val disk: CoroutineDispatcher,
    val network: CoroutineDispatcher,
    val database: CoroutineDispatcher
) {
    @Inject
    constructor() : this(
        main = Dispatchers.Main,
        disk = Dispatchers.IO,
        network = Dispatchers.IO,
        database = Schedulers.single().asCoroutineDispatcher()
    )
}
