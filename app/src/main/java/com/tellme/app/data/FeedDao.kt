/*
 * Copyright 2020 - André Thiele
 *
 * Fachbereich Informatik und Medien
 * Technische Hochschule Brandenburg
 */

package com.tellme.app.data

import com.tellme.app.model.FeedItem
import kotlinx.coroutines.flow.Flow

interface FeedDao {
    fun getFeedFromCache(): Flow<List<FeedItem>>
    suspend fun getFeedByUidRemote(uid: String): Result<List<FeedItem>>
    suspend fun cacheFeed(feedItems: List<FeedItem>)
    suspend fun filterFeedCache(feedItems: List<FeedItem>)
    suspend fun invalidateFeedCache()
}
