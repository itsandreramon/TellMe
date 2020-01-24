/*
 * Copyright 2020 - André Ramon Thiele
 *
 * Department of Computer Science and Media
 * University of Applied Sciences Brandenburg
 */

package com.tellme.app.ui.destinations.feed

import androidx.recyclerview.widget.RecyclerView
import com.tellme.app.extensions.setUserProfileImageFromPath
import com.tellme.app.model.FeedItem
import com.tellme.app.util.DateUtils
import com.tellme.databinding.ViewHolderItemFeedBinding

class FeedItemViewHolder(val binding: ViewHolderItemFeedBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(feedItem: FeedItem, listener: FeedItemViewAdapter.FeedClickListener) {
        binding.feedItem = feedItem
        binding.root.setOnClickListener { listener.onFeedClicked(feedItem) }
        binding.textViewDate.text = DateUtils.convertDate(feedItem.replyDate)
        binding.textViewUserUsername.text = feedItem.receiverUsername
        binding.imageViewUserAvatar.setUserProfileImageFromPath(feedItem.receiverPhotoUrl)
        binding.executePendingBindings()
    }
}
