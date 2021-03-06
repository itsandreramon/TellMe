/*
 * Copyright 2020 - André Ramon Thiele
 *
 * Department of Computer Science and Media
 * University of Applied Sciences Brandenburg
 */

package com.tellme.app.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tellme.app.model.FeedItem
import com.tellme.app.model.ReplyItem
import com.tellme.app.model.Tell
import com.tellme.app.model.User
import com.tellme.app.util.LOCAL_DATABASE_NAME

@Database(entities = [ReplyItem::class, FeedItem::class, Tell::class, User::class], version = 7, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun tellDao(): TellRoomDao
    abstract fun userDao(): UserRoomDao

    companion object {

        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance
                    ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, LOCAL_DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
