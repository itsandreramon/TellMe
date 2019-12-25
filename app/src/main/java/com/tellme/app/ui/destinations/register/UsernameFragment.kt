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
import com.tellme.app.util.ValidationUtils
import com.tellme.app.util.ViewUtils
import com.tellme.app.viewmodels.auth.AuthViewModel
import com.tellme.databinding.FragmentRegisterUsernameBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlinx.coroutines.launch

class UsernameFragment : Fragment() {

    private val disposables = CompositeDisposable()
    private val args: UsernameFragmentArgs by navArgs()
    private var loadingDialog: AlertDialog? = null
    private lateinit var binding: FragmentRegisterUsernameBinding

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
        binding = FragmentRegisterUsernameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        binding.editTextUsername.requestFocus()

        val inputUsernameObservable = RxTextView.textChanges(binding.editTextUsername)
            .map { it.toString().trim() }

        val validUsernameObservable = inputUsernameObservable
            .map { ValidationUtils.isValidUsername(it) }

        validUsernameObservable
            .subscribe { valid ->
                binding.buttonConfirm.isEnabled = valid
                ViewUtils.hideInputLayoutWarning(binding.inputLayoutUsername)
            }.also { disposables.add(it) }

        validUsernameObservable
            .debounce(500, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { valid ->
                ViewUtils.toggleInputLayoutWarning(
                    textInputLayout = binding.inputLayoutUsername,
                    textInputEditText = binding.editTextUsername,
                    message = getString(R.string.register_error_username_invalid),
                    enabled = !valid
                )
            }.also { disposables.add(it) }

        binding.buttonConfirm.setOnClickListener {
            showLoadingDialog()

            lifecycleScope.launch {
                val username = binding.editTextUsername.text.toString()
                val usernameAlreadyInUse = authViewModel.isUsernameAlreadyInUse(username)

                hideLoadingDialog()

                if (usernameAlreadyInUse) {
                    showEmailAlreadyInUseDialog()
                } else {
                    val action = UsernameFragmentDirections.actionUsernameFragmentToEmailFragment(
                        username = username,
                        name = args.name
                    )

                    findNavController().navigate(action)
                }
            }
        }
    }

    private fun showEmailAlreadyInUseDialog() {
        ViewUtils.createInfoAlertDialog(
            context = requireContext(),
            message = getString(R.string.register_error_username_in_use)
        ).show()
    }

    private fun hideLoadingDialog() {
        loadingDialog?.dismiss()
    }

    private fun showLoadingDialog() {
        loadingDialog = ViewUtils.createNonCancellableDialog(
            context = requireContext(),
            resId = R.layout.dialog_loading
        ).also { it.show() }
    }

    private fun showConnectionErrorDialog() {
        ViewUtils.createInfoAlertDialog(
            requireActivity(),
            message = getString(R.string.register_error_connection),
            title = getString(R.string.register),
            negative = getString(R.string.ok)
        ).show()
    }

    private fun showUsernameExistsDialog() {
        ViewUtils.createInfoAlertDialog(
            requireActivity(),
            message = getString(R.string.register_error_username_in_use),
            title = getString(R.string.register),
            negative = getString(R.string.ok)
        ).show()
    }

    private fun showInvalidUsernameDialog() {
        ViewUtils.createInfoAlertDialog(
            requireActivity(),
            message = getString(R.string.register_error_username),
            title = getString(R.string.register),
            negative = getString(R.string.ok)
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
