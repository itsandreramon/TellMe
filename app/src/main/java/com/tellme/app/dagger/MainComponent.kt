/*
 * Copyright © 2019 - André Thiele
 *
 * Fachbereich Informatik und Medien
 * Technische Hochschule Brandenburg
 */

package com.tellme.app.dagger

import androidx.fragment.app.FragmentActivity
import com.tellme.app.dagger.scope.MainScope
import com.tellme.app.ui.MainActivity
import com.tellme.app.ui.destinations.feed.FeedFragment
import com.tellme.app.ui.destinations.inbox.InboxFragment
import com.tellme.app.ui.destinations.inbox.ReplyTellActivity
import com.tellme.app.ui.destinations.profile.ProfileEditActivity
import com.tellme.app.ui.destinations.profile.ProfileFragment
import com.tellme.app.ui.destinations.search.FollowsFollowersFragment
import com.tellme.app.ui.destinations.search.SearchFragment
import com.tellme.app.ui.destinations.search.SendTellActivity
import com.tellme.app.ui.destinations.search.UserFollowersFragment
import com.tellme.app.ui.destinations.search.UserFollowsFragment
import com.tellme.app.ui.destinations.search.UserProfileFragment
import dagger.BindsInstance
import dagger.Subcomponent

@MainScope
@Subcomponent(modules = [MainModule::class, NetworkModule::class, TranslationModule::class])
interface MainComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance activity: FragmentActivity): MainComponent
    }

    fun inject(target: MainActivity)

    fun inject(target: SendTellActivity)

    fun inject(target: ProfileEditActivity)

    fun inject(target: ReplyTellActivity)

    fun inject(target: InboxFragment)

    fun inject(target: ProfileFragment)

    fun inject(target: SearchFragment)

    fun inject(target: UserProfileFragment)

    fun inject(target: FeedFragment)

    fun inject(target: FollowsFollowersFragment)

    fun inject(target: UserFollowersFragment)

    fun inject(target: UserFollowsFragment)
}
