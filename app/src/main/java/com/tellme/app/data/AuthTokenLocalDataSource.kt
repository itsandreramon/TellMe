/*
 * Copyright 2020 - André Ramon Thiele
 *
 * Department of Computer Science and Media
 * University of Applied Sciences Brandenburg
 */

package com.tellme.app.data

import android.content.SharedPreferences
import androidx.core.content.edit
import com.tellme.app.util.API_TOKEN
import javax.inject.Inject

class AuthTokenLocalDataSource @Inject constructor(private val sharedPrefs: SharedPreferences) {

    private var _authToken: String? = sharedPrefs.getString(API_TOKEN, null)

    var authToken: String? = _authToken
        set(value) {
            sharedPrefs.edit { putString(API_TOKEN, value) }
            field = value
        }

    fun clearData() {
        sharedPrefs.edit { API_TOKEN to null }
        authToken = null
    }

    companion object {

        @Volatile private var instance: AuthTokenLocalDataSource? = null

        fun getInstance(
            sharedPreferences: SharedPreferences
        ) = instance ?: synchronized(this) {
            instance ?: AuthTokenLocalDataSource(sharedPreferences)
                .also { instance = it }
        }
    }
}
