/*
 * Copyright 2020 - André Ramon Thiele
 *
 * Department of Computer Science and Media
 * University of Applied Sciences Brandenburg
 */

package com.tellme.app.ui.destinations.search

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.tellme.R
import com.tellme.app.dagger.inject
import com.tellme.app.data.CoroutinesDispatcherProvider
import com.tellme.app.data.Result
import com.tellme.app.model.User
import com.tellme.app.util.ArgsHelper
import com.tellme.app.util.DialogUtils
import com.tellme.app.viewmodels.main.UserViewModel
import com.tellme.databinding.FragmentUserFollowsFollowingBinding
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.launch

class FollowingFollowersFragment : Fragment(), FollowingListAdapter.FollowListUserClickListener, ArgsHelper {

    private val args: FollowingFollowersFragmentArgs by navArgs()

    private lateinit var mContext: Context
    private lateinit var binding: FragmentUserFollowsFollowingBinding

    @Inject lateinit var userViewModel: UserViewModel
    @Inject lateinit var dispatcherProvider: CoroutinesDispatcherProvider

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
        binding = FragmentUserFollowsFollowingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupViewPager()
    }

    private fun setupViewPager() {
        val tabLayout = binding.tabLayout
        val viewPager = binding.viewPager

        val pagerAdapter = ScreenSlidePagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = pagerAdapter.fragments[position]?.second!!
        }.attach()
    }

    private fun setupToolbar() {
        (activity as? AppCompatActivity)?.let {
            it.setSupportActionBar(binding.toolbar)
            it.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            it.supportActionBar?.title = args.user.username
        }
    }

    override fun onFollowListUserClicked(user: User, loggedInUserUid: String) {
        val action = when (user.uid) {
            loggedInUserUid -> FollowingFollowersFragmentDirections.actionFollowsFollowersFragmentToProfileFragment()
            else -> FollowingFollowersFragmentDirections.actionFollowsFollowersFragmentToUserProfileFragment(user)
        }

        findNavController().navigate(action)
    }

    override fun onFollowListUserButtonFollowClicked(user: User) {
        val result = userViewModel.loggedInUser.value!!
        if (result is Result.Success) {
            try {
                val isFollowing = result.data.following.contains(user.uid)

                if (isFollowing) {
                    lifecycleScope.launch {
                        userViewModel.unfollowUser(result.data, user)
                    }
                } else {
                    lifecycleScope.launch {
                        userViewModel.followUserByUid(result.data, user)
                    }
                }
            } catch (e: IOException) {
                DialogUtils.createFollowErrorDialog(requireContext()).show()
            }
        }
    }

    private inner class ScreenSlidePagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        val fragments = mapOf<Int, Pair<Fragment, String>>(
            0 to (UserFollowingFragment() to getString(R.string.following)),
            1 to (UserFollowersFragment() to getString(R.string.followers))
        )

        override fun getItemCount(): Int = fragments.size

        override fun createFragment(position: Int): Fragment {
            return fragments[position]?.first!!
        }
    }

    override fun passArguments() = args
}
