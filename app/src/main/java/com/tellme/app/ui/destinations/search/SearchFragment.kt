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
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.tellme.R
import com.tellme.app.dagger.inject
import com.tellme.app.data.CoroutinesDispatcherProvider
import com.tellme.app.extensions.hideSoftInput
import com.tellme.app.model.User
import com.tellme.app.ui.adapter.LatestUserSearchAdapter
import com.tellme.app.ui.adapter.UserAdapter
import com.tellme.app.util.DateUtils
import com.tellme.app.util.ViewUtils
import com.tellme.app.viewmodels.main.SearchViewModel
import com.tellme.app.viewmodels.main.UserViewModel
import com.tellme.databinding.FragmentSearchBinding
import javax.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import timber.log.Timber

class SearchFragment : Fragment(), UserAdapter.UserClickListener,
    LatestUserSearchAdapter.LatestUserSearchClickListener {

    private lateinit var resultViewAdapter: UserAdapter
    private lateinit var resultViewManager: LinearLayoutManager
    private lateinit var latestViewAdapter: LatestUserSearchAdapter
    private lateinit var latestViewManager: LinearLayoutManager

    private lateinit var mContext: Context
    private lateinit var binding: FragmentSearchBinding

    @Inject lateinit var dispatcherProvider: CoroutinesDispatcherProvider
    @Inject lateinit var userViewModel: UserViewModel
    @Inject lateinit var searchViewModel: SearchViewModel

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
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSearchResultsAdapter(this)
        setupSearchLatestAdapter(this)
        setupSearchField()

        binding.textViewClearLatest.setOnClickListener {
            showClearSearchResultsDialog()
        }

        searchViewModel.searchLatest.observe(viewLifecycleOwner, Observer {
            Timber.d(it.toString())
            when (it.isEmpty()) {
                true -> hideLatestUserSearches()
                false -> showLatestUserSearches()
            }
        })
    }

    private fun showLatestUserSearches() {
        binding.textViewLatest.visibility = View.VISIBLE
        binding.textViewClearLatest.visibility = View.VISIBLE
        binding.recyclerViewSearchLatest.visibility = View.VISIBLE
    }

    private fun hideLatestUserSearches() {
        binding.textViewLatest.visibility = View.GONE
        binding.textViewClearLatest.visibility = View.GONE
        binding.recyclerViewSearchLatest.visibility = View.GONE
    }

    private fun setupSearchField() {
        val searchInputFlow = callbackFlow {
            binding.editTextSearch.addTextChangedListener {
                offer(it.toString())
            }
            awaitClose()
        }

        val searchResultFlow = searchInputFlow
            .filter { it.length >= 3 }
            .debounce(500)
            .mapLatest { searchViewModel.getAllUsersByQuery(it, 30) }

        lifecycleScope.launch { searchResultFlow.collect() }
    }

    private fun showClearSearchResultsDialog() {
        ViewUtils.createInfoAlertDialog(
            context = requireContext(),
            message = getString(R.string.search_clear_latest),
            negative = getString(R.string.cancel),
            positive = getString(R.string.clear),
            title = getString(R.string.search_clear_latest_title),
            onPositiveCallback = { searchViewModel.clearLatestUserSearches() }
        ).show()
    }

    override suspend fun onLatestUserClicked(user: User) {
        requireActivity().hideSoftInput()

        val action = SearchFragmentDirections.actionSearchFragmentToUserProfileFragment(user)
        findNavController().navigate(action)
    }

    override suspend fun onResultUserClicked(user: User) {
        requireActivity().hideSoftInput()

        val action = SearchFragmentDirections.actionSearchFragmentToUserProfileFragment(user)
        findNavController().navigate(action)

        updateUserLatestSearch(user)
    }

    private fun updateUserLatestSearch(user: User) = lifecycleScope.launch {
        val updatedUser = user.copy(latestSearchAt = DateUtils.now())
        Timber.d("Updated $updatedUser")
        searchViewModel.cacheLatestUserSearched(updatedUser)
    }

    private fun setupSearchResultsAdapter(listener: UserAdapter.UserClickListener) {
        resultViewManager = LinearLayoutManager(activity)
        resultViewAdapter = UserAdapter(this, listener)

        binding.recyclerViewSearchResults.apply {
            layoutManager = resultViewManager
            adapter = resultViewAdapter

            addItemDecoration(DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL))
        }

        searchViewModel.searchResults.observe(viewLifecycleOwner, Observer {
            resultViewAdapter.submitList(it.filter { user -> user.uid != userViewModel.loggedInUser.value!!.uid })
        })
    }

    private fun setupSearchLatestAdapter(listener: LatestUserSearchAdapter.LatestUserSearchClickListener) {
        latestViewManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        latestViewAdapter = LatestUserSearchAdapter(this, listener)

        binding.recyclerViewSearchLatest.apply {
            layoutManager = latestViewManager
            adapter = latestViewAdapter
        }

        searchViewModel.searchLatest.observe(viewLifecycleOwner, Observer { users ->
            if (users.isNotEmpty()) {
                binding.textViewLatest.visibility = View.VISIBLE
                binding.recyclerViewSearchLatest.visibility = View.VISIBLE
                latestViewAdapter.submitList(users.sorted())
            }
        })
    }
}
