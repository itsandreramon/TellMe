/*
 * Copyright © 2019 - André Thiele
 *
 * Fachbereich Informatik und Medien
 * Technische Hochschule Brandenburg
 */

package com.tellme.app.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.tellme.R
import com.tellme.app.dagger.inject
import com.tellme.app.extensions.hideSoftInput
import com.tellme.app.network.ConnectivityChecker
import com.tellme.app.viewmodels.main.UserViewModel
import com.tellme.databinding.ActivityMainBinding
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @Inject lateinit var userViewModel: UserViewModel
    @Inject lateinit var connectivityChecker: ConnectivityChecker

    override fun onCreate(savedInstanceState: Bundle?) {
        inject(this)

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        if (savedInstanceState == null) {
            setupNavigation()
        }

        setupConnectivityChecker()
    }

    private fun setupConnectivityChecker() {
        lifecycle.addObserver(connectivityChecker)
        connectivityChecker.isConnected.observe(this@MainActivity, Observer<Boolean> { isConnected ->
            if (isConnected) {
                handleNetworkConnected()
            } else {
                handleNetworkNotConnected()
            }
        })
    }

    private fun handleNetworkConnected() {
        // TODO
    }

    private fun handleNetworkNotConnected() {
        // TODO
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        setupNavigation()
    }

    private fun setupNavigation() {
        val navController = findNavController(R.id.nav_host_fragment).apply {
            addOnDestinationChangedListener { _, destination, _ ->
                if (destination.id != R.id.searchFragment) {
                    hideSoftInput()
                }
            }
        }

        binding.bottomNavigationView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp()
    }
}
