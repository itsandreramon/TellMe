/*
 * Copyright © 2019 - André Thiele
 *
 * Fachbereich Informatik und Medien
 * Technische Hochschule Brandenburg
 */

package com.tellme.app.ui.destinations.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.tellme.R
import com.tellme.app.dagger.inject
import com.tellme.app.data.CoroutinesDispatcherProvider
import com.tellme.app.extensions.setUserProfileImageFromPath
import com.tellme.app.viewmodels.main.FeedViewModel
import com.tellme.app.viewmodels.main.InboxViewModel
import com.tellme.app.viewmodels.main.TellViewModel
import com.tellme.app.viewmodels.main.UserViewModel
import com.tellme.databinding.FragmentProfileBinding
import javax.inject.Inject
import kotlinx.coroutines.launch
import timber.log.Timber

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    // TODO Not optimal, consider DataManager to delete all data
    @Inject lateinit var userViewModel: UserViewModel
    @Inject lateinit var tellViewModel: TellViewModel
    @Inject lateinit var inboxViewModel: InboxViewModel
    @Inject lateinit var feedViewModel: FeedViewModel

    @Inject lateinit var dispatcherProvider: CoroutinesDispatcherProvider

    override fun onAttach(context: Context) {
        super.onAttach(context)
        inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUser()

        binding.buttonEdit.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.profileEditActivity))
        binding.buttonLogout.setOnClickListener {
            lifecycleScope.launch { logout() }
        }
    }

    private suspend fun logout() {
        userViewModel.logout()

        userViewModel.invalidateUserCache()
        inboxViewModel.invalidateInboxCache()
        feedViewModel.invalidateFeedCache()

        findNavController().navigate(R.id.loginRegisterActivity)
        requireActivity().finish()
    }

    private fun setupUser() {
        userViewModel.loggedInUser.observe(viewLifecycleOwner, Observer { user ->
            user?.let {
                binding.user = user
                binding.imageViewUserAvatar.setUserProfileImageFromPath(user.avatar)
                Timber.d(user.toString())
            }
        })
    }
}
