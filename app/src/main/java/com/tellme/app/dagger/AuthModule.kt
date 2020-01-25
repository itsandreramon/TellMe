/*
 * Copyright 2020 - André Ramon Thiele
 *
 * Department of Computer Science and Media
 * University of Applied Sciences Brandenburg
 */

package com.tellme.app.dagger

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.tellme.app.viewmodels.auth.AuthViewModel
import com.tellme.app.viewmodels.auth.AuthViewModelFactory
import dagger.Module
import dagger.Provides

@Module
abstract class AuthModule {

    @Module
    companion object {

        @JvmStatic
        @Provides
        fun provideAuthViewModel(
            factory: AuthViewModelFactory,
            activity: FragmentActivity
        ): AuthViewModel {
            return ViewModelProvider(activity, factory).get(AuthViewModel::class.java)
        }
    }
}
