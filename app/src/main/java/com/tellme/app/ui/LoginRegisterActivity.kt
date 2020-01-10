/*
 * Copyright 2020 - André Thiele
 *
 * Fachbereich Informatik und Medien
 * Technische Hochschule Brandenburg
 */

package com.tellme.app.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.tellme.R
import com.tellme.app.dagger.inject
import com.tellme.app.extensions.hideSoftInput
import com.tellme.app.util.NoConnectivityException
import com.tellme.app.viewmodels.auth.AuthViewModel
import javax.inject.Inject

class LoginRegisterActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    @Inject lateinit var authViewModel: AuthViewModel
    @Inject lateinit var sharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_register)

        setupToolbar()
        setupNavigation()

        try {
            authViewModel.getCurrentUser()?.let {
                findNavController(R.id.nav_host_fragment).navigate(R.id.action_startFragment_to_mainActivity)
            }
        } catch (e: NoConnectivityException) {
            Toast.makeText(this, "No connection.", Toast.LENGTH_LONG).show()
        }

        hideSoftInput()
    }

    private fun setupToolbar() {
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun setupNavigation() {
        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        toolbar.setupWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp()
    }

    override fun onResume() {
        super.onResume()
        hideSoftInput()
    }
}
