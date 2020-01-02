/*
 * Copyright 2020 - André Thiele
 *
 * Fachbereich Informatik und Medien
 * Technische Hochschule Brandenburg
 */

package com.tellme.app.extensions

fun String.isValidUsername(): Boolean {
    var valid = true

    if (length > 25) {
        valid = false
    }

    if (isEmpty()) {
        valid = false
    }

    forEach { char ->
        if (char !in 'a'..'z' && char !in '1'..'9' && char != '.') valid = false
    }

    return valid
}

fun String.hasValidUsernameLength() = length > 2

fun String.hasValidPasswordLength() = length > 7

fun String.isValidEmailAddress() = android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
