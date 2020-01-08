/*
 * Copyright 2020 - André Thiele
 *
 * Fachbereich Informatik und Medien
 * Technische Hochschule Brandenburg
 */

package com.tellme.app.ui.destinations.search

import android.content.Context
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.tellme.app.extensions.setUserProfileImageFromPath
import com.tellme.app.model.User
import com.tellme.app.util.ViewUtils
import com.tellme.databinding.ViewHolderItemUserFollowListBinding

class FollowingListViewHolder(
    val viewLifecycleOwner: LifecycleOwner,
    val binding: ViewHolderItemUserFollowListBinding,
    val loggedInUser: LiveData<User>
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(
        user: User,
        listener: FollowingListAdapter.FollowListUserClickListener,
        context: Context
    ) {
        binding.user = user
        binding.imageViewUserAvatar.setUserProfileImageFromPath(user.avatar)
        binding.executePendingBindings()

        itemView.setOnClickListener { listener.onFollowListUserClicked(user, loggedInUser.value!!.uid) }
        binding.buttonFollow.setOnClickListener { listener.onFollowListUserButtonFollowClicked(user) }

        loggedInUser.observe(viewLifecycleOwner, Observer {
            setupFollowButton(it, user, context, listener)
        })
    }

    private fun setupFollowButton(
        loggedInUser: User,
        user: User,
        context: Context,
        listener: FollowingListAdapter.FollowListUserClickListener
    ) {
        val isFollowing = loggedInUser.following.contains(user.uid)

        if (isFollowing) {
            ViewUtils.setFollowButtonToFollowing(binding.buttonFollow, context)
        } else {
            ViewUtils.setFollowButtonToFollow(binding.buttonFollow, context)
        }

        binding.buttonFollow.setOnClickListener {
            listener.onFollowListUserButtonFollowClicked(user)
        }

        if (user.uid == loggedInUser.uid) {
            binding.buttonFollow.visibility = View.INVISIBLE
            binding.textViewUserName.text = "You"
        }
    }
}
