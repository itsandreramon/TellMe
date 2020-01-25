/*
 * Copyright 2020 - André Ramon Thiele
 *
 * Department of Computer Science and Media
 * University of Applied Sciences Brandenburg
 */

package com.tellme.app.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tellme.app.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserRoomDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("SELECT * FROM users WHERE uid = :uid")
    fun getUserByUid(uid: String): LiveData<User>

    @Query("SELECT * FROM users WHERE latest_search_at IS NOT NULL")
    fun getLatestUserSearches(): Flow<List<User>>

    @Query("DELETE FROM users WHERE latest_search_at IS NOT NULL")
    fun clearLatestUserSearches()

    @Query("DELETE FROM users")
    suspend fun deleteUsers()
}
