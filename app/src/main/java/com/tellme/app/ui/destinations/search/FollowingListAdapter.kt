/*
 * Copyright 2020 - André Thiele
 *
 * Fachbereich Informatik und Medien
 * Technische Hochschule Brandenburg
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
import com.tellme.databinding.LayoutUserItemFollowListBinding
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

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
        val binding = LayoutUserItemFollowListBinding.inflate(layoutInflater, parent, false)
        return FollowingListViewHolder(
            viewLifecycleOwner,
            binding,
            loggedInUser
        )
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: FollowingListViewHolder, position: Int) {
        launch {
            try {
                val user = getItem(position)
                holder.bind(user, listener, context)
            } catch (e: UserNotFoundException) {
                e.printStackTrace()
            }
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
