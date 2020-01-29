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
import com.tellme.app.util.TELL_COLUMN_AUTHOR_UID
import com.tellme.app.util.TELL_COLUMN_ID
import com.tellme.app.util.TELL_COLUMN_QUESTION
import com.tellme.app.util.TELL_COLUMN_RECEIVER_UID
import com.tellme.app.util.TELL_COLUMN_REPLY
import com.tellme.app.util.TELL_COLUMN_REPLY_DATE
import com.tellme.app.util.TELL_COLUMN_SEND_DATE
import com.tellme.app.util.TELL_KEY_AUTHOR_UID
import com.tellme.app.util.TELL_KEY_ID
import com.tellme.app.util.TELL_KEY_QUESTION
import com.tellme.app.util.TELL_KEY_RECEIVER_UID
import com.tellme.app.util.TELL_KEY_REPLY
import com.tellme.app.util.TELL_KEY_REPLY_DATE
import com.tellme.app.util.TELL_KEY_SEND_DATE
import kotlin.Exception
import kotlinx.android.parcel.Parcelize
import timber.log.Timber

@Parcelize
@Entity(tableName = "inbox_items")
@JsonClass(generateAdapter = true)
data class Tell(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = TELL_COLUMN_ID)
    @Json(name = TELL_KEY_ID)
    val id: String = "-1",

    @ColumnInfo(name = TELL_COLUMN_AUTHOR_UID)
    @Json(name = TELL_KEY_AUTHOR_UID)
    val authorUid: String,

    @ColumnInfo(name = TELL_COLUMN_RECEIVER_UID)
    @Json(name = TELL_KEY_RECEIVER_UID)
    val receiverUid: String,

    @ColumnInfo(name = TELL_COLUMN_QUESTION)
    @Json(name = TELL_KEY_QUESTION)
    val question: String,

    @ColumnInfo(name = TELL_COLUMN_REPLY)
    @Json(name = TELL_KEY_REPLY)
    val reply: String = "",

    @ColumnInfo(name = TELL_COLUMN_SEND_DATE)
    @Json(name = TELL_KEY_SEND_DATE)
    val sendDate: String,

    @ColumnInfo(name = TELL_COLUMN_REPLY_DATE)
    @Json(name = TELL_KEY_REPLY_DATE)
    val replyDate: String? = ""

) : Comparable<Tell>, Parcelable {

    override fun toString(): String {
        return "Tell(id=$id, authorUid='$authorUid', receiverUid='$receiverUid', question='$question', reply=$reply, sendDate='$sendDate')"
    }

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
}

object TellDiffCallback : DiffUtil.ItemCallback<Tell>() {
    override fun areItemsTheSame(oldItem: Tell, newItem: Tell) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Tell, newItem: Tell) = oldItem == newItem
}
