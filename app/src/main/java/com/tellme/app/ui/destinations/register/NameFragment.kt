/*
 * Copyright 2020 - André Thiele
 *
 * Fachbereich Informatik und Medien
 * Technische Hochschule Brandenburg
 */

package com.tellme.app.ui.destinations.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.jakewharton.rxbinding2.widget.RxTextView
import com.tellme.R
import com.tellme.app.util.ValidationUtils
import com.tellme.app.util.ViewUtils
import com.tellme.databinding.FragmentRegisterNameBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit

class NameFragment : Fragment() {

    private val disposables = CompositeDisposable()
    private lateinit var binding: FragmentRegisterNameBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterNameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        binding.editTextName.requestFocus()

        val inputNameObservable = RxTextView.textChanges(binding.editTextName)
            .map { it.toString().trim() }

        val validNameObservable = inputNameObservable
            .map { ValidationUtils.isValidName(it) }

        validNameObservable
            .subscribe { valid ->
                binding.buttonConfirm.isEnabled = valid
                ViewUtils.hideInputLayoutWarning(binding.inputLayoutName)
            }.also { disposables.add(it) }

        validNameObservable
            .debounce(500, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { valid ->
                ViewUtils.toggleInputLayoutWarning(
                    textInputLayout = binding.inputLayoutName,
                    textInputEditText = binding.editTextName,
                    message = getString(R.string.register_error_name),
                    enabled = !valid
                )
            }.also { disposables.add(it) }

        binding.buttonConfirm.setOnClickListener {
            val name = binding.editTextName.text.toString().trim()

            val action = NameFragmentDirections.actionNameFragmentToUsernameFragment(
                name = name
            )

            findNavController().navigate(action)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.login_options, menu)
    }

    override fun onDestroy() {
        disposables.dispose()
        super.onDestroy()
    }
}
