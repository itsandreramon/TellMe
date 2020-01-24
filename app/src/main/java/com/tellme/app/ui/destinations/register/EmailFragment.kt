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
import com.tellme.app.data.CoroutinesDispatcherProvider
import com.tellme.app.extensions.showSoftInput
import com.tellme.app.util.DialogUtils
import com.tellme.app.util.ValidationUtils
import com.tellme.app.util.ViewUtils
import com.tellme.app.viewmodels.auth.AuthViewModel
import com.tellme.databinding.FragmentRegisterEmailBinding
import com.uber.autodispose.android.lifecycle.autoDispose
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlinx.coroutines.launch

class EmailFragment : Fragment() {

    private val args: EmailFragmentArgs by navArgs()
    private var loadingDialog: AlertDialog? = null
    private lateinit var binding: FragmentRegisterEmailBinding

    @Inject lateinit var authViewModel: AuthViewModel
    @Inject lateinit var dispatcherProvider: CoroutinesDispatcherProvider

    override fun onAttach(context: Context) {
        super.onAttach(context)
        inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterEmailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        showSoftInput(binding.editTextEmail)

        val inputEmailObservable = RxTextView.textChanges(binding.editTextEmail)
            .map { it.toString().trim() }

        val validEmailObservable = inputEmailObservable
            .map { ValidationUtils.isValidEmail(it) }

        validEmailObservable
            .autoDispose(viewLifecycleOwner)
            .subscribe { valid ->
                binding.buttonConfirm.isEnabled = valid
                ViewUtils.hideInputLayoutWarning(binding.inputLayoutEmail)
            }

        validEmailObservable
            .debounce(500, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .autoDispose(viewLifecycleOwner)
            .subscribe { valid ->
                ViewUtils.toggleInputLayoutWarning(
                    textInputLayout = binding.inputLayoutEmail,
                    textInputEditText = binding.editTextEmail,
                    message = getString(R.string.register_error_email_invalid),
                    enabled = !valid
                )
            }

        binding.buttonConfirm.setOnClickListener {
            val loadingDialog = DialogUtils.createLoadingDialog(requireContext())
                .also { it.show() }

            lifecycleScope.launch {
                val email = binding.editTextEmail.text.toString()
                val emailAlreadyInUse = authViewModel.isEmailAlreadyInUse(email)

                loadingDialog.dismiss()

                if (emailAlreadyInUse) {
                    DialogUtils.createEmailAlreadyInUseDialog(requireContext()).show()
                } else {
                    val action = EmailFragmentDirections.actionEmailFragmentToPasswordFragment(
                        email = email,
                        name = args.name,
                        username = args.username
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
