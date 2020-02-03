/*
 * Copyright 2020 - André Ramon Thiele
 *
 * Department of Computer Science and Media
 * University of Applied Sciences Brandenburg
 */

package com.tellme.app.model

import androidx.recyclerview.widget.DiffUtil
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.tellme.app.util.DateUtils
import timber.log.Timber

@Entity(tableName = "reply_items")
@JsonClass(generateAdapter = true)
data class ReplyItem(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    @Json(name = "id")
    val id: String,

    @ColumnInfo(name = "receiver_avatar")
    @Json(name = "receiverAvatar")
    val receiverPhotoUrl: String?,

    @ColumnInfo(name = "receiver_username")
    @Json(name = "receiverUsername")
    val receiverUsername: String,

    @ColumnInfo(name = "question")
    @Json(name = "question")
    val question: String,

    @ColumnInfo(name = "reply")
    @Json(name = "reply")
    val reply: String,

    @ColumnInfo(name = "reply_date")
    @Json(name = "replyDate")
    val replyDate: String

) : Comparable<ReplyItem> {

    override fun compareTo(other: ReplyItem): Int {
        try {
            val thisReplyDate = DateUtils.fromString(this.replyDate)
            val otherReplyDate = DateUtils.fromString(other.replyDate)
            return -thisReplyDate.compareTo(otherReplyDate) // descending order
        } catch (e: Exception) {
            Timber.e(e)
        }

        return 0
    }
}

object ReplyItemDiffCallback : DiffUtil.ItemCallback<ReplyItem>() {
    override fun areItemsTheSame(oldItem: ReplyItem, newItem: ReplyItem) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: ReplyItem, newItem: ReplyItem) = oldItem == newItem
}
