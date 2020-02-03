/*
 * Copyright 2020 - André Ramon Thiele
 *
 * Department of Computer Science and Media
 * University of Applied Sciences Brandenburg
 */

package com.tellme.app.ui.destinations.inbox

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tellme.R
import com.tellme.app.dagger.inject
import com.tellme.app.data.CoroutinesDispatcherProvider
import com.tellme.app.model.Tell
import com.tellme.app.util.EXTRA_TELL
import com.tellme.app.util.EXTRA_TELL_ID
import com.tellme.app.util.EXTRA_TELL_QUESTION
import com.tellme.app.util.EXTRA_TELL_UPDATED
import com.tellme.app.util.REQUEST_REPLY_TELL
import com.tellme.app.util.ViewUtils
import com.tellme.app.viewmodels.main.InboxViewModel
import com.tellme.app.viewmodels.main.TellViewModel
import com.tellme.app.viewmodels.main.UserViewModel
import com.tellme.databinding.FragmentInboxBinding
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.launch

class InboxFragment : Fragment(), InboxItemViewAdapter.InboxItemClickListener {

    private lateinit var binding: FragmentInboxBinding
    private lateinit var mContext: Context

    @Inject lateinit var dispatcherProvider: CoroutinesDispatcherProvider
    @Inject lateinit var tellViewModel: TellViewModel
    @Inject lateinit var inboxViewModel: InboxViewModel
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
        binding = FragmentInboxBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTellAdapter(this@InboxFragment)

