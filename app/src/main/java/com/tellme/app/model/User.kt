/*
 * Copyright 2020 - André Ramon Thiele
 *
 * Department of Computer Science and Media
 * University of Applied Sciences Brandenburg
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
import kotlinx.android.parcel.Parcelize
import timber.log.Timber

@Parcelize
@Entity(tableName = "users")
@TypeConverters(Converters::class)
@JsonClass(generateAdapter = true)
data class User(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "uid")
    @Json(name = "uid")
    val uid: String,

    @ColumnInfo(name = "name")
    @Json(name = "name")
    val name: String,

    @ColumnInfo(name = "avatar")
    @Json(name = "avatar")
    val avatar: String? = null,

    @ColumnInfo(name = "email")
    @Json(name = "email")
    val email: String = "",

    @ColumnInfo(name = "username")
    @Json(name = "username")
    val username: String,

    @ColumnInfo(name = "following")
    @Json(name = "following")
    val following: List<String> = emptyList(),

    @ColumnInfo(name = "followers")
    @Json(name = "followers")
    val followers: List<String> = emptyList(),

    @ColumnInfo(name = "about")
    @Json(name = "about")
    val about: String = "",

    @ColumnInfo(name = "latest_search_at")
    val latestSearchAt: String? = null

) : Parcelable, Comparable<User> {

    override fun compareTo(other: User): Int {
        if (this.latestSearchAt != null && other.latestSearchAt != null) {
            try {
                val thisLatestSearch = DateUtils.fromString(this.latestSearchAt)
                val otherLatestSearch = DateUtils.fromString(other.latestSearchAt)
                return -thisLatestSearch.compareTo(otherLatestSearch) // descending order
            } catch (e: Exception) {
                Timber.e(e)
            }
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
