/*
 * Copyright 2020 - André Ramon Thiele
 *
 * Department of Computer Science and Media
 * University of Applied Sciences Brandenburg
 */

package com.tellme.app.ui.destinations.start

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.tellme.R
import com.tellme.app.dagger.inject
import com.tellme.app.extensions.hideSoftInput
import com.tellme.app.util.DialogUtils
import com.tellme.app.viewmodels.auth.AuthViewModel
import com.tellme.databinding.FragmentStartBinding
import javax.inject.Inject
import kotlinx.coroutines.launch

class StartFragment : Fragment() {

    private lateinit var binding: FragmentStartBinding
    private var loadingDialog: AlertDialog? = null

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
        binding = FragmentStartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLoginButton()

        binding.buttonRegister.setOnClickListener {
            findNavController().navigate(R.id.action_startFragment_to_nameFragment)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        requireActivity().hideSoftInput()
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
            e.message?.let { message ->
                DialogUtils.createErrorDialog(requireContext(), message).show()
            }

            e.printStackTrace()
        } finally {
            authViewModel.logout()
            loadingDialog?.dismiss()
        }
    }

    private fun startMainActivity() {
        val action = StartFragmentDirections.actionStartFragmentToMainActivity()
        loadingDialog?.dismiss()
        findNavController().navigate(action)
        activity?.finish()
    }
}
