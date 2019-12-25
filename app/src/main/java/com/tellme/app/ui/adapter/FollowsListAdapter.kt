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
import com.tellme.app.model.User
import com.tellme.app.model.UserDiffCallback
import com.tellme.app.util.UserNotFoundException
import com.tellme.databinding.LayoutUserItemFollowListBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class FollowsListAdapter(
    private val listener: FollowListUserClickListener
) : ListAdapter<User, FollowsListViewHolder>(UserDiffCallback), CoroutineScope {

    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowsListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = LayoutUserItemFollowListBinding.inflate(layoutInflater, parent, false)
        return FollowsListViewHolder(binding)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: FollowsListViewHolder, position: Int) {
        launch {
            try {
                val user = getItem(position)
                holder.bind(user, listener)
            } catch (e: UserNotFoundException) {
                e.printStackTrace()
            }
        }
    }

    interface FollowListUserClickListener {
        fun onFollowListUserClicked(user: User)
        fun onFollowListUserButtonFollowClicked(user: User)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        job.cancel()
    }
}
