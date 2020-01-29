/*
 * Copyright 2020 - André Ramon Thiele
 *
 * Department of Computer Science and Media
 * University of Applied Sciences Brandenburg
 */

package com.tellme.app.ui.destinations.feed

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tellme.R
import com.tellme.app.dagger.inject
import com.tellme.app.data.CoroutinesDispatcherProvider
import com.tellme.app.data.Result
import com.tellme.app.model.FeedItem
import com.tellme.app.util.ViewUtils
import com.tellme.app.viewmodels.main.FeedViewModel
import com.tellme.app.viewmodels.main.UserViewModel
import com.tellme.databinding.FragmentFeedBinding
import javax.inject.Inject

class FeedFragment : Fragment(), FeedItemViewAdapter.FeedClickListener {

    private lateinit var viewItemViewAdapter: FeedItemViewAdapter
    private lateinit var viewManager: LinearLayoutManager

    private lateinit var mContext: Context
    private lateinit var binding: FragmentFeedBinding

    @Inject lateinit var dispatcherProvider: CoroutinesDispatcherProvider
    @Inject lateinit var feedViewModel: FeedViewModel
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
        binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUserAdapter(this)

        binding.collapsingToolbar.title = getString(R.string.fragment_feed_title)
        binding.swipeRefreshLayout.apply {
            setColorSchemeColors(ContextCompat.getColor(context, R.color.colorAccent))
            setOnRefreshListener { feedViewModel.swipeRefreshFeed { isRefreshing = false } }
        }
    }

    private fun setupUserAdapter(listener: FeedItemViewAdapter.FeedClickListener) {
        viewManager = LinearLayoutManager(activity)
        viewItemViewAdapter = FeedItemViewAdapter(this, listener)

        // scrolls to top when new data arrives
        viewItemViewAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                viewManager.smoothScrollToPosition(binding.feedRecyclerView, null, 0)
            }
        })

        binding.feedRecyclerView.apply {
            layoutManager = viewManager
            adapter = viewItemViewAdapter

            addItemDecoration(DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL))
        }

        feedViewModel.feedItems.observe(viewLifecycleOwner, Observer { feedItems ->
            when (feedItems) {
                is Result.Success -> {
                    viewItemViewAdapter.submitList(feedItems.data.sorted())
                    binding.progressBar.visibility = View.INVISIBLE
                }
                is Result.Error -> {
                    ViewUtils.showToast(requireContext(), "Error loading feed.")
                }
            }
        })
    }

    override fun onFeedClicked(feedItem: FeedItem) {
        Toast.makeText(mContext, "Clicked ${feedItem.id}", Toast.LENGTH_LONG).show()
    }
}
