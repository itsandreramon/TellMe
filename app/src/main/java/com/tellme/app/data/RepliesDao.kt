/*
 * Copyright 2020 - André Ramon Thiele
 *
 * Department of Computer Science and Media
 * University of Applied Sciences Brandenburg
 */

package com.tellme.app.data

import com.tellme.app.model.ReplyItem
import kotlinx.coroutines.flow.Flow

interface RepliesDao {
    fun getRepliesFromCache(): Flow<List<ReplyItem>>
    suspend fun getRepliesByUidRemote(uid: String): Result<List<ReplyItem>>
    suspend fun cacheReplies(replyItems: List<ReplyItem>)
    suspend fun filterRepliesCache(replyItems: List<ReplyItem>)
    suspend fun invalidateRepliesCache()
}
