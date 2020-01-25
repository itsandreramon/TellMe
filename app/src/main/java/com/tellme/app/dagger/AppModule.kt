/*
 * Copyright 2020 - André Ramon Thiele
 *
 * Department of Computer Science and Media
 * University of Applied Sciences Brandenburg
 */

package com.tellme.app.dagger

import android.content.Context
import com.tellme.app.data.FirebaseSource
import com.tellme.app.data.UserRepository
import com.tellme.app.data.api.UserService
import com.tellme.app.data.database.AppDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
abstract class AppModule {

    @Module
    companion object {

        @JvmStatic
        @Provides
        @Singleton
        fun provideFirebaseSource(): FirebaseSource = FirebaseSource()

        @JvmStatic
        @Provides
        fun provideUserRepository(
            firebaseSource: FirebaseSource,
            userService: UserService,
            context: Context
        ): UserRepository {
            return UserRepository.getInstance(firebaseSource, userService, AppDatabase.getInstance(context).userDao())
        }
    }
}
