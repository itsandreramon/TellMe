/*
 * Copyright 2020 - André Ramon Thiele
 *
 * Department of Computer Science and Media
 * University of Applied Sciences Brandenburg
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
import com.tellme.app.extensions.showSoftInput
import com.tellme.app.model.User
import com.tellme.app.util.DialogUtils
import com.tellme.app.util.ValidationUtils
import com.tellme.app.util.ViewUtils
import com.tellme.app.viewmodels.auth.AuthViewModel
import com.tellme.databinding.FragmentRegisterPasswordBinding
import com.uber.autodispose.android.lifecycle.autoDispose
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.Observables
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PasswordFragment : Fragment() {

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

        showSoftInput(binding.editTextInputPassword)

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
            .autoDispose(viewLifecycleOwner)
            .subscribe {
                ViewUtils.hideInputLayoutWarning(binding.inputLayoutInputPassword)
            }

        validConfirmPasswordObservable
            .autoDispose(viewLifecycleOwner)
            .subscribe { valid ->
                binding.buttonRegister.isEnabled = valid
                ViewUtils.hideInputLayoutWarning(binding.inputLayoutConfirmPassword)
            }

        validInputPasswordObservable
            .debounce(500, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .autoDispose(viewLifecycleOwner)
            .subscribe { valid ->
                ViewUtils.toggleInputLayoutWarning(
                    textInputLayout = binding.inputLayoutInputPassword,
                    textInputEditText = binding.editTextInputPassword,
                    message = getString(R.string.register_error_password_invalid_length),
                    enabled = !valid
                )
            }

        validConfirmPasswordObservable
            .debounce(500, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .autoDispose(viewLifecycleOwner)
            .subscribe { valid ->
                ViewUtils.toggleInputLayoutWarning(
                    textInputLayout = binding.inputLayoutConfirmPassword,
                    textInputEditText = binding.editTextConfirmPassword,
                    message = getString(R.string.register_error_password_mismatch),
                    enabled = !valid
                )
            }

        binding.buttonRegister.setOnClickListener {
            val name = args.name
            val username = args.username
            val email = args.email
            val password = binding.editTextInputPassword.text.toString()

            val loadingDialog = DialogUtils.createLoadingDialog(requireContext())
                .also { it.show() }

            lifecycleScope.launch {
                try {
                    register(name, username, email, password)

                    DialogUtils.createRegisterSuccessDialog(requireContext()) {
                        findNavController().navigate(R.id.action_passwordFragment_to_startFragment)
                    }.show()
                } catch (exception: Exception) {
                    DialogUtils.createRegisterErrorDialog(requireContext(), exception).show()
                }

                loadingDialog.dismiss()
            }
        }
    }

    private suspend fun register(name: String, username: String, email: String, password: String) {
        var uid: String? = null

        try {
            uid = authViewModel.register(email, password)
            val firebaseUser = authViewModel.getCurrentUserFirebase()!!

            val user = User(
                uid = uid,
                name = name,
                email = email,
                username = username
            )

            Timber.d(user.toString())

            authViewModel.addUserToDatabase(user)
            authViewModel.sendEmailVerification(firebaseUser, Locale.getDefault().language)
            authViewModel.logout()
        } catch (registerException: Exception) {
            Timber.e(registerException)

            try {
                uid?.let { authViewModel.deleteUserByUid(uid) }
            } catch (deleteException: Exception) {
                Timber.e(deleteException)
            }

            authViewModel.logout()
            throw registerException
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.login_options, menu)
    }
}
