/*
 * Copyright 2020 - André Ramon Thiele
 *
 * Department of Computer Science and Media
 * University of Applied Sciences Brandenburg
 */

package com.tellme.app.dagger

import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentificationOptions
import dagger.Module
import dagger.Provides

@Module
abstract class TranslationModule {

    @Module
    companion object {

        @JvmStatic
        @Provides
        fun provideFirebaseLanguageIdentifcation(): FirebaseLanguageIdentification {
            return FirebaseNaturalLanguage.getInstance().getLanguageIdentification(
                FirebaseLanguageIdentificationOptions.Builder()
                    .setConfidenceThreshold(.5f)
                    .build()
            )
        }
    }
}
