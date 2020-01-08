/*
 * Copyright 2020 - André Thiele
 *
 * Fachbereich Informatik und Medien
 * Technische Hochschule Brandenburg
 */

package com.tellme.app.model

import androidx.recyclerview.widget.DiffUtil
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.tellme.app.util.DateUtils
import com.tellme.app.util.FEED_ITEM_COLUMN_ID
import com.tellme.app.util.FEED_ITEM_COLUMN_QUESTION
import com.tellme.app.util.FEED_ITEM_COLUMN_RECEIVER_PHOTO_URL
import com.tellme.app.util.FEED_ITEM_COLUMN_RECEIVER_USERNAME
import com.tellme.app.util.FEED_ITEM_COLUMN_REPLY
import com.tellme.app.util.FEED_ITEM_COLUMN_REPLY_DATE
import com.tellme.app.util.FEED_ITEM_KEY_ID
import com.tellme.app.util.FEED_ITEM_KEY_QUESTION
import com.tellme.app.util.FEED_ITEM_KEY_RECEIVER_PHOTO_URL
import com.tellme.app.util.FEED_ITEM_KEY_RECEIVER_USERNAME
import com.tellme.app.util.FEED_ITEM_KEY_REPLY
import com.tellme.app.util.FEED_ITEM_KEY_REPLY_DATE

@Entity(tableName = "feed_items")
@JsonClass(generateAdapter = true)
data class FeedItem(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = FEED_ITEM_COLUMN_ID)
    @Json(name = FEED_ITEM_KEY_ID)
    val id: String,

    @ColumnInfo(name = FEED_ITEM_COLUMN_RECEIVER_PHOTO_URL)
    @Json(name = FEED_ITEM_KEY_RECEIVER_PHOTO_URL)
    val receiverPhotoUrl: String?,

    @ColumnInfo(name = FEED_ITEM_COLUMN_RECEIVER_USERNAME)
    @Json(name = FEED_ITEM_KEY_RECEIVER_USERNAME)
    val receiverUsername: String,

    @ColumnInfo(name = FEED_ITEM_COLUMN_QUESTION)
    @Json(name = FEED_ITEM_KEY_QUESTION)
    val question: String,

    @ColumnInfo(name = FEED_ITEM_COLUMN_REPLY)
    @Json(name = FEED_ITEM_KEY_REPLY)
    val reply: String,

    @ColumnInfo(name = FEED_ITEM_COLUMN_REPLY_DATE)
    @Json(name = FEED_ITEM_KEY_REPLY_DATE)
    val replyDate: String

) : Comparable<FeedItem> {

    override fun compareTo(other: FeedItem): Int {
        val thisSendDate = DateUtils.fromString(this.replyDate)
        val otherSendDate = DateUtils.fromString(other.replyDate)
        return -thisSendDate.compareTo(otherSendDate) // descending order
    }
}

object FeedDiffCallback : DiffUtil.ItemCallback<FeedItem>() {
    override fun areItemsTheSame(oldItem: FeedItem, newItem: FeedItem) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: FeedItem, newItem: FeedItem) = oldItem == newItem
}
