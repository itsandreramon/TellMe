/*
 * Copyright 2020 - André Ramon Thiele
 *
 * Department of Computer Science and Media
 * University of Applied Sciences Brandenburg
 */

package com.tellme.app.model

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.tellme.app.util.DateUtils
import kotlin.Exception
import kotlinx.android.parcel.Parcelize
import timber.log.Timber

@Parcelize
@Entity(tableName = "inbox_items")
@JsonClass(generateAdapter = true)
data class Tell(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    @Json(name = "id")
    val id: String = "-1",

    @ColumnInfo(name = "sender_uid")
    @Json(name = "senderUid")
    val authorUid: String,

    @ColumnInfo(name = "receiver_uid")
    @Json(name = "receiverUid")
    val receiverUid: String,

    @ColumnInfo(name = "question")
    @Json(name = "question")
    val question: String,

    @ColumnInfo(name = "reply")
    @Json(name = "reply")
    val reply: String = "",

    @ColumnInfo(name = "send_date")
    @Json(name = "sendDate")
    val sendDate: String,

    @ColumnInfo(name = "reply_date")
    @Json(name = "replyDate")
    val replyDate: String? = ""

) : Comparable<Tell>, Parcelable {

    override fun compareTo(other: Tell): Int {
        try {
            val thisSendDate = DateUtils.fromString(this.sendDate)
            val otherSendDate = DateUtils.fromString(other.sendDate)
            return -thisSendDate.compareTo(otherSendDate) // descending order
        } catch (e: Exception) {
            Timber.e(e)
        }

        return 0
    }

    override fun toString(): String {
        return "Tell(id='$id', authorUid='$authorUid', receiverUid='$receiverUid', question='$question', reply='$reply', sendDate='$sendDate', replyDate=$replyDate)"
    }
}

object TellDiffCallback : DiffUtil.ItemCallback<Tell>() {
    override fun areItemsTheSame(oldItem: Tell, newItem: Tell) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Tell, newItem: Tell) = oldItem == newItem
}
