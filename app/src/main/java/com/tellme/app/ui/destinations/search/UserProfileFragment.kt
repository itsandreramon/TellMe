/*
 * Copyright © 2019 - André Thiele
 *
 * Fachbereich Informatik und Medien
 * Technische Hochschule Brandenburg
 */

package com.tellme.app.ui.destinations.search

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tellme.R
import com.tellme.app.dagger.inject
import com.tellme.app.extensions.setUserProfileImageFromPath
import com.tellme.app.model.User
import com.tellme.app.util.ViewUtils
import com.tellme.app.viewmodels.main.TellViewModel
import com.tellme.app.viewmodels.main.UserViewModel
import com.tellme.databinding.FragmentProfileUserBinding
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

class UserProfileFragment : Fragment() {

    private val args: UserProfileFragmentArgs by navArgs()
    private lateinit var binding: FragmentProfileUserBinding

    @Inject lateinit var userViewModel: UserViewModel
    @Inject lateinit var tellViewModel: TellViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        renderUserData()

        userViewModel.loggedInUser.observe(viewLifecycleOwner, Observer { loggedInUser ->
            val isFollowing = loggedInUser.follows.contains(args.user.uid)

            if (isFollowing) {
                ViewUtils.setFollowButtonToFollowing(binding.buttonFollow, requireContext())
            } else {
                ViewUtils.setFollowButtonToFollow(binding.buttonFollow, requireContext())
            }

            binding.buttonFollow.setOnClickListener {
                if (isFollowing) {
                    lifecycleScope.launch { unfollowUserByUid(loggedInUser, args.user.uid) }
                } else {
                    lifecycleScope.launch { followUserByUid(loggedInUser, args.user.uid) }
                }
            }
        })

        binding.editTextSendUserTell.setOnClickListener {
            val action = UserProfileFragmentDirections.actionUserProfileFragmentToSendTellActivity(args.user.uid)
            findNavController().navigate(action)
        }

        binding.layoutUserStats.setOnClickListener {
            val action = UserProfileFragmentDirections.actionUserProfileFragmentToFollowsFragment(args.user)
            findNavController().navigate(action)
        }
    }

    private suspend fun unfollowUserByUid(user: User, userToUnfollowUid: String) {
        try {
            userViewModel.unfollowUserByUid(user, userToUnfollowUid)
        } catch (e: IOException) {
            ViewUtils.showFollowErrorDialog(requireContext())
        }
    }

    private suspend fun followUserByUid(user: User, userToFollowUid: String) {
        try {
            userViewModel.followUserByUid(user, userToFollowUid)
        } catch (e: IOException) {
            ViewUtils.showFollowErrorDialog(requireContext())
        }
    }

    private fun renderUserData() {
        binding.user = args.user
        binding.imageViewUserAvatar.setUserProfileImageFromPath(args.user.avatar)
        binding.textViewUserFollowerCount.text = getString(R.string.follower_count, 0)
        binding.textViewUserFollowingCount.text = getString(R.string.following_count, args.user.follows.size)
        binding.textViewUserTellCount.text = getString(R.string.tells_count, 0)
        binding.editTextSendUserTell.hint = getString(R.string.send_user_tell, args.user.name)
    }

    private fun setupToolbar() {
        (activity as? AppCompatActivity)?.let {
            it.setSupportActionBar(binding.toolbar)
            it.supportActionBar?.setDisplayShowTitleEnabled(false)
            it.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }
}
