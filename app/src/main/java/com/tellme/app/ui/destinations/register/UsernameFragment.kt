/*
 * Copyright 2020 - André Thiele
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
import com.tellme.app.extensions.showSoftInput
import com.tellme.app.util.DialogUtils
import com.tellme.app.util.ValidationUtils
import com.tellme.app.util.ViewUtils
import com.tellme.app.viewmodels.auth.AuthViewModel
import com.tellme.databinding.FragmentRegisterUsernameBinding
import com.uber.autodispose.android.lifecycle.autoDispose
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlinx.coroutines.launch

class UsernameFragment : Fragment() {

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

        showSoftInput(binding.editTextUsername)

        val inputUsernameObservable = RxTextView.textChanges(binding.editTextUsername)
            .map { it.toString().trim() }

        val validUsernameObservable = inputUsernameObservable
            .map { ValidationUtils.isValidUsername(it) }

        validUsernameObservable
            .autoDispose(viewLifecycleOwner)
            .subscribe { valid ->
                binding.buttonConfirm.isEnabled = valid
                ViewUtils.hideInputLayoutWarning(binding.inputLayoutUsername)
            }

        validUsernameObservable
            .debounce(500, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .autoDispose(viewLifecycleOwner)
            .subscribe { valid ->
                ViewUtils.toggleInputLayoutWarning(
                    textInputLayout = binding.inputLayoutUsername,
                    textInputEditText = binding.editTextUsername,
                    message = getString(R.string.register_error_username_invalid),
                    enabled = !valid
                )
            }

        binding.buttonConfirm.setOnClickListener {
            loadingDialog = DialogUtils.createLoadingDialog(requireContext())
                .also { it.show() }

            lifecycleScope.launch {
                val username = binding.editTextUsername.text.toString()
                val usernameAlreadyInUse = authViewModel.isUsernameAlreadyInUse(username)

                loadingDialog?.dismiss()

                if (usernameAlreadyInUse) {
                    DialogUtils.createEmailAlreadyInUseDialog(requireContext()).show()
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.login_options, menu)
    }
}
