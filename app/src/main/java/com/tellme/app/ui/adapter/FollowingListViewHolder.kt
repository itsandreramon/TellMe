/*
 * Copyright 2020 - André Thiele
 *
 * Fachbereich Informatik und Medien
 * Technische Hochschule Brandenburg
 */

package com.tellme.app.ui.adapter

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.tellme.app.extensions.setUserProfileImageFromPath
import com.tellme.app.model.User
import com.tellme.app.util.ViewUtils
import com.tellme.databinding.LayoutUserItemFollowListBinding

class FollowingListViewHolder(
    val binding: LayoutUserItemFollowListBinding,
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

        itemView.setOnClickListener { listener.onFollowListUserClicked(user) }
        binding.buttonFollow.setOnClickListener { listener.onFollowListUserButtonFollowClicked(user) }

        loggedInUser.observeForever {
            val isFollowing = it.following.contains(user.uid)

            if (isFollowing) {
                ViewUtils.setFollowButtonToFollowing(binding.buttonFollow, context)
            } else {
                ViewUtils.setFollowButtonToFollow(binding.buttonFollow, context)
            }

            binding.buttonFollow.setOnClickListener {
                listener.onFollowListUserButtonFollowClicked(user)
            }
        }
    }
}
