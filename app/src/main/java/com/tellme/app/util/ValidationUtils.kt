/*
 * Copyright 2020 - André Ramon Thiele
 *
 * Department of Computer Science and Media
 * University of Applied Sciences Brandenburg
 */

package com.tellme.app.util

import android.text.TextUtils

object ValidationUtils {

    fun isValidTellLength(message: String): Boolean {
        val trimmedMessage = message.trim()
        return trimmedMessage.isNotEmpty() && trimmedMessage.length <= 140
    }

    fun isValidName(name: String): Boolean {
        var valid = true

        if (name.length !in 3..25) {
            valid = false
        }

        return valid
    }

    fun isValidUsername(username: String): Boolean {
        var valid = true

        if (username.length !in 3..25) {
            valid = false
        }

        username.forEach { char ->
            if (char !in 'a'..'z' && char !in '1'..'9' && char != '.') {
                valid = false
            }
        }

        return valid
    }

    fun isValidEmail(email: String): Boolean {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidInputPassword(password: String): Boolean {
        return password.length >= 8
    }

    fun isValidConfirmPassword(inputPassword: String, confirmPassword: String): Boolean {
        return isValidInputPassword(inputPassword) && inputPassword == confirmPassword
    }
}
