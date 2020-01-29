/*
 * Copyright 2020 - André Ramon Thiele
 *
 * Department of Computer Science and Media
 * University of Applied Sciences Brandenburg
 */

package com.tellme.app.ui.destinations.search

import androidx.recyclerview.widget.RecyclerView
import com.tellme.app.model.Tell
import com.tellme.app.util.DateUtils
import com.tellme.databinding.ViewHolderItemUserTellsBinding
import org.threeten.bp.format.DateTimeParseException

class UserTellsItemViewHolder(val binding: ViewHolderItemUserTellsBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(tell: Tell) {

        binding.tell = tell

        binding.textViewDate.text = try {
            DateUtils.convertDate(tell.sendDate)
        } catch (e: DateTimeParseException) {
            DateUtils.convertDate(DateUtils.now())
        }

        binding.executePendingBindings()
    }
}
