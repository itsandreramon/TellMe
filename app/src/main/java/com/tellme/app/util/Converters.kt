/*
 * Copyright 2020 - André Thiele
 *
 * Fachbereich Informatik und Medien
 * Technische Hochschule Brandenburg
 */

package com.tellme.app.util

import androidx.room.TypeConverter
import org.threeten.bp.ZonedDateTime

class Converters {
    @TypeConverter
    fun fromString(listAsString: String): List<String> {
        return listAsString.split(",").map { it }
    }

    @TypeConverter
    fun toString(list: List<String>): String {
        return list.joinToString(separator = ",")
    }

    @TypeConverter
    fun dateFromString(timestamp: String?): ZonedDateTime? {
        return DateUtils.fromString(timestamp)
    }

    @TypeConverter
    fun dateToString(date: ZonedDateTime): String {
        return DateUtils.toString(date)
    }
}
