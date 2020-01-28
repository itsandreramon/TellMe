/*
 * Copyright 2020 - André Ramon Thiele
 *
 * Department of Computer Science and Media
 * University of Applied Sciences Brandenburg
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
import com.tellme.app.dagger.inject
import com.tellme.app.extensions.hideSoftInput
import com.tellme.app.extensions.showSoftInput
import com.tellme.app.util.DialogUtils
import com.tellme.app.viewmodels.auth.AuthViewModel
import com.tellme.databinding.FragmentLoginBinding
import javax.inject.Inject
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

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
                email.isEmpty() -> DialogUtils.createEmptyEmailDialog(requireContext()).show()
                password.isEmpty() -> DialogUtils.createEmptyPasswordDialog(requireContext()).show()
                else -> {
                    requireActivity().hideSoftInput()

                    loadingDialog = DialogUtils.createLoadingDialog(requireContext())
                        .also { it.show() }

                    lifecycleScope.launch { login(email, password) }
                }
            }
        }
    }

    private suspend fun login(email: String, password: String) {
        try {
            authViewModel.login(email, password)
            val currentUser = authViewModel.getCurrentUserFirebase()!!

            if (currentUser.isEmailVerified) {
                startMainActivity()
            } else {
                DialogUtils.createEmailVerificationDialog(requireContext()) {
                    currentUser.sendEmailVerification()
                }.show()
            }
        } catch (e: Exception) {
            authViewModel.logout()

            e.message?.let { message ->
                DialogUtils.createErrorDialog(requireContext(), message).show()
            }

            e.printStackTrace()
        } finally {
            loadingDialog?.dismiss()
        }
    }

    private fun startMainActivity() {
        val action = LoginFragmentDirections.actionLoginFragmentToMainActivity()
        loadingDialog?.dismiss()
        findNavController().navigate(action)
        activity?.finish()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        inject(this)
    }
}
