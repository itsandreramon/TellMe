/*
 * Copyright 2020 - André Thiele
 *
 * Fachbereich Informatik und Medien
 * Technische Hochschule Brandenburg
 */

package com.tellme.app.ui.destinations.search

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.navArgs
import com.jakewharton.rxbinding2.widget.RxTextView
import com.tellme.R
import com.tellme.app.dagger.inject
import com.tellme.app.extensions.showSoftInput
import com.tellme.app.model.Tell
import com.tellme.app.util.DateUtils
import com.tellme.app.util.ValidationUtils
import com.tellme.app.util.ViewUtils
import com.tellme.app.viewmodels.main.TellViewModel
import com.tellme.app.viewmodels.main.UserViewModel
import com.tellme.databinding.ActivitySendTellBinding
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject
import kotlinx.coroutines.launch

class SendTellActivity : AppCompatActivity() {

    private var disposables = CompositeDisposable()
    private val args: SendTellActivityArgs by navArgs()
    private lateinit var binding: ActivitySendTellBinding

    @Inject lateinit var tellViewModel: TellViewModel
    @Inject lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        inject(this)
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_send_tell)
        showSoftInput(binding.editTextQuestion)

        setupToolbar()

        val questionInputObservable = RxTextView.textChanges(binding.editTextQuestion)
            .map { it.toString() }

        val validQuestionInputObservable = questionInputObservable
            .map { ValidationUtils.isValidTellLength(it) }

        validQuestionInputObservable
            .subscribe { valid ->
                binding.buttonReply.isEnabled = valid
            }.also { disposables.add(it) }

        binding.buttonReply.setOnClickListener {
            val question = binding.editTextQuestion.text.toString().trim()

            val tell = Tell(
                authorUid = userViewModel.loggedInUser.value!!.uid,
                receiverUid = args.userUid,
                question = question,
                sendDate = DateUtils.now()
            )

            lifecycleScope.launch {
                val added = addTell(tell)

                if (added) {
                    finish()
                } else {
                    ViewUtils.createInfoAlertDialog(
                        context = this@SendTellActivity,
                        message = getString(R.string.send_user_tell)
                    )
                }
            }
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_close)
        }
    }

    private suspend fun addTell(tell: Tell): Boolean {
        return try {
            tellViewModel.addTell(tell)
        } catch (e: Exception) {
            showErrorSendingTellDialog()
            false
        }
    }

    private fun showErrorSendingTellDialog() {
        ViewUtils.createInfoAlertDialog(
            context = this,
            message = getString(R.string.send_tell_error)
        )
    }

    override fun onDestroy() {
        disposables.dispose()
        super.onDestroy()
    }
}