        binding.collapsingToolbar.title = getString(R.string.fragment_inbox_title)
        binding.swipeRefreshLayout.apply {
            setColorSchemeColors(ContextCompat.getColor(context, R.color.colorAccent))
            setOnRefreshListener { inboxViewModel.swipeRefreshInbox { isRefreshing = false } }
        }
    }

    override fun onInboxItemClicked(tell: Tell) {
        val intent = Intent(context, ReplyTellActivity::class.java)

        intent.putExtra(EXTRA_TELL_ID, tell.id)
        intent.putExtra(EXTRA_TELL, tell)
        intent.putExtra(EXTRA_TELL_QUESTION, tell.question)

        startActivityForResult(intent, REQUEST_REPLY_TELL)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_REPLY_TELL) {
            if (resultCode == RESULT_OK && data != null) {
                val backupTell = data.getParcelableExtra<Tell>(EXTRA_TELL)!!
                val updatedTell = data.getParcelableExtra<Tell>(EXTRA_TELL_UPDATED)!!

                sendReply(updatedTell, backupTell,
                    onSuccess = { inboxViewModel.refreshInbox() },
                    showOnSuccess = { showReplySentSnackbar(backupTell) },
                    showOnError = { showReplyNotSentSnackbar() })
            }

            if (resultCode == RESULT_CANCELED) {
                // TODO
            }
        }
    }

    private fun showReplyNotSentSnackbar() {
        ViewUtils.createSnackbar(mContext, binding.layoutCoordinator, getString(R.string.reply_sent_error)).show()
    }

    private fun showReplySentSnackbar(backupTell: Tell) {
        ViewUtils
            .createSnackbar(mContext, binding.layoutCoordinator, getString(R.string.reply_sent))
            .setAction(getString(R.string.undo)) {

                // when clicked on undo, remove reply
                removeReply(backupTell,
                    onSuccess = { inboxViewModel.refreshInbox() },
                    showOnSuccess = { showReplyRemovedSnackbar() },
                    showOnError = { showReplyNotRemovedSnackbar() })
            }
            .setActionTextColor(ContextCompat.getColor(mContext, R.color.white))
            .show()
    }

    private fun removeReply(
        tell: Tell,
        onSuccess: () -> Unit,
        showOnSuccess: () -> Unit,
        showOnError: () -> Unit
    ) {
        lifecycleScope.launch {
            val result = tellViewModel.updateTell(tell)
            if (result) {
                showOnSuccess()
                onSuccess()
            } else {
                showOnError()
            }
        }
    }

    private fun sendReply(
        tell: Tell,
        backupTell: Tell,
        onSuccess: () -> Unit,
        showOnSuccess: (Tell) -> Unit,
        showOnError: () -> Unit
    ) {
        lifecycleScope.launch {
            val result = tellViewModel.updateTell(tell)
            if (result) {
                showOnSuccess(backupTell)
                onSuccess()
            } else {
                showOnError()
            }
        }
    }

    private fun showReplyNotRemovedSnackbar() {
        ViewUtils
            .createSnackbar(mContext, binding.layoutCoordinator, getString(R.string.reply_removed_error))
            .show()
    }

    private fun showReplyRemovedSnackbar() {
        ViewUtils
            .createSnackbar(mContext, binding.layoutCoordinator, getString(R.string.reply_removed))
            .show()
    }

    private fun setupItemTouchHelper(itemViewAdapter: InboxItemViewAdapter) {
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                viewHolder?.let {
                    val foregroundView = (viewHolder as? (InboxItemViewHolder))?.binding?.viewForeground

                    foregroundView?.let {
                        getDefaultUIUtil().onSelected(foregroundView)
                    }
                }
            }

            override fun onChildDrawOver(
                c: Canvas,
                inboxRecyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder?,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                viewHolder?.let {
                    val foregroundView = (viewHolder as? (InboxItemViewHolder))?.binding?.viewForeground

                    foregroundView?.let {
                        getDefaultUIUtil().onDrawOver(
                            c, inboxRecyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive
                        )
                    }
                }
            }

            override fun clearView(inboxRecyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                val foregroundView = (viewHolder as? (InboxItemViewHolder))?.binding?.viewForeground

                foregroundView?.let {
                    getDefaultUIUtil().clearView(foregroundView)
                }
            }

            override fun onChildDraw(
                c: Canvas,
                inboxRecyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val foregroundView = (viewHolder as? (InboxItemViewHolder))?.binding?.viewForeground

                foregroundView?.let {
                    getDefaultUIUtil().onDraw(
                        c, inboxRecyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive
                    )
                }
            }

            override fun onMove(
                inboxRecyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val deletedPosition = viewHolder.adapterPosition
                val deletedTell = itemViewAdapter.getInboxItemAt(deletedPosition)

                // TODO own method
                lifecycleScope.launch {
                    val result = tellViewModel.deleteTell(deletedTell.id)

                    if (result) {
                        ViewUtils
                            .createSnackbar(mContext, binding.layoutCoordinator, getString(R.string.tell_removed))
                            .setAction(getString(R.string.undo)) {
                                insertTell(deletedTell) {
                                    binding.inboxRecyclerView.adapter?.notifyItemInserted(deletedPosition)
                                }
                            }
                            .setActionTextColor(ContextCompat.getColor(mContext, R.color.white))
                            .show()

                        binding.inboxRecyclerView.adapter?.notifyItemRemoved(deletedPosition)
                    } else {
                        ViewUtils
                            .createSnackbar(
                                mContext,
                                binding.layoutCoordinator,
                                getString(R.string.tell_removed_error)
                            )
                            .show()

                        binding.inboxRecyclerView.adapter?.notifyItemInserted(deletedPosition)
                    }
                }
            }
        }).attachToRecyclerView(binding.inboxRecyclerView)
    }

    private fun insertTell(tell: Tell, callback: () -> Unit) {
        lifecycleScope.launch {
            try {
                tellViewModel.addTell(tell)
                callback()
            } catch (e: IOException) {
                // TODO
            }
        }
    }

    private fun setupTellAdapter(listener: InboxItemViewAdapter.InboxItemClickListener) {
        val viewManager = LinearLayoutManager(activity)
        val viewAdapter = InboxItemViewAdapter(listener)

        // scroll to top when new data arrives
        viewAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                viewManager.smoothScrollToPosition(binding.inboxRecyclerView, null, 0)
            }
        })

        binding.inboxRecyclerView.apply {
            layoutManager = viewManager
            adapter = viewAdapter

            addItemDecoration(DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL))
        }

        inboxViewModel.inbox.observe(viewLifecycleOwner, Observer { inboxItems ->
            binding.progressBar.visibility = View.INVISIBLE

            if (inboxItems.isNotEmpty()) {
                binding.layoutNothingToSee.visibility = View.INVISIBLE
                viewAdapter.submitList(inboxItems.sorted())
            } else {
                binding.layoutNothingToSee.visibility = View.VISIBLE
            }
        })

        setupItemTouchHelper(viewAdapter)
    }
}
