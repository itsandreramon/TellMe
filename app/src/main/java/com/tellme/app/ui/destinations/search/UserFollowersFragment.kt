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
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.tellme.app.dagger.inject
import com.tellme.app.data.CoroutinesDispatcherProvider
import com.tellme.app.data.Result
import com.tellme.app.model.User
import com.tellme.app.util.ArgsHelper
import com.tellme.app.viewmodels.main.UserViewModel
import com.tellme.databinding.FragmentUserListFollowingFollowersBinding
import javax.inject.Inject
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class UserFollowersFragment : Fragment(), FollowingListAdapter.FollowListUserClickListener {

    private val args: FollowingFollowersFragmentArgs by lazy {
        (parentFragment as? ArgsHelper)?.passArguments()!!
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

        args.user.uid.let { uid ->
            userViewModel
                .getUserByUidLocal(uid)
                .observe(viewLifecycleOwner, Observer { submitList(it.followers) })
        }
    }

    private fun submitList(listToLoad: List<String>) {
        listToLoad
            .filter { it.isNotEmpty() }
            .asFlow()
            .map { userViewModel.getUserByUid(it) }
            .onEach { if (it is Result.Success) viewAdapter.addItem(it.data) }
            .launchIn(lifecycleScope)
    }

    private fun setupAdapter(listener: FollowingListAdapter.FollowListUserClickListener) {
        val result = userViewModel.loggedInUser

        viewManager = LinearLayoutManager(activity)
        viewAdapter = FollowingListAdapter(
            listener = listener,
            loggedInUser = result,
            context = requireContext(),
            viewLifecycleOwner = viewLifecycleOwner
        )

        binding.recyclerViewFollows.apply {
            layoutManager = viewManager
            adapter = viewAdapter

            addItemDecoration(DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL))
        }
    }

    override fun onFollowListUserClicked(user: User, loggedInUserUid: String) {
        (parentFragment as? FollowingListAdapter.FollowListUserClickListener)
            ?.onFollowListUserClicked(user, loggedInUserUid)
    }

    override fun onFollowListUserButtonFollowClicked(user: User) {
        (parentFragment as? FollowingListAdapter.FollowListUserClickListener)
            ?.onFollowListUserButtonFollowClicked(user)
    }
}
