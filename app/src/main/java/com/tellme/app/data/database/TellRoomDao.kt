/*
 * Copyright © 2019 - André Thiele
 *
 * Fachbereich Informatik und Medien
 * Technische Hochschule Brandenburg
 */

package com.tellme.app.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tellme.app.model.FeedItem
import com.tellme.app.model.Tell
import kotlinx.coroutines.flow.Flow

@Dao
interface TellRoomDao {

    @Query("SELECT * FROM feed_items")
    fun getFeed(): Flow<List<FeedItem>>

    @Query("SELECT * FROM inbox_items")
    fun getInbox(): Flow<List<Tell>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInbox(inbox_items: List<Tell>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeed(feedItems: List<FeedItem>)

    @Query("DELETE FROM inbox_items WHERE id NOT IN (:inboxItemIds)")
    suspend fun filterInbox(inboxItemIds: List<String>)

    @Query("DELETE FROM feed_items WHERE id NOT IN (:feedItemIds)")
    suspend fun filterFeed(feedItemIds: List<String>)

    @Query("DELETE FROM inbox_items")
    suspend fun deleteInbox()

    @Query("DELETE FROM feed_items")
    suspend fun deleteFeed()
}
