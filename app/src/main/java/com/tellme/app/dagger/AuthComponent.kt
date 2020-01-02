/*
 * Copyright 2020 - André Thiele
 *
 * Fachbereich Informatik und Medien
 * Technische Hochschule Brandenburg
 */

package com.tellme.app.dagger

import androidx.fragment.app.FragmentActivity
import com.tellme.app.dagger.scope.AuthScope
import com.tellme.app.ui.LoginRegisterActivity
import com.tellme.app.ui.destinations.login.LoginFragment
import com.tellme.app.ui.destinations.register.EmailFragment
import com.tellme.app.ui.destinations.register.PasswordFragment
import com.tellme.app.ui.destinations.register.UsernameFragment
import dagger.BindsInstance
import dagger.Subcomponent

@AuthScope
@Subcomponent(modules = [AuthModule::class])
interface AuthComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance activity: FragmentActivity): AuthComponent
    }

    fun inject(target: LoginRegisterActivity)

    fun inject(target: LoginFragment)

    fun inject(target: EmailFragment)

    fun inject(target: UsernameFragment)

    fun inject(target: PasswordFragment)
}
