/*
 * Copyright 2020 - André Thiele
 *
 * Fachbereich Informatik und Medien
 * Technische Hochschule Brandenburg
 */

package com.tellme.app.ui.destinations.inbox

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.tellme.app.model.Tell
import com.tellme.app.model.TellDiffCallback
import com.tellme.databinding.LayoutTellItemInboxBinding

class InboxItemViewAdapter(
    private val listener: TellClickListener
) : ListAdapter<Tell, InboxItemViewHolder>(TellDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InboxItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = LayoutTellItemInboxBinding.inflate(layoutInflater, parent, false)
        return InboxItemViewHolder(binding)
    }

    fun getInboxItemAt(position: Int): Tell = getItem(position)

    override fun onBindViewHolder(holderItem: InboxItemViewHolder, position: Int) {
        holderItem.bind(getItem(position), listener)
    }

    interface TellClickListener {
        fun onTellClicked(tell: Tell)
    }
}
