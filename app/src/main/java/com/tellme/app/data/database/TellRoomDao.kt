/*
 * Copyright 2020 - André Ramon Thiele
 *
 * Department of Computer Science and Media
 * University of Applied Sciences Brandenburg
 */

package com.tellme.app.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tellme.app.model.FeedItem
import com.tellme.app.model.ReplyItem
import com.tellme.app.model.Tell
import kotlinx.coroutines.flow.Flow

@Dao
interface TellRoomDao {

    @Query("SELECT * FROM inbox_items")
    fun getInbox(): Flow<List<Tell>>

    @Query("SELECT * FROM feed_items")
    fun getFeed(): Flow<List<FeedItem>>

    @Query("SELECT * FROM reply_items")
    fun getReplies(): Flow<List<ReplyItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInbox(inboxItems: List<Tell>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeed(feedItems: List<FeedItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReplies(inboxItems: List<ReplyItem>)

    @Query("DELETE FROM inbox_items WHERE id NOT IN (:inboxItemIds)")
    suspend fun filterInbox(inboxItemIds: List<String>)

    @Query("DELETE FROM feed_items WHERE id NOT IN (:feedItemIds)")
    suspend fun filterFeed(feedItemIds: List<String>)

    @Query("DELETE FROM reply_items WHERE id NOT IN (:replyItemIds)")
    suspend fun filterReplies(replyItemIds: List<String>)

    @Query("DELETE FROM inbox_items")
    suspend fun deleteInbox()

    @Query("DELETE FROM feed_items")
    suspend fun deleteFeed()

    @Query("DELETE FROM reply_items")
    suspend fun deleteReplies()
}
