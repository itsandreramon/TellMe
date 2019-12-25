/*
 * Copyright © 2019 - André Thiele
 *
 * Fachbereich Informatik und Medien
 * Technische Hochschule Brandenburg
 */

package com.tellme.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tellme.app.model.FeedDiffCallback
import com.tellme.app.model.FeedItem
import com.tellme.app.ui.destinations.feed.FeedFragment
import com.tellme.databinding.LayoutTellItemFeedBinding
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class FeedAdapter(
    private val parent: FeedFragment,
    private val listener: FeedClickListener
) : ListAdapter<FeedItem, FeedViewHolder>(FeedDiffCallback), CoroutineScope {

    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = LayoutTellItemFeedBinding.inflate(layoutInflater, parent, false)
        return FeedViewHolder(binding)
    }

    fun getFeedAt(position: Int): FeedItem {
        return getItem(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        val feedItem = getItem(position)
        holder.bind(feedItem, listener)
    }

    interface FeedClickListener {
        fun onFeedClicked(feedItem: FeedItem)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        job.cancel()
    }
}
