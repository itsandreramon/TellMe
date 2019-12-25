/*
 * Copyright © 2019 - André Thiele
 *
 * Fachbereich Informatik und Medien
 * Technische Hochschule Brandenburg
 */

package com.tellme.app.ui.destinations.login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseUser
import com.tellme.R
import com.tellme.app.dagger.inject
import com.tellme.app.extensions.hideSoftInput
import com.tellme.app.extensions.showSoftInput
import com.tellme.app.util.ViewUtils
import com.tellme.app.viewmodels.auth.AuthViewModel
import com.tellme.databinding.FragmentLoginBinding
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginFragment : Fragment() {

    companion object {
        private val TAG = LoginFragment::class.simpleName
    }

    private lateinit var binding: FragmentLoginBinding
    private var loadingDialog: AlertDialog? = null

    @Inject lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLoginButton()
        showSoftInput(binding.editTextEmail)
    }

    private fun setupLoginButton() {
        binding.buttonLogin.setOnClickListener {
            val email = binding.editTextEmail.text.toString().trim()
            val password = binding.editTextPassword.text.toString()

            when {
                email.isEmpty() -> showEmptyEmailDialog()
                password.isEmpty() -> showEmptyPasswordDialog()
                else -> {
                    requireActivity().hideSoftInput()
                    showProgressDialog()
                    lifecycleScope.launch { login(email, password) }
                }
            }
        }
    }

    private suspend fun login(email: String, password: String) {
        try {
            authViewModel.login(email, password)
            val currentUser = authViewModel.getCurrentUser()!!

            if (currentUser.isEmailVerified) {
                startMainActivity()
            } else {
                showEmailVerificationDialog(currentUser)
            }
        } catch (e: Exception) {
            authViewModel.logout()
            showErrorDialog(e.message!!)
            e.printStackTrace()
        } finally {
            loadingDialog?.dismiss()
        }
    }

    private fun showProgressDialog() {
        loadingDialog = ViewUtils
            .createNonCancellableDialog(requireActivity(), R.layout.dialog_login)
            .also { it.show() }
    }

    private fun showEmptyPasswordDialog() {
        ViewUtils.createInfoAlertDialog(
            requireActivity(),
            message = getString(R.string.error_password),
            title = getString(R.string.login),
            negative = getString(R.string.cancel)
        ).show()
    }

    private fun startMainActivity() {
        val action = LoginFragmentDirections.actionLoginFragmentToMainActivity()
        loadingDialog?.dismiss()
        findNavController().navigate(action)
        activity?.finish()
    }

    private fun showEmailVerificationDialog(user: FirebaseUser) {
        AlertDialog.Builder(requireContext()).apply {
            setTitle(getString(R.string.login))
            setMessage(getString(R.string.login_email_verification_error))
            setNegativeButton(getString(R.string.ok)) { _, _ -> }
            setPositiveButton(getString(R.string.resend)) { _, _ -> user.sendEmailVerification() }
        }.create().show()
    }

    private fun showErrorDialog(message: String) {
        ViewUtils.createInfoAlertDialog(
            requireActivity(),
            message = message,
            title = getString(R.string.login),
            negative = getString(R.string.cancel)
        ).show()
    }

    private fun showEmptyEmailDialog() {
        ViewUtils.createInfoAlertDialog(
            requireActivity(),
            message = getString(R.string.error_email),
            title = getString(R.string.login),
            negative = getString(R.string.cancel)
        ).show()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        inject(this)
    }
}
