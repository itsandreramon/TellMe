/*
 * Copyright © 2019 - André Thiele
 *
 * Fachbereich Informatik und Medien
 * Technische Hochschule Brandenburg
 */

package com.tellme.app.util

import androidx.room.TypeConverter
import org.threeten.bp.ZonedDateTime

class Converters {
    @TypeConverter
    fun fromString(stringListString: String): List<String> {
        return stringListString.split(",").map { it }
    }

    @TypeConverter
    fun toString(stringList: List<String>): String {
        return stringList.joinToString(separator = ",")
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
