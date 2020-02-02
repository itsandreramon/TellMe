/*
 * Copyright 2020 - André Ramon Thiele
 *
 * Department of Computer Science and Media
 * University of Applied Sciences Brandenburg
 */

package com.tellme.app.ui.destinations.replies

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.tellme.app.model.ReplyItem
import com.tellme.app.model.ReplyItemDiffCallback
import com.tellme.databinding.ViewHolderItemRepliesBinding

class RepliesItemViewAdapter(
    private val listener: ReplyItemClickListener
) : ListAdapter<ReplyItem, RepliesItemViewHolder>(ReplyItemDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepliesItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ViewHolderItemRepliesBinding.inflate(layoutInflater, parent, false)
        return RepliesItemViewHolder(binding)
    }

    fun getReplyItemAt(position: Int): ReplyItem = getItem(position)

    override fun onBindViewHolder(holderItem: RepliesItemViewHolder, position: Int) {
        holderItem.bind(getItem(position), listener)
    }

    interface ReplyItemClickListener {
        fun onReplyItemClicked(replyItem: ReplyItem)
    }
}
