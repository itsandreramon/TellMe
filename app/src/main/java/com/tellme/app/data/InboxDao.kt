/*
 * Copyright © 2019 - André Thiele
 *
 * Fachbereich Informatik und Medien
 * Technische Hochschule Brandenburg
 */

package com.tellme.app.data

import com.tellme.app.model.Tell
import kotlinx.coroutines.flow.Flow

interface InboxDao {
    fun getInboxFromCache(): Flow<List<Tell>>
    suspend fun getInboxByUidRemote(uid: String): Result<List<Tell>>
    suspend fun cacheInbox(tells: List<Tell>)
    suspend fun filterInboxCache(tells: List<Tell>)
    suspend fun invalidateInboxCache()
}
