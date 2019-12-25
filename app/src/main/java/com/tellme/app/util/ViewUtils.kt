/*
 * Copyright © 2019 - André Thiele
 *
 * Fachbereich Informatik und Medien
 * Technische Hochschule Brandenburg
 */

package com.tellme.app.util

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.tellme.R

object ViewUtils {

    fun showLoadingDialog(context: Context, layout: Int): AlertDialog {
        return createNonCancellableDialog(
            context = context,
            resId = layout
        ).also { it.show() }
    }

    fun showToast(ctx: Context, message: String) {
        Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show()
    }

    fun createNonCancellableDialog(context: Context, resId: Int): AlertDialog {
        return MaterialAlertDialogBuilder(context).apply {
            setCancelable(false)
            setView(resId)
        }.create()
    }

    fun createInfoAlertDialog(
        context: Context,
        message: String = "Message",
        negative: String = "Cancel",
        title: String = "Dialog"
    ): AlertDialog {
        return MaterialAlertDialogBuilder(context).apply {
            setTitle(title)
            setMessage(message)
            setNegativeButton(negative) { _, _ -> }
        }.create()
    }

    fun createInfoAlertDialog(
        context: Context,
        message: String = "Message",
        positive: String = "Ok",
        onPositiveCallback: () -> Unit,
        negative: String = "Cancel",
        title: String = "Dialog"
    ): AlertDialog {
        return MaterialAlertDialogBuilder(context).apply {
            setTitle(title)
            setPositiveButton(positive) { _, _ -> onPositiveCallback() }
            setMessage(message)
            setNegativeButton(negative) { _, _ -> }
        }.create()
    }

    fun createActionInfoAlertDialog(
        context: Context,
        message: String = "Message",
        positive: String = "Cancel",
        title: String = "Dialog",
        onPositiveCallback: () -> Unit
    ): AlertDialog {
        return MaterialAlertDialogBuilder(context).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton(positive) { _, _ -> onPositiveCallback() }
        }.create()
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

    fun showFollowUserNotFoundDialog(context: Context) {
        createInfoAlertDialog(
            context = context,
            message = context.getString(R.string.user_profile_follow_not_found_error),
            title = context.getString(R.string.follow),
            negative = context.getString(R.string.ok)
        ).show()
    }

    fun showFollowErrorDialog(context: Context) {
        createInfoAlertDialog(
            context = context,
            message = context.getString(R.string.error_connection),
            title = context.getString(R.string.follow),
            negative = context.getString(R.string.ok)
        ).show()
    }
}
