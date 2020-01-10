/*
 * Copyright 2020 - André Thiele
 *
 * Fachbereich Informatik und Medien
 * Technische Hochschule Brandenburg
 */

package com.tellme.app.util

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tellme.R

object DialogUtils {

    fun createClearSearchResultsDialog(
        context: Context,
        onPositiveCallback: () -> Unit
    ): AlertDialog {
        return createInfoAlertDialog(
            context = context,
            message = context.getString(R.string.search_clear_latest),
            negative = context.getString(R.string.cancel),
            positive = context.getString(R.string.clear),
            title = context.getString(R.string.search_clear_latest_title),
            onPositiveCallback = { onPositiveCallback() }
        )
    }

    fun createRegisterSuccessDialog(
        context: Context,
        onPositiveCallback: () -> Unit
    ): AlertDialog {
        return createInfoAlertDialog(
            context = context,
            message = context.getString(R.string.register_success),
            title = context.getString(R.string.register),
            positive = context.getString(R.string.ok),
            onPositiveCallback = { onPositiveCallback() }
        )
    }

    fun createEmailAlreadyInUseDialog(context: Context): AlertDialog {
        return createInfoAlertDialog(
            context = context,
            message = context.getString(R.string.register_error_email_in_use)
        )
    }

    fun createUsernameAlreadyInUseDialog(context: Context): AlertDialog {
        return createInfoAlertDialog(
            context = context,
            message = context.getString(R.string.register_error_username_in_use)
        )
    }

    fun createUpdateErrorDialog(context: Context): AlertDialog {
        return createInfoAlertDialog(
            context = context,
            message = context.getString(R.string.error_updating),
            title = context.getString(R.string.update),
            negative = context.getString(R.string.ok)
        )
    }

    fun createRegisterErrorDialog(
        context: Context,
        e: Exception
    ): AlertDialog {
        return createInfoAlertDialog(
            context = context,
            message = context.getString(R.string.register_error, e.message),
            title = context.getString(R.string.register),
            negative = context.getString(R.string.cancel)
        )
    }

    fun showVerificationErrorDialog(
        context: Context,
        onPositiveCallback: () -> Unit
    ): AlertDialog {
        return createInfoAlertDialog(
            context = context,
            message = context.getString(R.string.register_email_verification_error),
            negative = context.getString(R.string.cancel),
            positive = context.getString(R.string.ok),
            title = context.getString(R.string.register),
            onPositiveCallback = { onPositiveCallback() }
        )
    }

    fun createVerificationSuccessDialog(
        context: Context,
        onPositiveCallback: () -> Unit
    ): AlertDialog {
        return createInfoAlertDialog(
            context = context,
            message = context.getString(R.string.register_email_verification),
            negative = context.getString(R.string.cancel),
            positive = context.getString(R.string.ok),
            title = context.getString(R.string.register),
            onPositiveCallback = { onPositiveCallback() }
        )
    }

    fun createLoadingDialog(context: Context): AlertDialog {
        return createNonCancellableDialog(
            context = context,
            resId = R.layout.dialog_loading
        )
    }

    fun showConnectionErrorDialog(context: Context) {
        createInfoAlertDialog(
            context = context,
            message = context.getString(R.string.register_error_connection),
            title = context.getString(R.string.register),
            negative = context.getString(R.string.ok)
        )
    }

    fun showUsernameExistsDialog(context: Context) {
        createInfoAlertDialog(
            context = context,
            message = context.getString(R.string.register_error_username_in_use),
            title = context.getString(R.string.register),
            negative = context.getString(R.string.ok)
        )
    }

    fun showInvalidUsernameDialog(context: Context) {
        createInfoAlertDialog(
            context = context,
            message = context.getString(R.string.register_error_username),
            title = context.getString(R.string.register),
            negative = context.getString(R.string.ok)
        )
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

    fun createFollowUserNotFoundDialog(context: Context): AlertDialog {
        return createInfoAlertDialog(
            context = context,
            message = context.getString(R.string.user_profile_follow_not_found_error),
            title = context.getString(R.string.follow),
            negative = context.getString(R.string.ok)
        )
    }

    fun createFollowErrorDialog(context: Context): AlertDialog {
        return createInfoAlertDialog(
            context = context,
            message = context.getString(R.string.error_connection),
            title = context.getString(R.string.follow),
            negative = context.getString(R.string.ok)
        )
    }

    fun createEmailVerificationDialog(
        context: Context,
        onPositiveCallback: () -> Unit
    ): AlertDialog {
        return createInfoAlertDialog(
            context = context,
            message = context.getString(R.string.login_email_verification_error),
            negative = context.getString(R.string.ok),
            positive = context.getString(R.string.resend),
            onPositiveCallback = { onPositiveCallback() }
        )
    }

    fun createErrorDialog(context: Context, message: String): AlertDialog {
        return createInfoAlertDialog(
            context = context,
            message = message,
            title = context.getString(R.string.login),
            negative = context.getString(R.string.cancel)
        )
    }

    fun createEmptyEmailDialog(context: Context): AlertDialog {
        return createInfoAlertDialog(
            context = context,
            message = context.getString(R.string.error_email),
            title = context.getString(R.string.login),
            negative = context.getString(R.string.cancel)
        )
    }

    fun createErrorSendingTellDialog(context: Context): AlertDialog {
        return createInfoAlertDialog(
            context = context,
            message = context.getString(R.string.send_tell_error)
        )
    }

    fun createEmptyPasswordDialog(context: Context): AlertDialog {
        return createInfoAlertDialog(
            context = context,
            message = context.getString(R.string.error_password),
            title = context.getString(R.string.login),
            negative = context.getString(R.string.cancel)
        )
    }
}
