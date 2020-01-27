/*
 * Copyright 2020 - André Ramon Thiele
 *
 * Department of Computer Science and Media
 * University of Applied Sciences Brandenburg
 */

package com.tellme.app.util

const val BASE_URL = "https://tellme-backend.herokuapp.com/api/v2/"
// const val BASE_URL = "http://10.0.2.2:8080/api/v2/"

const val LOCAL_DATABASE_NAME = "tellMe-db"

const val REPLY_TELL_REQUEST_CODE = 1
const val EDIT_PROFILE_REQUEST_CODE = 2
const val PICK_IMAGE_REQUEST_CODE = 3

const val LOAD_USER_FOLLOWS = 100L
const val LOAD_USER_FOLLOWERS = 101L

const val API_TOKEN = "API_TOKEN"

const val EXTRA_CACHE_KEY = "cacheKey"
const val EXTRA_TELL_KEY = "tellKey"
const val EXTRA_TELL_KEY_UPDATED = "tellKeyUpdated"

const val ISO_8601_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSX"

const val TELL_COLUMN_ID = "id"
const val TELL_COLUMN_AUTHOR_UID = "sender_uid"
const val TELL_COLUMN_RECEIVER_UID = "receiver_uid"
const val TELL_COLUMN_QUESTION = "question"
const val TELL_COLUMN_REPLY = "reply"
const val TELL_COLUMN_SEND_DATE = "send_date"
const val TELL_COLUMN_REPLY_DATE = "reply_date"

const val USER_COLUMN_UID = "uid"
const val USER_COLUMN_NAME = "name"
const val USER_COLUMN_AVATAR = "avatar"
const val USER_COLUMN_EMAIL = "email"
const val USER_COLUMN_USERNAME = "username"
const val USER_COLUMN_FOLLOWING = "following"
const val USER_COLUMN_FOLLOWERS = "followers"
const val USER_COLUMN_ABOUT = "about"
const val USER_COLUMN_LATEST_SEARCH_AT = "latest_search_at"

const val FEED_ITEM_COLUMN_ID = "id"
const val FEED_ITEM_COLUMN_RECEIVER_USERNAME = "receiver_username"
const val FEED_ITEM_COLUMN_RECEIVER_PHOTO_URL = "receiver_avatar"
const val FEED_ITEM_COLUMN_EMAIL = "receiver_avatar"
const val FEED_ITEM_COLUMN_QUESTION = "question"
const val FEED_ITEM_COLUMN_REPLY = "reply"
const val FEED_ITEM_COLUMN_REPLY_DATE = "reply_date"

const val TELL_KEY_ID = "id"
const val TELL_KEY_AUTHOR_UID = "senderUid"
const val TELL_KEY_RECEIVER_UID = "receiverUid"
const val TELL_KEY_QUESTION = "question"
const val TELL_KEY_REPLY = "reply"
const val TELL_KEY_SEND_DATE = "sendDate"
const val TELL_KEY_REPLY_DATE = "replyDate"

const val USER_KEY_UID = "uid"
const val USER_KEY_NAME = "name"
const val USER_KEY_AVATAR = "avatar"
const val USER_KEY_EMAIL = "email"
const val USER_KEY_USERNAME = "username"
const val USER_KEY_FOLLOWING = "following"
const val USER_KEY_FOLLOWERS = "followers"
const val USER_KEY_ABOUT = "about"

const val FEED_ITEM_KEY_ID = "id"
const val FEED_ITEM_KEY_RECEIVER_USERNAME = "receiverUsername"
const val FEED_ITEM_KEY_RECEIVER_PHOTO_URL = "receiverAvatar"
const val FEED_ITEM_KEY_QUESTION = "question"
const val FEED_ITEM_KEY_REPLY = "reply"
const val FEED_ITEM_KEY_REPLY_DATE = "replyDate"
