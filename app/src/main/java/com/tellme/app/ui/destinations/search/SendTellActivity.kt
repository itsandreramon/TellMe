/*
 * Copyright 2020 - André Ramon Thiele
 *
 * Department of Computer Science and Media
 * University of Applied Sciences Brandenburg
 */

package com.tellme.app.ui.destinations.search

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.jakewharton.rxbinding2.widget.RxTextView
import com.tellme.R
import com.tellme.app.dagger.inject
import com.tellme.app.extensions.showSoftInput
import com.tellme.app.model.Tell
import com.tellme.app.util.DateUtils
import com.tellme.app.util.DialogUtils
import com.tellme.app.util.EXTRA_UID
import com.tellme.app.util.ValidationUtils
import com.tellme.app.viewmodels.main.TellViewModel
import com.tellme.app.viewmodels.main.UserViewModel
import com.tellme.databinding.ActivitySendTellBinding
import com.uber.autodispose.android.lifecycle.autoDispose
import javax.inject.Inject
import kotlinx.coroutines.launch

class SendTellActivity : AppCompatActivity() {

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
            .autoDispose(this)
            .subscribe { valid ->
                binding.buttonReply.isEnabled = valid
            }

        binding.buttonReply.setOnClickListener {
            val question = binding.editTextQuestion.text.toString().trim()

            val tell = Tell(
                authorUid = userViewModel.getCurrentUserFirebase()!!.uid,
                receiverUid = intent.getStringExtra(EXTRA_UID) ?: "-1",
                question = question,
                sendDate = DateUtils.now()
            )

            lifecycleScope.launch {
                val added = addTell(tell)

                if (added) {
                    setResult(RESULT_OK)
                    finish()
                } else {
                    DialogUtils.createErrorSendingTellDialog(this@SendTellActivity).show()
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
            DialogUtils.createErrorSendingTellDialog(this)
            false
        }
    }
}
