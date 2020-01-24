/*
 * Copyright 2020 - André Ramon Thiele
 *
 * Department of Computer Science and Media
 * University of Applied Sciences Brandenburg
 */

package com.tellme.app.extensions

import android.widget.ImageView
import coil.api.load
import coil.transform.CircleCropTransformation
import com.tellme.R

fun ImageView.setUserProfileImageFromPath(path: String?) {
    if (path != null) {
        load(path) {
            crossfade(true)
            placeholder(R.drawable.account_placeholder_light)
            transformations(CircleCropTransformation())
        }
    } else {
        load(R.drawable.account_placeholder_light) {
            crossfade(true)
            transformations(CircleCropTransformation())
        }
    }
}

fun ImageView.setUserProfileImageFromDrawable(resId: Int) {
    load(resId) {
        crossfade(true)
        transformations(CircleCropTransformation())
    }
}
