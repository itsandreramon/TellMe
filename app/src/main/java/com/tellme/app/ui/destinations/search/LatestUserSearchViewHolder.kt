/*
 * Copyright 2020 - André Ramon Thiele
 *
 * Department of Computer Science and Media
 * University of Applied Sciences Brandenburg
 */

package com.tellme.app.ui.destinations.search

import androidx.recyclerview.widget.RecyclerView
import com.tellme.app.extensions.setUserProfileImageFromPath
import com.tellme.app.model.User
import com.tellme.databinding.ViewHolderItemUserSearchLatestBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LatestUserSearchViewHolder(
    val binding: ViewHolderItemUserSearchLatestBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(user: User, listener: LatestUserSearchAdapter.LatestUserSearchClickListener) {
        binding.apply {
            binding.user = user
            binding.imageViewUserAvatar.transitionName = user.uid

            binding.imageViewUserAvatar.setUserProfileImageFromPath(user.avatar)
            executePendingBindings()

            itemView.setOnClickListener {
                CoroutineScope(Dispatchers.Main).launch { listener.onLatestUserClicked(user) }
            }
        }
    }
}
