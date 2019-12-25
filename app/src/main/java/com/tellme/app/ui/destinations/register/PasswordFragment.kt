/*
 * Copyright © 2019 - André Thiele
 *
 * Fachbereich Informatik und Medien
 * Technische Hochschule Brandenburg
 */

package com.tellme.app.ui.destinations.register

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.jakewharton.rxbinding2.widget.RxTextView
import com.tellme.R
import com.tellme.app.dagger.inject
import com.tellme.app.model.User
import com.tellme.app.util.ValidationUtils
import com.tellme.app.util.ViewUtils
import com.tellme.app.viewmodels.auth.AuthViewModel
import com.tellme.databinding.FragmentRegisterPasswordBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.Observables
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlinx.coroutines.launch
import timber.log.Timber

class PasswordFragment : Fragment() {

    private val disposables = CompositeDisposable()
    private val args: PasswordFragmentArgs by navArgs()
    private var loadingDialog: AlertDialog? = null
    private lateinit var binding: FragmentRegisterPasswordBinding

    @Inject lateinit var authViewModel: AuthViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        binding.editTextInputPassword.requestFocus()

        val inputPasswordObservable = RxTextView.textChanges(binding.editTextInputPassword)
            .map { it.toString() }

        val confirmPasswordObservable = RxTextView.textChanges(binding.editTextConfirmPassword)
            .map { it.toString() }

        val validInputPasswordObservable = inputPasswordObservable
            .map { ValidationUtils.isValidInputPassword(it) }

        val validConfirmPasswordObservable = Observables.combineLatest(
            inputPasswordObservable,
            confirmPasswordObservable
        ) { input, confirm ->
            ValidationUtils.isValidConfirmPassword(input, confirm)
        }

        validInputPasswordObservable
            .subscribe {
                ViewUtils.hideInputLayoutWarning(binding.inputLayoutInputPassword)
            }.also { disposables.add(it) }

        validConfirmPasswordObservable
            .subscribe { valid ->
                binding.buttonRegister.isEnabled = valid
                ViewUtils.hideInputLayoutWarning(binding.inputLayoutConfirmPassword)
            }.also { disposables.add(it) }

        validInputPasswordObservable
            .debounce(500, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { valid ->
                ViewUtils.toggleInputLayoutWarning(
                    textInputLayout = binding.inputLayoutInputPassword,
                    textInputEditText = binding.editTextInputPassword,
                    message = getString(R.string.register_error_password_invalid_length),
                    enabled = !valid
                )
            }.also { disposables.add(it) }

        validConfirmPasswordObservable
            .debounce(500, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { valid ->
                ViewUtils.toggleInputLayoutWarning(
                    textInputLayout = binding.inputLayoutConfirmPassword,
                    textInputEditText = binding.editTextConfirmPassword,
                    message = getString(R.string.register_error_password_mismatch),
                    enabled = !valid
                )
            }.also { disposables.add(it) }

        binding.buttonRegister.setOnClickListener {
            val name = args.name
            val username = args.username
            val email = args.email
            val password = binding.editTextInputPassword.text.toString()

            val loadingDialog = ViewUtils.showLoadingDialog(
                context = requireContext(),
                layout = R.layout.dialog_loading
            )

            lifecycleScope.launch {
                try {
                    register(name, username, email, password)
                    showRegisterSuccessDialog()
                } catch (exception: Exception) {
                    showRegisterErrorDialog(exception)
                }

                loadingDialog.dismiss()
            }
        }
    }

    private suspend fun register(name: String, username: String, email: String, password: String) {
        var uid: String? = null

        try {
            uid = authViewModel.register(email, password)
            val firebaseUser = authViewModel.getCurrentUser()!!

            val user = User(
                uid = uid,
                name = name,
                email = email,
                username = username
            )

            authViewModel.addUserToDatabase(user)
            authViewModel.sendEmailVerification(firebaseUser, Locale.getDefault().language)
        } catch (registerException: Exception) {
            Timber.e(registerException)

            try {
                uid?.let { authViewModel.deleteUserByUid(uid) }
            } catch (deleteException: Exception) {
                Timber.e(deleteException)
            }

            throw registerException
        }

        authViewModel.logout()
    }

    private fun showRegisterSuccessDialog() {
        ViewUtils.createActionInfoAlertDialog(
            context = requireContext(),
            message = getString(R.string.register_success),
            title = getString(R.string.register),
            positive = getString(R.string.ok),
            onPositiveCallback = { findNavController().navigate(R.id.action_passwordFragment_to_startFragment) }
        ).show()
    }

    private fun showRegisterErrorDialog(e: Exception) {
        ViewUtils.createInfoAlertDialog(
            context = requireContext(),
            message = getString(R.string.register_error, e.message),
            title = getString(R.string.register),
            negative = getString(R.string.cancel)
        ).show()
    }

    private fun showVerificationErrorDialog() {
        ViewUtils.createInfoAlertDialog(
            context = requireContext(),
            message = getString(R.string.register_email_verification_error),
            negative = getString(R.string.cancel),
            positive = getString(R.string.ok),
            title = getString(R.string.register),
            onPositiveCallback = { findNavController().navigate(R.id.action_passwordFragment_to_startFragment) }
        ).show()
    }

    private fun showVerificationSuccessDialog() {
        ViewUtils.createInfoAlertDialog(
            context = requireContext(),
            message = getString(R.string.register_email_verification),
            negative = getString(R.string.cancel),
            positive = getString(R.string.ok),
            title = getString(R.string.register),
            onPositiveCallback = { findNavController().navigate(R.id.action_passwordFragment_to_startFragment) }
        ).show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.login_options, menu)
    }

    override fun onDestroy() {
        disposables.dispose()
        super.onDestroy()
    }
}
