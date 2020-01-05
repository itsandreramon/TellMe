/*
 * Copyright 2020 - André Thiele
 *
 * Fachbereich Informatik und Medien
 * Technische Hochschule Brandenburg
 */

package com.tellme.app.util

import androidx.lifecycle.LiveData
import com.tellme.app.ui.destinations.search.FollowingFollowersFragmentArgs

interface ArgsHelper {
    fun passArguments(): LiveData<FollowingFollowersFragmentArgs>
}
