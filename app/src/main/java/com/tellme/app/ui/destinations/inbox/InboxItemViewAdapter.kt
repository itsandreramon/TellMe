/*
 * Copyright 2020 - André Ramon Thiele
 *
 * Department of Computer Science and Media
 * University of Applied Sciences Brandenburg
 */

package com.tellme.app.ui.destinations.inbox

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.tellme.app.model.Tell
import com.tellme.app.model.TellDiffCallback
import com.tellme.databinding.ViewHolderItemInboxBinding

class InboxItemViewAdapter(
    private val listener: InboxItemClickListener
) : ListAdapter<Tell, InboxItemViewHolder>(TellDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InboxItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ViewHolderItemInboxBinding.inflate(layoutInflater, parent, false)
        return InboxItemViewHolder(binding)
    }

    fun getInboxItemAt(position: Int): Tell = getItem(position)

    override fun onBindViewHolder(holderItem: InboxItemViewHolder, position: Int) {
        holderItem.bind(getItem(position), listener)
    }

    interface InboxItemClickListener {
        fun onInboxItemClicked(tell: Tell)
    }
}
