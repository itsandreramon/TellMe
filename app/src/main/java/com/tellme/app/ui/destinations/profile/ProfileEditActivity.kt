/*
 * Copyright 2020 - André Ramon Thiele
 *
 * Department of Computer Science and Media
 * University of Applied Sciences Brandenburg
 */

package com.tellme.app.ui.destinations.profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.tellme.R
import com.tellme.app.dagger.inject
import com.tellme.app.data.Result
import com.tellme.app.extensions.setUserProfileImageFromPath
import com.tellme.app.util.DialogUtils
import com.tellme.app.util.PICK_IMAGE_REQUEST_CODE
import com.tellme.app.util.ViewUtils
import com.tellme.app.viewmodels.main.UserViewModel
import com.tellme.databinding.ActivityProfileEditBinding
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.launch
import timber.log.Timber

class ProfileEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileEditBinding

    private var loadingDialog: AlertDialog? = null
    private var updatedProfileImage: String? = null

    @Inject lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        inject(this)

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile_edit)

        setupToolbar()
        setupUser()

        binding.textViewChangeProfileImage.setOnClickListener { chooseImage() }
        binding.imageViewUserAvatar.setOnClickListener { chooseImage() }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)

        supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_close)
        }
    }

    private fun setupUser() {
        userViewModel.loggedInUser.observe(this, Observer { result ->
            when (result) {
                is Result.Success -> {
                    binding.user = result.data
                    binding.imageViewUserAvatar.setUserProfileImageFromPath(result.data.avatar)
                }
                is Result.Error -> userViewModel.logout()
            }
        })
    }

    private fun chooseImage() {
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }

        startActivityForResult(
            Intent.createChooser(intent, "select image"),
            PICK_IMAGE_REQUEST_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val filePath = data?.data
            try {
                filePath?.let { path ->
                    updatedProfileImage = path.toString()
                    binding.imageViewUserAvatar.setUserProfileImageFromPath(path.toString())
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun saveChanges(callback: () -> Unit) {
        val name = binding.editTextName.text.toString().trim()
        val username = binding.editTextUsername.text.toString().trim()
        val about = binding.editTextAbout.text.toString().trim()
        val profileImage = updatedProfileImage

        when {
            name.isEmpty() -> ViewUtils.showToast(this@ProfileEditActivity, "Name cannot be empty")
            username.isEmpty() -> ViewUtils.showToast(this@ProfileEditActivity, "Username cannot be empty")
            usernameIsInUse(username) -> DialogUtils.createUsernameAlreadyInUseDialog(this).show()
            else -> {
                loadingDialog = DialogUtils.createLoadingDialog(this)
                    .also { it.show() }

                var exceptionThrown = false
                try {
                    Timber.e("Updating user...")
                    updateUser(username, profileImage, name, about)
                } catch (e: Exception) {
                    e.printStackTrace()
                    exceptionThrown = true
                }

                loadingDialog?.dismiss()

                if (exceptionThrown) {
                    DialogUtils.createUpdateErrorDialog(this).show()
                } else {
                    setResult(Activity.RESULT_OK)
                    callback()
                }
            }
        }
    }

    private suspend fun usernameIsInUse(username: String): Boolean {
        return when (val user = userViewModel.loggedInUser.value) {
            is Result.Success -> userViewModel.isUsernameAlreadyInUse(username) && username != user.data.username
            else -> {
                ViewUtils.showToast(this, "Error checking username availability.")
                true
            }
        }
    }

    private suspend fun updateUser(
        username: String,
        avatar: String?,
        displayName: String,
        about: String
    ) = when (avatar != null) {
        true -> handleProfileUpdated(username, displayName, about, updateAvatar(avatar))
        false -> handleProfileUpdated(username, displayName, about)
    }

    private suspend fun handleProfileUpdated(
        username: String,
        displayName: String,
        about: String,
        avatar: String? = null
    ) {
        val loggedInUser = userViewModel.loggedInUser.value!!

        when (loggedInUser) {
            is Result.Success -> {
                val updatedUser = loggedInUser.data.copy(
                    username = username,
                    name = displayName,
                    about = about,
                    avatar = avatar ?: loggedInUser.data.avatar
                )

                userViewModel.updateUser(updatedUser)
            }

            is Result.Error -> {
                ViewUtils.showToast(this, "Error updating profile.")
            }
        }
    }

    private suspend fun updateAvatar(photoUrl: String): String? {
        val currentUserUid = userViewModel.getCurrentUserFirebase()!!.uid
        userViewModel.uploadAvatarFirebase(photoUrl, currentUserUid)
        return userViewModel.getAvatarFirebase(currentUserUid).toString()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_profile_edit_options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.profileFragment -> {
                lifecycleScope.launch { saveChanges { finish() } }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
