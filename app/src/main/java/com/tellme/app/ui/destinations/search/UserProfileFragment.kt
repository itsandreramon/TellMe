/*
 * Copyright 2020 - André Ramon Thiele
 *
 * Department of Computer Science and Media
 * University of Applied Sciences Brandenburg
 */

package com.tellme.app.ui.destinations.search

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.tellme.R
import com.tellme.app.dagger.inject
import com.tellme.app.data.Result
import com.tellme.app.extensions.setUserProfileImageFromPath
import com.tellme.app.model.User
import com.tellme.app.util.DialogUtils
import com.tellme.app.util.EXTRA_UID
import com.tellme.app.util.SEND_TELL_REQUEST
import com.tellme.app.util.ViewUtils
import com.tellme.app.viewmodels.main.TellViewModel
import com.tellme.app.viewmodels.main.UserViewModel
import com.tellme.databinding.FragmentProfileUserBinding
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.launch

class UserProfileFragment : Fragment() {

    private lateinit var viewItemViewAdapter: UserTellsItemViewAdapter
    private lateinit var viewManager: LinearLayoutManager

    private val args: UserProfileFragmentArgs by navArgs()
    private lateinit var binding: FragmentProfileUserBinding

    @Inject lateinit var userViewModel: UserViewModel
    @Inject lateinit var tellViewModel: TellViewModel

    private lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        inject(this)
        mContext = context
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
        renderUserData(args.user)

        userViewModel.loggedInUser.observe(viewLifecycleOwner, Observer { loggedInUser ->
            when (loggedInUser) {
                is Result.Success -> setupFollowButton(loggedInUser.data)
                is Result.Error -> {
                    // TODO
                }
            }
        })

        binding.editTextSendUserTell.setOnClickListener {
            val intent = Intent(activity, SendTellActivity::class.java)
            intent.putExtra(EXTRA_UID, args.user.uid)
            startActivityForResult(intent, SEND_TELL_REQUEST)
        }

        binding.layoutUserStats.setOnClickListener {
            val action = UserProfileFragmentDirections.actionUserProfileFragmentToFollowsFragment(args.user)
            findNavController().navigate(action)
        }

        setupTellAdapter()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SEND_TELL_REQUEST) {
            if (resultCode == RESULT_OK) {
                ViewUtils.createSnackbar(
                    ctx = requireContext(),
                    layout = binding.layoutCoordinator,
                    msg = getString(R.string.tell_sent)
                ).show()
            }
        }
    }

    private fun setupTellAdapter() {
        viewManager = LinearLayoutManager(activity)
        viewItemViewAdapter = UserTellsItemViewAdapter()

        binding.userTellsRecyclerView.apply {
            layoutManager = viewManager
            adapter = viewItemViewAdapter

            addItemDecoration(DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL))
        }

        lifecycleScope.launch {
            try {
                val tells = tellViewModel.findTellsByReceiverUid(args.user.uid)
                    .filter { it.reply.isNotBlank() }
                    .sorted()

                if (tells.isEmpty()) {
                    binding.textViewAbout.visibility = View.GONE
                    binding.textViewAboutMessage.visibility = View.GONE
                }

                binding.textViewUserTellCount.text = getString(R.string.tells_count, tells.size)

                viewItemViewAdapter.submitList(tells)
            } catch (e: Exception) {
                ViewUtils.showToast(requireContext(), "Error loading tells.")
            }
        }
    }

    private fun setupFollowButton(loggedInUser: User) {
        val isFollowing = loggedInUser.following.contains(args.user.uid)

        if (isFollowing) {
            ViewUtils.setFollowButtonToFollowing(binding.buttonFollow, requireContext())
        } else {
            ViewUtils.setFollowButtonToFollow(binding.buttonFollow, requireContext())
        }

        binding.buttonFollow.setOnClickListener {
            if (isFollowing) {
                lifecycleScope.launch { unfollowUserByUid(loggedInUser, args.user) }
            } else {
                lifecycleScope.launch { followUserByUid(loggedInUser, args.user) }
            }
        }
    }

    private suspend fun unfollowUserByUid(user: User, userToUnfollow: User) {
        try {
            userViewModel.unfollowUser(user, userToUnfollow)
        } catch (e: IOException) {
            DialogUtils.createFollowErrorDialog(requireContext()).show()
        }
    }

    private suspend fun followUserByUid(user: User, userToFollow: User) {
        try {
            userViewModel.followUserByUid(user, userToFollow)
        } catch (e: IOException) {
            DialogUtils.createFollowErrorDialog(requireContext()).show()
        }
    }

    private fun renderUserData(user: User) {
        binding.user = user
        binding.imageViewUserAvatar.setUserProfileImageFromPath(user.avatar)
        binding.textViewUserFollowerCount.text = getString(R.string.follower_count, user.followers.size)
        binding.textViewUserFollowingCount.text = getString(R.string.following_count, user.following.size)
        binding.textViewUserTellCount.text = getString(R.string.tells_count, 0)
        binding.editTextSendUserTell.hint = getString(R.string.send_user_tell, user.name)

        if (user.about.isEmpty()) {
            binding.textViewAbout.visibility = View.GONE
            binding.textViewAboutMessage.visibility = View.GONE
        }
    }

    private fun setupToolbar() {
        (activity as? AppCompatActivity)?.let {
            it.setSupportActionBar(binding.toolbar)
            it.supportActionBar?.setDisplayShowTitleEnabled(false)
            it.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }
}
