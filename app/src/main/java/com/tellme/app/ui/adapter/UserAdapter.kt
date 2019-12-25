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
import com.tellme.app.ui.destinations.search.SearchFragment
import com.tellme.app.util.UserNotFoundException
import com.tellme.databinding.LayoutUserItemSearchResultsBinding
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class UserAdapter(
    private val parent: SearchFragment,
    private val listener: UserClickListener
) : ListAdapter<User, UserViewHolder>(UserDiffCallback), CoroutineScope {

    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = LayoutUserItemSearchResultsBinding.inflate(layoutInflater, parent, false)
        return UserViewHolder(binding)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        launch {
            try {
                val user = getItem(position)
                holder.bind(user, listener)
            } catch (e: UserNotFoundException) {
                e.printStackTrace()
            }
        }
    }

    interface UserClickListener {
        suspend fun onResultUserClicked(user: User)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        job.cancel()
    }
}
