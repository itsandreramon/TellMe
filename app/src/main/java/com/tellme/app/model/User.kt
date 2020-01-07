/*
 * Copyright 2020 - André Thiele
 *
 * Fachbereich Informatik und Medien
 * Technische Hochschule Brandenburg
 */

package com.tellme.app.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.tellme.app.util.Converters
import com.tellme.app.util.DateUtils
import com.tellme.app.util.USER_COLUMN_ABOUT
import com.tellme.app.util.USER_COLUMN_AVATAR
import com.tellme.app.util.USER_COLUMN_EMAIL
import com.tellme.app.util.USER_COLUMN_FOLLOWERS
import com.tellme.app.util.USER_COLUMN_FOLLOWING
import com.tellme.app.util.USER_COLUMN_LATEST_SEARCH_AT
import com.tellme.app.util.USER_COLUMN_NAME
import com.tellme.app.util.USER_COLUMN_UID
import com.tellme.app.util.USER_COLUMN_USERNAME
import com.tellme.app.util.USER_KEY_ABOUT
import com.tellme.app.util.USER_KEY_AVATAR
import com.tellme.app.util.USER_KEY_EMAIL
import com.tellme.app.util.USER_KEY_FOLLOWERS
import com.tellme.app.util.USER_KEY_FOLLOWING
import com.tellme.app.util.USER_KEY_NAME
import com.tellme.app.util.USER_KEY_UID
import com.tellme.app.util.USER_KEY_USERNAME
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "users")
@TypeConverters(Converters::class)
@JsonClass(generateAdapter = true)
data class User(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = USER_COLUMN_UID)
    @Json(name = USER_KEY_UID)
    val uid: String,

    @ColumnInfo(name = USER_COLUMN_NAME)
    @Json(name = USER_KEY_NAME)
    val name: String,

    @ColumnInfo(name = USER_COLUMN_AVATAR)
    @Json(name = USER_KEY_AVATAR)
    val avatar: String? = null,

    @ColumnInfo(name = USER_COLUMN_EMAIL)
    @Json(name = USER_KEY_EMAIL)
    val email: String = "",

    @ColumnInfo(name = USER_COLUMN_USERNAME)
    @Json(name = USER_KEY_USERNAME)
    val username: String,

    @ColumnInfo(name = USER_COLUMN_FOLLOWING)
    @Json(name = USER_KEY_FOLLOWING)
    val following: List<String> = arrayListOf(),

    @ColumnInfo(name = USER_COLUMN_FOLLOWERS)
    @Json(name = USER_KEY_FOLLOWERS)
    val followers: List<String> = emptyList(),

    @ColumnInfo(name = USER_COLUMN_ABOUT)
    @Json(name = USER_KEY_ABOUT)
    val about: String = "",

    @ColumnInfo(name = USER_COLUMN_LATEST_SEARCH_AT)
    val latestSearchAt: String? = null
) : Parcelable, Comparable<User> {

    override fun compareTo(other: User): Int {
        if (this.latestSearchAt != null && other.latestSearchAt != null) {
            val thisLatestSearch = DateUtils.fromString(this.latestSearchAt)
            val otherLatestSearch = DateUtils.fromString(other.latestSearchAt)
            return -thisLatestSearch.compareTo(otherLatestSearch) // descending order
        }

        return 0
    }

    override fun toString(): String {
        return "User(uid='$uid', name='$name', avatar=$avatar, email='$email', username='$username', following=$following, followers=$followers, about='$about', latestSearchAt=$latestSearchAt)"
    }
}

object UserDiffCallback : androidx.recyclerview.widget.DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User) = oldItem.uid == newItem.uid
    override fun areContentsTheSame(oldItem: User, newItem: User) = oldItem == newItem
}
