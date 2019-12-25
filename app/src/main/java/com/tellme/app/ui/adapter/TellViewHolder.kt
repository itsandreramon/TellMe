/*
 * Copyright © 2019 - André Thiele
 *
 * Fachbereich Informatik und Medien
 * Technische Hochschule Brandenburg
 */

package com.tellme.app.ui.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentificationOptions
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions
import com.tellme.R
import com.tellme.app.extensions.convertDateToTimestamp
import com.tellme.app.extensions.convertTimestampToDate
import com.tellme.app.model.Tell
import com.tellme.databinding.LayoutTellItemInboxBinding
import java.util.Locale

class TellViewHolder(val binding: LayoutTellItemInboxBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(tell: Tell, listener: TellAdapter.TellClickListener) {

        binding.tell = tell
        binding.root.setOnClickListener { listener.onTellClicked(tell) }
        binding.textViewTranslate.setOnClickListener { tryTranslate(tell.question, Locale.getDefault().language) }
        binding.textViewDate.text = convertDate(tell.sendDate)
        binding.executePendingBindings()

        toggleTranslate(tell.question)
    }

    private fun toggleTranslate(message: String) {
        val languageIdentifier = FirebaseNaturalLanguage.getInstance().getLanguageIdentification(
            FirebaseLanguageIdentificationOptions.Builder()
                .setConfidenceThreshold(.5f)
                .build()
        )

        languageIdentifier.identifyLanguage(message).addOnSuccessListener { language ->
            if (language != Locale.getDefault().language && language != "und") {
                binding.textViewTranslate.visibility = View.VISIBLE
            } else {
                binding.textViewTranslate.visibility = View.INVISIBLE
            }
        }
    }

    private fun tryTranslate(message: String, outputLang: String) {
        val languageIdentifier = FirebaseNaturalLanguage.getInstance().getLanguageIdentification(
            FirebaseLanguageIdentificationOptions.Builder()
                .setConfidenceThreshold(.5f)
                .build()
        )

        binding.progressBarTranslate.visibility = View.VISIBLE

        languageIdentifier.identifyLanguage(message).apply {
            addOnSuccessListener { inputLang ->
                translate(message, inputLang, outputLang) { translation ->
                    binding.apply {
                        textViewTranslate.visibility = View.INVISIBLE
                        textViewMessageTranslation.visibility = View.VISIBLE
                        binding.progressBarTranslate.visibility = View.INVISIBLE

                        // e.g. map en to English
                        val input = Locale(inputLang).displayLanguage
                        val output = Locale.getDefault().displayLanguage

                        textViewMessageTranslation.text =
                            root.context.getString(R.string.translated_from, translation, input, output)
                    }
                }
            }
        }
    }

    private fun translate(message: String, from: String, to: String, callback: (translation: String) -> Unit) {
        val fromCode = FirebaseTranslateLanguage.languageForLanguageCode(from)
        val toCode = FirebaseTranslateLanguage.languageForLanguageCode(to)

        if (fromCode != null && toCode != null) {
            val options = FirebaseTranslatorOptions.Builder()
                .setSourceLanguage(fromCode)
                .setTargetLanguage(toCode)
                .build()

            val translator = FirebaseNaturalLanguage.getInstance().getTranslator(options)
            translator.downloadModelIfNeeded()
                .addOnSuccessListener {
                    translator.translate(message).addOnSuccessListener { translation ->
                        callback(translation)
                    }
                }
        }
    }

    private fun convertDate(timestamp: String): String {
        val date = timestamp.convertTimestampToDate()
        return date.convertDateToTimestamp("EEE, d MMM yyyy")
    }
}
