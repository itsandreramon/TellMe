/*
 * Copyright 2020 - André Thiele
 *
 * Fachbereich Informatik und Medien
 * Technische Hochschule Brandenburg
 */

package com.tellme.app.ui.destinations.inbox

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.jakewharton.rxbinding2.widget.RxTextView
import com.tellme.R
import com.tellme.app.dagger.inject
import com.tellme.app.extensions.showSoftInput
import com.tellme.app.model.Tell
import com.tellme.app.util.DateUtils
import com.tellme.app.util.EXTRA_TELL_KEY
import com.tellme.app.util.EXTRA_TELL_KEY_UPDATED
import com.tellme.app.util.TELL_KEY_QUESTION
import com.tellme.app.viewmodels.main.TellViewModel
import com.tellme.databinding.ActivityReplyTellBinding
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class ReplyTellActivity : AppCompatActivity() {

    private val disposables = CompositeDisposable()
    private lateinit var binding: ActivityReplyTellBinding

    @Inject lateinit var tellViewModel: TellViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        inject(this)
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_reply_tell)
        binding.question = intent.getStringExtra(TELL_KEY_QUESTION)
        showSoftInput(binding.editTextReply)

        setupReplyButton()

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_close)
        }

        val replyInputObservable = RxTextView.textChanges(binding.editTextReply)
            .map { it.toString().trim() }

        val validReplyInputObservable = replyInputObservable
            .map { it.isNotEmpty() }

        validReplyInputObservable
            .subscribe { valid ->
                binding.buttonReply.isEnabled = valid
            }.also { disposables.add(it) }
    }

    private fun setupReplyButton() {
        binding.buttonReply.setOnClickListener {
            val tell = intent.getParcelableExtra<Tell>(EXTRA_TELL_KEY)!!

            val updatedTell = tell.copy(
                replyDate = DateUtils.now(),
                reply = binding.editTextReply.text.toString()
            )

            val result = Intent()
            result.putExtra(EXTRA_TELL_KEY_UPDATED, updatedTell)
            result.putExtra(EXTRA_TELL_KEY, tell)
            setResult(Activity.RESULT_OK, result)
            finish()
        }
    }

    override fun onDestroy() {
        disposables.dispose()
        super.onDestroy()
    }
}
