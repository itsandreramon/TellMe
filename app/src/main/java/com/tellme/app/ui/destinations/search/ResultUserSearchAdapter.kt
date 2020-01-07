/*
 * Copyright 2020 - André Thiele
 *
 * Fachbereich Informatik und Medien
 * Technische Hochschule Brandenburg
 */

package com.tellme.app.ui.destinations.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tellme.app.model.User
import com.tellme.app.model.UserDiffCallback
import com.tellme.app.util.UserNotFoundException
import com.tellme.databinding.LayoutUserItemSearchResultsBinding
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class ResultUserSearchAdapter(
    private val parent: SearchFragment,
    private val listenerResultSearch: ResultUserSearchClickListener
) : ListAdapter<User, ResultUserSearchViewHolder>(UserDiffCallback), CoroutineScope {

    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultUserSearchViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = LayoutUserItemSearchResultsBinding.inflate(layoutInflater, parent, false)
        return ResultUserSearchViewHolder(binding)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holderResult: ResultUserSearchViewHolder, position: Int) {
        launch {
            try {
                val user = getItem(position)
                holderResult.bind(user, listenerResultSearch)
            } catch (e: UserNotFoundException) {
                e.printStackTrace()
            }
        }
    }

    interface ResultUserSearchClickListener {
        suspend fun onResultUserClicked(user: User)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        job.cancel()
    }
}
