/*
 * Copyright 2020 - André Ramon Thiele
 *
 * Department of Computer Science and Media
 * University of Applied Sciences Brandenburg
 */

package com.tellme.app.ui.destinations.replies

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.tellme.R
import com.tellme.app.dagger.inject
import com.tellme.app.data.Result
import com.tellme.app.model.ReplyItem
import com.tellme.app.model.User
import com.tellme.app.util.DialogUtils
import com.tellme.app.viewmodels.main.RepliesViewModel
import com.tellme.app.viewmodels.main.UserViewModel
import com.tellme.databinding.FragmentRepliesBinding
import javax.inject.Inject
import kotlinx.coroutines.launch

class RepliesFragment : Fragment(), RepliesItemViewAdapter.ReplyItemClickListener {

    private lateinit var binding: FragmentRepliesBinding
    private lateinit var mContext: Context

    @Inject lateinit var repliesViewModel: RepliesViewModel
    @Inject lateinit var userViewModel: UserViewModel

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
        binding = FragmentRepliesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTellAdapter(this)

        binding.collapsingToolbar.title = getString(R.string.fragment_replies_title)
        binding.swipeRefreshLayout.apply {
            setColorSchemeColors(ContextCompat.getColor(context, R.color.colorAccent))
            setOnRefreshListener { repliesViewModel.swipeRefreshReplies { isRefreshing = false } }
        }
    }

    override fun onReplyItemClicked(replyItem: ReplyItem) {
        lifecycleScope.launch {
            when (val result = userViewModel.getUserByUsername(replyItem.receiverUsername)) {
                is Result.Success -> {
                    navigateToUserProfile(result.data)
                }
                is Result.Error -> {
                    DialogUtils.createErrorDialog(requireContext(), "Error loading user profile.")
                }
            }
        }
    }

    private fun navigateToUserProfile(user: User) {
        when (val result = userViewModel.loggedInUser.value) {
            is Result.Success -> {
                val loggedInUser = result.data

                if (loggedInUser.uid == user.uid) {
                    val action = RepliesFragmentDirections.actionRepliesFragmentToUserProfileFragment(user)
                    findNavController().navigate(action)
                } else {
                    val action = RepliesFragmentDirections.actionRepliesFragmentToUserProfileFragment(user)
                    findNavController().navigate(action)
                }
            }
            is Result.Error -> {
                // TODO
            }
        }
    }

    private fun setupTellAdapter(listener: RepliesItemViewAdapter.ReplyItemClickListener) {
        val viewManager = LinearLayoutManager(activity)
        val viewAdapter = RepliesItemViewAdapter(listener)

        binding.inboxRecyclerView.apply {
            layoutManager = viewManager
            adapter = viewAdapter

            addItemDecoration(DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL))
        }

        repliesViewModel.replies.observe(viewLifecycleOwner, Observer { replies ->
            viewAdapter.submitList(replies.sorted())
            binding.progressBar.visibility = View.GONE
        })
    }
}
