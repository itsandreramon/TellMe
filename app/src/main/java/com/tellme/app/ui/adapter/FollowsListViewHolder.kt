/*
 * Copyright © 2019 - André Thiele
 *
 * Fachbereich Informatik und Medien
 * Technische Hochschule Brandenburg
 */

package com.tellme.app.ui.adapter

import androidx.recyclerview.widget.RecyclerView
import com.tellme.app.extensions.setUserProfileImageFromPath
import com.tellme.app.model.User
import com.tellme.databinding.LayoutUserItemFollowListBinding

class FollowsListViewHolder(
    val binding: LayoutUserItemFollowListBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(user: User, listener: FollowsListAdapter.FollowListUserClickListener) {
        binding.user = user
        binding.imageViewUserAvatar.setUserProfileImageFromPath(user.avatar)
        binding.executePendingBindings()

        itemView.setOnClickListener {
            listener.onFollowListUserClicked(user)
        }

        binding.buttonFollow.setOnClickListener {
            listener.onFollowListUserButtonFollowClicked(user)
        }
    }
}
