/*
 * Copyright 2020 - André Thiele
 *
 * Fachbereich Informatik und Medien
 * Technische Hochschule Brandenburg
 */

package com.tellme.app.dagger

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.tellme.app.dagger.qualifier.LoggedInUserUid
import com.tellme.app.data.TellRepository
import com.tellme.app.data.api.TellService
import com.tellme.app.data.database.AppDatabase
import com.tellme.app.viewmodels.main.FeedViewModel
import com.tellme.app.viewmodels.main.FeedViewModelFactory
import com.tellme.app.viewmodels.main.InboxViewModel
import com.tellme.app.viewmodels.main.InboxViewModelFactory
import com.tellme.app.viewmodels.main.SearchViewModel
import com.tellme.app.viewmodels.main.SearchViewModelFactory
import com.tellme.app.viewmodels.main.TellViewModel
import com.tellme.app.viewmodels.main.TellViewModelFactory
import com.tellme.app.viewmodels.main.UserViewModel
import com.tellme.app.viewmodels.main.UserViewModelFactory
import dagger.Module
import dagger.Provides

@Module
abstract class MainModule {

    @Module
    companion object {

        @JvmStatic
        @LoggedInUserUid
        @Provides
        fun provideCurrentUserUid(userViewModel: UserViewModel): String {
            return userViewModel.getCurrentUserFirebase()!!.uid
        }

        @JvmStatic
        @Provides
        fun provideTellRepository(
            tellService: TellService,
            context: Context
        ): TellRepository = TellRepository.getInstance(tellService, AppDatabase.getInstance(context).tellDao())

        @JvmStatic
        @Provides
        fun provideTellViewModel(
            factory: TellViewModelFactory,
            activity: FragmentActivity
        ): TellViewModel = ViewModelProvider(activity, factory).get(TellViewModel::class.java)

        @JvmStatic
        @Provides
        fun provideFeedViewModel(
            factory: FeedViewModelFactory,
            activity: FragmentActivity
        ): FeedViewModel = ViewModelProvider(activity, factory).get(FeedViewModel::class.java)

        @JvmStatic
        @Provides
        fun provideInboxViewModel(
            factory: InboxViewModelFactory,
            activity: FragmentActivity
        ): InboxViewModel = ViewModelProvider(activity, factory).get(InboxViewModel::class.java)

        @JvmStatic
        @Provides
        fun provideUserViewModel(
            factory: UserViewModelFactory,
            activity: FragmentActivity
        ): UserViewModel = ViewModelProvider(activity, factory).get(UserViewModel::class.java)

        @JvmStatic
        @Provides
        fun provideSearchViewModel(
            factory: SearchViewModelFactory,
            activity: FragmentActivity
        ): SearchViewModel = ViewModelProvider(activity, factory).get(SearchViewModel::class.java)
    }
}
