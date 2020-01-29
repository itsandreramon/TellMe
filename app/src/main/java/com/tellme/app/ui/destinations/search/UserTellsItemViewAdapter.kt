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
import com.tellme.app.model.Tell
import com.tellme.app.model.TellDiffCallback
import com.tellme.databinding.ViewHolderItemUserTellsBinding

class UserTellsItemViewAdapter : ListAdapter<Tell, UserTellsItemViewHolder>(TellDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserTellsItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ViewHolderItemUserTellsBinding.inflate(layoutInflater, parent, false)
        return UserTellsItemViewHolder(binding)
    }

    fun getInboxItemAt(position: Int): Tell = getItem(position)

    override fun onBindViewHolder(holderItem: UserTellsItemViewHolder, position: Int) {
        holderItem.bind(getItem(position))
    }

    interface InboxItemClickListener {
        fun onInboxItemClicked(tell: Tell)
    }
}
