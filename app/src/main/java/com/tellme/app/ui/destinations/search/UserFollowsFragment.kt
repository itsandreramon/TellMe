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
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.tellme.app.dagger.inject
import com.tellme.app.data.CoroutinesDispatcherProvider
import com.tellme.app.model.User
import com.tellme.app.ui.adapter.FollowsListAdapter
import com.tellme.app.util.ArgsHelper
import com.tellme.app.viewmodels.main.UserViewModel
import com.tellme.databinding.FragmentUserListFollowsFollowersBinding
import javax.inject.Inject
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

class UserFollowsFragment : Fragment(), FollowsListAdapter.FollowListUserClickListener {

    private val args: FollowsFollowersFragmentArgs? by lazy {
        (parentFragment as? ArgsHelper)?.passArguments()
    }

    private lateinit var viewAdapter: FollowsListAdapter
    private lateinit var viewManager: LinearLayoutManager

    private lateinit var mContext: Context
    private lateinit var binding: FragmentUserListFollowsFollowersBinding

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
        binding = FragmentUserListFollowsFollowersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter(this)

        args?.let { args ->
            lifecycleScope.launch {
                submitList(args.user.follows)
            }
        }
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

    private fun setupAdapter(listener: FollowsListAdapter.FollowListUserClickListener) {
        viewManager = LinearLayoutManager(activity)
        viewAdapter = FollowsListAdapter(listener)

        binding.recyclerViewFollows.apply {
            layoutManager = viewManager
            adapter = viewAdapter

            addItemDecoration(DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL))
        }
    }

    override fun onFollowListUserClicked(user: User) {
        (parentFragment as? FollowsListAdapter.FollowListUserClickListener)?.onFollowListUserClicked(user)
    }

    override fun onFollowListUserButtonFollowClicked(user: User) {
        (parentFragment as? FollowsListAdapter.FollowListUserClickListener)?.onFollowListUserButtonFollowClicked(user)
    }
}
