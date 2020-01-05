/*
 * Copyright 2020 - André Thiele
 *
 * Fachbereich Informatik und Medien
 * Technische Hochschule Brandenburg
 */

package com.tellme.app.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tellme.app.model.FeedItem
import com.tellme.app.model.Tell
import com.tellme.app.model.User
import com.tellme.app.util.DATABASE_NAME

@Database(entities = [FeedItem::class, Tell::class, User::class], version = 3, exportSchema = false)
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
            return Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
