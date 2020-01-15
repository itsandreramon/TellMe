/*
 * Copyright 2020 - André Thiele
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
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.tellme.app.dagger.inject
import com.tellme.app.data.CoroutinesDispatcherProvider
import com.tellme.app.model.User
import com.tellme.app.util.ArgsHelper
import com.tellme.app.viewmodels.main.UserViewModel
import com.tellme.databinding.FragmentUserListFollowingFollowersBinding
import javax.inject.Inject
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

class UserFollowingFragment : Fragment(), FollowingListAdapter.FollowListUserClickListener {

    private val args: LiveData<FollowingFollowersFragmentArgs>? by lazy {
        (parentFragment as? ArgsHelper)?.passArguments()
    }

    private lateinit var viewAdapter: FollowingListAdapter
    private lateinit var viewManager: LinearLayoutManager

    private lateinit var mContext: Context
    private lateinit var binding: FragmentUserListFollowingFollowersBinding

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
        binding = FragmentUserListFollowingFollowersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter(this)

        args?.observe(viewLifecycleOwner, Observer { args ->
            lifecycleScope.launch {
                submitList(args.user.following)
            }
        })
    }

    private suspend fun submitList(listToLoad: List<String>) {
        val list = listToLoad
            .filter { it.isNotEmpty() }
            .asFlow()
            .map { userViewModel.getUserByUid(it) }
            .flowOn(dispatcherProvider.network)
            .toList()

        viewAdapter.submitList(list)
    }

    private fun setupAdapter(listener: FollowingListAdapter.FollowListUserClickListener) {
        viewManager = LinearLayoutManager(activity)
        viewAdapter = FollowingListAdapter(
            listener,
            userViewModel.loggedInUser,
            requireContext(),
            viewLifecycleOwner
        )

        binding.recyclerViewFollows.apply {
            layoutManager = viewManager
            adapter = viewAdapter

            addItemDecoration(DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL))
        }
    }

    override fun onFollowListUserClicked(user: User, loggedInUserUid: String) {
        (parentFragment as? FollowingListAdapter.FollowListUserClickListener)?.onFollowListUserClicked(user, loggedInUserUid)
    }

    override fun onFollowListUserButtonFollowClicked(user: User) {
        (parentFragment as? FollowingListAdapter.FollowListUserClickListener)?.onFollowListUserButtonFollowClicked(user)
    }
}
