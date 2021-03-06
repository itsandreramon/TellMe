/*
 * Copyright 2020 - André Ramon Thiele
 *
 * Department of Computer Science and Media
 * University of Applied Sciences Brandenburg
 */

package com.tellme.app.dagger

import com.tellme.app.App
import com.tellme.app.ui.LoginRegisterActivity
import com.tellme.app.ui.MainActivity
import com.tellme.app.ui.destinations.feed.FeedFragment
import com.tellme.app.ui.destinations.inbox.InboxFragment
import com.tellme.app.ui.destinations.inbox.ReplyTellActivity
import com.tellme.app.ui.destinations.profile.ProfileEditActivity
import com.tellme.app.ui.destinations.profile.ProfileFragment
import com.tellme.app.ui.destinations.register.EmailFragment
import com.tellme.app.ui.destinations.register.PasswordFragment
import com.tellme.app.ui.destinations.register.UsernameFragment
import com.tellme.app.ui.destinations.replies.RepliesFragment
import com.tellme.app.ui.destinations.search.FollowingFollowersFragment
import com.tellme.app.ui.destinations.search.SearchFragment
import com.tellme.app.ui.destinations.search.SendTellActivity
import com.tellme.app.ui.destinations.search.UserFollowersFragment
import com.tellme.app.ui.destinations.search.UserFollowingFragment
import com.tellme.app.ui.destinations.search.UserProfileFragment
import com.tellme.app.ui.destinations.start.StartFragment

/**
 * Injector for MainActivity
 * */
fun inject(activity: MainActivity) {
    App.appComponent(activity.applicationContext)
        .getMainComponentFactory()
        .create(activity)
        .inject(activity)
}

/**
 * Injector for MainActivity
 * */
fun inject(activity: SendTellActivity) {
    App.appComponent(activity.applicationContext)
        .getMainComponentFactory()
        .create(activity)
        .inject(activity)
}

/**
 * Injector for InboxFragment
 * */
fun inject(fragment: InboxFragment) {
    App.appComponent(fragment.requireContext())
        .getMainComponentFactory()
        .create(fragment.requireActivity())
        .inject(fragment)
}

/**
 * Injector for RepliesFragment
 * */
fun inject(fragment: RepliesFragment) {
    App.appComponent(fragment.requireContext())
        .getMainComponentFactory()
        .create(fragment.requireActivity())
        .inject(fragment)
}

/**
 * Injector for AnswerTellActivity
 * */
fun inject(replyTellActivity: ReplyTellActivity) {
    App.appComponent(replyTellActivity.applicationContext)
        .getMainComponentFactory()
        .create(replyTellActivity)
        .inject(replyTellActivity)
}

/**
 * Injector for SearchFragment
 * */
fun inject(fragment: SearchFragment) {
    App.appComponent(fragment.requireContext())
        .getMainComponentFactory()
        .create(fragment.requireActivity())
        .inject(fragment)
}

/**
 * Injector for SearchFragment
 * */
fun inject(fragment: UserProfileFragment) {
    App.appComponent(fragment.requireContext())
        .getMainComponentFactory()
        .create(fragment.requireActivity())
        .inject(fragment)
}

/**
 * Injector for FeedFragment
 * */
fun inject(fragment: FeedFragment) {
    App.appComponent(fragment.requireContext())
        .getMainComponentFactory()
        .create(fragment.requireActivity())
        .inject(fragment)
}

/**
 * Injector for ProfileFragment
 * */
fun inject(fragment: ProfileFragment) {
    App.appComponent(fragment.requireContext())
        .getMainComponentFactory()
        .create(fragment.requireActivity())
        .inject(fragment)
}

/**
 * Injector for ProfileEditActivity
 * */
fun inject(activity: ProfileEditActivity) {
    App.appComponent(activity.applicationContext)
        .getMainComponentFactory()
        .create(activity)
        .inject(activity)
}

/**
 * Injector for LoginRegisterActivity
 * */
fun inject(activity: LoginRegisterActivity) {
    App.appComponent(activity.applicationContext)
        .getAuthComponentFactory()
        .create(activity)
        .inject(activity)
}

/**
 * Injector for StartFragment
 * */
fun inject(fragment: StartFragment) {
    App.appComponent(fragment.requireContext())
        .getAuthComponentFactory()
        .create(fragment.requireActivity())
        .inject(fragment)
}

/**
 * Injector for EmailFragment
 * */
fun inject(fragment: EmailFragment) {
    App.appComponent(fragment.requireContext())
        .getAuthComponentFactory()
        .create(fragment.requireActivity())
        .inject(fragment)
}

/**
 * Injector for UsernameFragment
 * */
fun inject(fragment: UsernameFragment) {
    App.appComponent(fragment.requireContext())
        .getAuthComponentFactory()
        .create(fragment.requireActivity())
        .inject(fragment)
}

/**
 * Injector for PasswordFragment
 * */
fun inject(fragment: PasswordFragment) {
    App.appComponent(fragment.requireContext())
        .getAuthComponentFactory()
        .create(fragment.requireActivity())
        .inject(fragment)
}

/**
 * Injector for FollowsFollowersFragment
 * */
fun inject(followersFragment: FollowingFollowersFragment) {
    App.appComponent(followersFragment.requireContext())
        .getMainComponentFactory()
        .create(followersFragment.requireActivity())
        .inject(followersFragment)
}

/**
 * Injector for FollowsFragment
 * */
fun inject(followersFragment: UserFollowingFragment) {
    App.appComponent(followersFragment.requireContext())
        .getMainComponentFactory()
        .create(followersFragment.requireActivity())
        .inject(followersFragment)
}

/**
 * Injector for FollowersFragment
 * */
fun inject(followersFragment: UserFollowersFragment) {
    App.appComponent(followersFragment.requireContext())
        .getMainComponentFactory()
        .create(followersFragment.requireActivity())
        .inject(followersFragment)
}
