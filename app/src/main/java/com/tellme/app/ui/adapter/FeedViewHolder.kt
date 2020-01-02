/*
 * Copyright 2020 - André Thiele
 *
 * Fachbereich Informatik und Medien
 * Technische Hochschule Brandenburg
 */

package com.tellme.app.ui.adapter

import androidx.recyclerview.widget.RecyclerView
import com.tellme.app.extensions.convertDateToTimestamp
import com.tellme.app.extensions.convertTimestampToDate
import com.tellme.app.extensions.setUserProfileImageFromPath
import com.tellme.app.model.FeedItem
import com.tellme.databinding.LayoutTellItemFeedBinding

class FeedViewHolder(val binding: LayoutTellItemFeedBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(feedItem: FeedItem, listener: FeedAdapter.FeedClickListener) {
        binding.feedItem = feedItem
        binding.root.setOnClickListener { listener.onFeedClicked(feedItem) }
        binding.textViewDate.text = convertDate(feedItem.replyDate)
        binding.textViewUserUsername.text = feedItem.receiverUsername
        binding.imageViewUserAvatar.setUserProfileImageFromPath(feedItem.receiverPhotoUrl)
        binding.executePendingBindings()
    }

    private fun convertDate(timestamp: String): String {
        val date = timestamp.convertTimestampToDate()
        return date.convertDateToTimestamp("EEE, d MMM yyyy")
    }
}
