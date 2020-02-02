/*
 * Copyright 2020 - André Ramon Thiele
 *
 * Department of Computer Science and Media
 * University of Applied Sciences Brandenburg
 */

package com.tellme.app.ui.destinations.replies

import androidx.recyclerview.widget.RecyclerView
import com.tellme.app.extensions.setUserProfileImageFromPath
import com.tellme.app.model.ReplyItem
import com.tellme.app.util.DateUtils
import com.tellme.databinding.ViewHolderItemRepliesBinding
import org.threeten.bp.format.DateTimeParseException

class RepliesItemViewHolder(val binding: ViewHolderItemRepliesBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(replyItem: ReplyItem, listener: RepliesItemViewAdapter.ReplyItemClickListener) {
        binding.replyItem = replyItem
        binding.textViewUserUsername.text = replyItem.receiverUsername
        binding.imageViewUserAvatar.setUserProfileImageFromPath(replyItem.receiverPhotoUrl)
        binding.root.setOnClickListener { listener.onReplyItemClicked(replyItem) }

        binding.textViewDate.text = try {
            DateUtils.convertDate(replyItem.replyDate)
        } catch (e: DateTimeParseException) {
            DateUtils.convertDate(DateUtils.now())
        }

        binding.executePendingBindings()
    }
}
