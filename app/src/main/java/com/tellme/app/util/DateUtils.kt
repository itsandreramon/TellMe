/*
 * Copyright © 2019 - André Thiele
 *
 * Fachbereich Informatik und Medien
 * Technische Hochschule Brandenburg
 */

package com.tellme.app.util

import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.ChronoUnit

object DateUtils {

    /** Uses the JSR-310 Android backport ThreeTenABP
     * https://github.com/JakeWharton/ThreeTenABP
     *
     * @return String */
    fun now(): String {
        return ZonedDateTime
            .now(ZoneId.systemDefault())
            .truncatedTo(ChronoUnit.MILLIS)
            .format(DateTimeFormatter.ISO_ZONED_DATE_TIME)
    }

    fun fromString(timestamp: String?): ZonedDateTime? {
        return ZonedDateTime.parse(timestamp, DateTimeFormatter.ISO_ZONED_DATE_TIME)
            .truncatedTo(ChronoUnit.MILLIS)
    }

    fun toString(date: ZonedDateTime): String {
        return date
            .truncatedTo(ChronoUnit.MILLIS)
            .format(DateTimeFormatter.ISO_ZONED_DATE_TIME)
            .toString()
    }
}
