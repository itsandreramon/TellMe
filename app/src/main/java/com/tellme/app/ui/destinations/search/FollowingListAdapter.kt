/*
 * Copyright 2020 - André Ramon Thiele
 *
 * Department of Computer Science and Media
 * University of Applied Sciences Brandenburg
 */

package com.tellme.app.ui.destinations.search

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tellme.app.model.User
import com.tellme.app.model.UserDiffCallback
import com.tellme.app.util.UserNotFoundException
import com.tellme.databinding.ViewHolderItemUserFollowListBinding
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class FollowingListAdapter(
    private val listener: FollowListUserClickListener,
    private val loggedInUser: LiveData<User>,
    private val context: Context,
    private val viewLifecycleOwner: LifecycleOwner
) : ListAdapter<User, FollowingListViewHolder>(UserDiffCallback), CoroutineScope {

    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowingListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ViewHolderItemUserFollowListBinding.inflate(layoutInflater, parent, false)

        return FollowingListViewHolder(
            viewLifecycleOwner = viewLifecycleOwner,
            binding = binding,
            loggedInUser = loggedInUser
        )
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun addItem(element: User) {
        val currentList = mutableListOf<User>().apply { addAll(currentList) }
        currentList.add(element)
        submitList(currentList.distinct())
    }

    override fun onBindViewHolder(holder: FollowingListViewHolder, position: Int) {
        try {
            val user = getItem(position)
            holder.bind(user, listener, context)
        } catch (e: UserNotFoundException) {
            e.printStackTrace()
        }
    }

    interface FollowListUserClickListener : LifecycleOwner {
        fun onFollowListUserClicked(user: User, loggedInUserUid: String)
        fun onFollowListUserButtonFollowClicked(user: User)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        job.cancel()
    }
}
