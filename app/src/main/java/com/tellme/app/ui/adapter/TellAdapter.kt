/*
 * Copyright 2020 - André Thiele
 *
 * Fachbereich Informatik und Medien
 * Technische Hochschule Brandenburg
 */

package com.tellme.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.tellme.app.model.Tell
import com.tellme.app.model.TellDiffCallback
import com.tellme.databinding.LayoutTellItemInboxBinding

class TellAdapter(
    private val listener: TellClickListener
) : ListAdapter<Tell, TellViewHolder>(TellDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TellViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = LayoutTellItemInboxBinding.inflate(layoutInflater, parent, false)
        return TellViewHolder(binding)
    }

    fun getTellAt(position: Int): Tell = getItem(position)

    override fun onBindViewHolder(holder: TellViewHolder, position: Int) {
        holder.bind(getItem(position), listener)
    }

    interface TellClickListener {
        fun onTellClicked(tell: Tell)
    }
}
