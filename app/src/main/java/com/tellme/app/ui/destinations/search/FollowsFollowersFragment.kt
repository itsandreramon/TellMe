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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.tellme.R
import com.tellme.app.dagger.inject
import com.tellme.app.data.CoroutinesDispatcherProvider
import com.tellme.app.model.User
import com.tellme.app.ui.adapter.FollowsListAdapter
import com.tellme.app.util.ArgsHelper
import com.tellme.app.util.ViewUtils
import com.tellme.app.viewmodels.main.UserViewModel
import com.tellme.databinding.FragmentUserFollowsFollowingBinding
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

class FollowsFollowersFragment : Fragment(), ArgsHelper, FollowsListAdapter.FollowListUserClickListener {

    private val args: FollowsFollowersFragmentArgs by navArgs()

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

    override fun passArguments(): FollowsFollowersFragmentArgs? {
        return args
    }

    override fun onFollowListUserClicked(user: User) {
        val action = FollowsFollowersFragmentDirections.actionFollowsFollowersFragmentToUserProfileFragment(user)
        findNavController().navigate(action)
    }

    override fun onFollowListUserButtonFollowClicked(user: User) {
        try {
            val loggedInUser = userViewModel.loggedInUser.value!!
            val isFollowing = loggedInUser.follows.contains(user.uid)

            if (isFollowing) {
                lifecycleScope.launch {
                    userViewModel.unfollowUserByUid(loggedInUser, user.uid)
                }
            } else {
                lifecycleScope.launch {
                    userViewModel.followUserByUid(loggedInUser, user.uid)
                }
            }
        } catch (e: IOException) {
            ViewUtils.showFollowErrorDialog(requireContext())
        }
    }

    private inner class ScreenSlidePagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        val fragments = mapOf<Int, Pair<Fragment, String>>(
            0 to (UserFollowsFragment() to getString(R.string.following)),
            1 to (UserFollowersFragment() to getString(R.string.followers))
        )

        override fun getItemCount(): Int = fragments.size

        override fun createFragment(position: Int): Fragment {
            return fragments[position]?.first!!
        }
    }
}
