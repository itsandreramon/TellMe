/*
 * Copyright 2020 - André Thiele
 *
 * Fachbereich Informatik und Medien
 * Technische Hochschule Brandenburg
 */

package com.tellme.app.ui.adapter

import androidx.recyclerview.widget.RecyclerView
import com.tellme.app.extensions.setUserProfileImageFromPath
import com.tellme.app.model.User
import com.tellme.databinding.LayoutUserItemSearchResultsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ResultUserSearchViewHolder(
    val binding: LayoutUserItemSearchResultsBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(user: User, listenerResultSearch: ResultUserSearchAdapter.ResultUserSearchClickListener) {
        binding.apply {
            binding.user = user

            binding.imageViewUserAvatar.setUserProfileImageFromPath(user.avatar)
            executePendingBindings()

            itemView.setOnClickListener {
                CoroutineScope(Dispatchers.Main).launch { listenerResultSearch.onResultUserClicked(user) }
            }
        }
    }
}
