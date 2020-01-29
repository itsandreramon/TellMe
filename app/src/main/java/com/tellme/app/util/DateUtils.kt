/*
 * Copyright 2020 - André Ramon Thiele
 *
 * Department of Computer Science and Media
 * University of Applied Sciences Brandenburg
 */

package com.tellme.app.util

import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.DateTimeParseException
import org.threeten.bp.temporal.ChronoUnit

object DateUtils {

    /** Uses the JSR-310 Android backport ThreeTenABP
     * https://github.com/JakeWharton/ThreeTenABP
     *
     * @return
     * */
    fun now(): String {
        return Instant.now()
            .truncatedTo(ChronoUnit.SECONDS)
            .toString()
    }

    @Throws(DateTimeParseException::class)
    fun fromString(
        timestamp: String?
    ): ZonedDateTime {
        val utc = Instant.parse(timestamp)
        return ZonedDateTime.ofInstant(utc, ZoneId.systemDefault())
    }

    @Throws(DateTimeParseException::class)
    fun toString(date: ZonedDateTime, formatter: DateTimeFormatter = DateTimeFormatter.ISO_INSTANT): String {
        return date.truncatedTo(ChronoUnit.MILLIS)
            .format(formatter)
            .toString()
    }

    @Throws(DateTimeParseException::class)
    fun convertDate(timestamp: String): String? {
        val zonedDateTime = fromString(timestamp)
        return toString(zonedDateTime, DateTimeFormatter.ofPattern("EEE, d MMM yyyy"))
    }
}
