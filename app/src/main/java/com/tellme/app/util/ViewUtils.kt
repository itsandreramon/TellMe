/*
 * Copyright 2020 - André Ramon Thiele
 *
 * Department of Computer Science and Media
 * University of Applied Sciences Brandenburg
 */

package com.tellme.app.util

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.tellme.R

object ViewUtils {

    fun showToast(ctx: Context, message: String) {
        Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show()
    }

    fun createSnackbar(ctx: Context, layout: View, msg: String): Snackbar {
        return Snackbar.make(layout, msg, 5000)
            .setBackgroundTint(ContextCompat.getColor(ctx, R.color.colorAccent))
            .setTextColor(ContextCompat.getColor(ctx, R.color.white))
    }

    fun createActionSnackBar(
        ctx: Context,
        layout: View,
        msg: String,
        actionTitle: String,
        action: () -> Unit
    ): Snackbar {
        return Snackbar.make(layout, msg, 5000)
            .setBackgroundTint(ContextCompat.getColor(ctx, R.color.colorAccent))
            .setTextColor(ContextCompat.getColor(ctx, R.color.white))
            .setAction(actionTitle) { action() }
            .setActionTextColor(ContextCompat.getColor(ctx, R.color.white))
    }

    fun toggleInputLayoutWarning(
        textInputLayout: TextInputLayout,
        textInputEditText: TextInputEditText,
        message: String,
        enabled: Boolean
    ) = when (enabled) {
        true -> showInputLayoutWarning(textInputLayout, textInputEditText, message)
        false -> hideInputLayoutWarning(textInputLayout)
    }

    fun showInputLayoutWarning(
        textInputLayout: TextInputLayout,
        textInputEditText: TextInputEditText,
        message: String
    ) {
        if (textInputEditText.text.toString().trim().isNotEmpty()) {
            textInputLayout.error = message
            textInputLayout.isErrorEnabled = true
        }
    }

    fun hideInputLayoutWarning(textInputLayout: TextInputLayout) {
        textInputLayout.isErrorEnabled = false
    }

    fun setFollowButtonToFollow(button: MaterialButton, context: Context) {
        button.apply {
            icon = ContextCompat.getDrawable(context, R.drawable.ic_person_add)
            iconTint = ContextCompat.getColorStateList(context, R.color.dark_grey)
            setTextColor(ContextCompat.getColor(context, R.color.dark_grey))
            text = context.getString(R.string.follow)
        }
    }

    fun setFollowButtonToFollowing(button: MaterialButton, context: Context) {
        button.apply {
            icon = ContextCompat.getDrawable(context, R.drawable.ic_person_check)
            iconTint = ContextCompat.getColorStateList(context, R.color.colorAccent)
            setTextColor(ContextCompat.getColor(context, R.color.colorAccent))
            text = context.getString(R.string.following)
        }
    }
}
