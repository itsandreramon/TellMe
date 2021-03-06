/*
 * Copyright 2020 - André Ramon Thiele
 *
 * Department of Computer Science and Media
 * University of Applied Sciences Brandenburg
 */

package com.tellme.app.ui.destinations.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tellme.app.model.User
import com.tellme.app.model.UserDiffCallback
import com.tellme.app.util.UserNotFoundException
import com.tellme.databinding.ViewHolderItemUserSearchLatestBinding
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class LatestUserSearchAdapter(
    private val parent: SearchFragment,
    private val listener: LatestUserSearchClickListener
) : ListAdapter<User, LatestUserSearchViewHolder>(UserDiffCallback), CoroutineScope {

    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LatestUserSearchViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ViewHolderItemUserSearchLatestBinding.inflate(layoutInflater, parent, false)
        return LatestUserSearchViewHolder(binding)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: LatestUserSearchViewHolder, position: Int) {
        try {
            val user = getItem(position)
            holder.bind(user, listener)
        } catch (e: UserNotFoundException) {
            e.printStackTrace()
        }
    }

    interface LatestUserSearchClickListener {
        suspend fun onLatestUserClicked(user: User)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        job.cancel()
    }
}
