/*
 * Copyright 2020 - André Ramon Thiele
 *
 * Department of Computer Science and Media
 * University of Applied Sciences Brandenburg
 */

package com.tellme.app.data

import com.tellme.app.data.api.TellService
import com.tellme.app.data.database.TellRoomDao
import com.tellme.app.model.FeedItem
import com.tellme.app.model.ReplyItem
import com.tellme.app.model.Tell
import java.io.IOException
import kotlinx.coroutines.flow.Flow

class TellRepository private constructor(
    private val service: TellService,
    private val roomDao: TellRoomDao
) : TellDao, InboxDao, FeedDao {

    override suspend fun invalidateInboxCache() {
        roomDao.deleteInbox()
    }

    override suspend fun invalidateFeedCache() {
        roomDao.deleteFeed()
    }

    override suspend fun cacheInbox(tells: List<Tell>) {
        roomDao.filterInbox(tells.map { it.id })
        roomDao.insertInbox(tells)
    }

    override suspend fun cacheFeed(feedItems: List<FeedItem>) {
        roomDao.insertFeed(feedItems)
    }

    override fun getInboxFromCache(): Flow<List<Tell>> {
        return roomDao.getInbox()
    }

    override fun getFeedFromCache(): Flow<List<FeedItem>> {
        return roomDao.getFeed()
    }

    override suspend fun filterInboxCache(tells: List<Tell>) {
        roomDao.filterInbox(tells.map { it.id })
    }

    override suspend fun filterFeedCache(feedItems: List<FeedItem>) {
        roomDao.filterFeed(feedItems.map { it.id })
    }

    override suspend fun getFeedByUidRemote(uid: String): Result<List<FeedItem>> {
        return try {
            val response = service.getFeedByUser(uid)
            getResult(response = response, onError = {
                throw IOException("Error getting feed by user: ${response.code()} ${response.message()}")
            })
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getInboxByUidRemote(uid: String): Result<List<Tell>> {
        return try {
            val response = service.getInboxByUser(uid)
            getResult(response = response, onError = {
                throw IOException("Error getting all tells by receiver: ${response.code()} ${response.message()}")
            })
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun insertTellRemote(tell: Tell): Result<Boolean> {
        return try {
            val response = service.addTell(tell)
            getResult(response = response, onError = {
                throw IOException("Error add tell to database: ${response.code()} ${response.message()}")
            })
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deleteTellRemote(id: String): Result<Boolean> {
        return try {
            val response = service.deleteTell(id)
            getResult(response = response, onError = {
                throw IOException("Error deleting tell by id: ${response.code()} ${response.message()}")
            })
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getTellByIdRemote(id: String): Result<Tell> {
        return try {
            val response = service.getTellById(id)
            getResult(response = response, onError = {
                throw IOException("Error getting tell by id: ${response.code()} ${response.message()}")
            })
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateTellRemote(tell: Tell): Result<Boolean> {
        return try {
            val response = service.updateTell(tell)
            getResult(response = response, onError = {
                throw IOException("Error replying to tell by id: ${response.code()} ${response.message()}")
            })
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun findTellsByReceiverUid(uid: String): Result<List<Tell>> {
        return try {
            val response = service.findTellsByReceiverUid(uid)
            getResult(response = response, onError = {
                throw IOException("Error getting to tells by receiver: ${response.code()} ${response.message()}")
            })
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getRepliesByUidRemote(uid: String): Result<List<ReplyItem>> {
        return try {
            val response = service.getRepliesByUidRemote(uid)
            getResult(response = response, onError = {
                throw IOException("Error getting to replies by uid: ${response.code()} ${response.message()}")
            })
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    companion object {

        @Volatile private var instance: TellRepository? = null

        fun getInstance(
            service: TellService,
            dao: TellRoomDao
        ) = instance
            ?: TellRepository(
                service,
                dao
            ).also { instance = it }
    }
}
