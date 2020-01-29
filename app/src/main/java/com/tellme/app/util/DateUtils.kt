/*
 * Copyright 2020 - André Ramon Thiele
 *
 * Department of Computer Science and Media
 * University of Applied Sciences Brandenburg
 */

package com.tellme.app.util

import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.DateTimeParseException
import org.threeten.bp.temporal.ChronoUnit

object DateUtils {

    /** Uses the JSR-310 Android backport ThreeTenABP
     * https://github.com/JakeWharton/ThreeTenABP
     *
     * @return String */
    fun now(formatter: DateTimeFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME): String {
        return ZonedDateTime
            .now(ZoneId.systemDefault())
            .truncatedTo(ChronoUnit.MILLIS)
            .format(formatter)
    }

    @Throws(DateTimeParseException::class)
    fun fromString(
        timestamp: String?,
        formatter: DateTimeFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME
    ): ZonedDateTime {
        return ZonedDateTime.parse(timestamp, formatter).truncatedTo(ChronoUnit.MILLIS)
    }

    @Throws(DateTimeParseException::class)
    fun toString(date: ZonedDateTime, formatter: DateTimeFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME): String {
        return date.truncatedTo(ChronoUnit.MILLIS)
            .format(formatter)
            .toString()
    }

    @Throws(DateTimeParseException::class)
    fun convertDate(timestamp: String): String? {
        val date = fromString(timestamp)
        return toString(date, DateTimeFormatter.ofPattern("EEE, d MMM yyyy"))
    }
}
