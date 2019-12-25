/*
 * Copyright © 2019 - André Thiele
 *
 * Fachbereich Informatik und Medien
 * Technische Hochschule Brandenburg
 */

package com.tellme.app.extensions

import com.tellme.app.util.ISO_8601_PATTERN
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun Date.convertDateToTimestamp(pattern: String = ISO_8601_PATTERN): String {
    val df = SimpleDateFormat(pattern, Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    return df.format(this)
}

fun String.convertTimestampToDate(pattern: String = ISO_8601_PATTERN): Date {
    val formatter = SimpleDateFormat(pattern, Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    return formatter.parse(this) ?: Date(0)
}
