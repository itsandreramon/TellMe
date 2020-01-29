/*
 * Copyright 2020 - André Ramon Thiele
 *
 * Department of Computer Science and Media
 * University of Applied Sciences Brandenburg
 */

package com.tellme.app.ui.destinations.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.tellme.app.model.FeedDiffCallback
import com.tellme.app.model.FeedItem
import com.tellme.databinding.ViewHolderItemFeedBinding

class FeedItemViewAdapter(
    private val parent: FeedFragment,
    private val listener: FeedClickListener
) : ListAdapter<FeedItem, FeedItemViewHolder>(FeedDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ViewHolderItemFeedBinding.inflate(layoutInflater, parent, false)
        return FeedItemViewHolder(binding)
    }

    fun getFeedItemAt(position: Int): FeedItem = getItem(position)

    override fun onBindViewHolder(holderItem: FeedItemViewHolder, position: Int) {
        val feedItem = getItem(position)
        holderItem.bind(feedItem, listener)
    }

    interface FeedClickListener {
        fun onFeedClicked(feedItem: FeedItem)
    }
}
