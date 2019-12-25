/*
 * Copyright © 2019 - André Thiele
 *
 * Fachbereich Informatik und Medien
 * Technische Hochschule Brandenburg
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
import com.tellme.app.extensions.setUserProfileImageFromPath
import com.tellme.app.util.PICK_IMAGE_REQUEST_CODE
import com.tellme.app.util.ViewUtils
import com.tellme.app.viewmodels.main.UserViewModel
import com.tellme.databinding.ActivityProfileEditBinding
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

class ProfileEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileEditBinding

    private var dialog: AlertDialog? = null
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
        userViewModel.loggedInUser.observe(this, Observer { user ->
            binding.user = user
            binding.imageViewUserAvatar.setUserProfileImageFromPath(user.avatar)
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
        val profileImage = updatedProfileImage ?: userViewModel.loggedInUser.value!!.avatar

        when {
            name.isEmpty() -> ViewUtils.showToast(this@ProfileEditActivity, "Display name cannot be empty")
            username.isEmpty() -> ViewUtils.showToast(this@ProfileEditActivity, "Username cannot be empty")
            usernameIsInUse(username) -> showUsernameAlreadyInUseDialog()
            else -> {
                showLoadingDialog()

                var exceptionThrown = false
                try {
                    updateUser(username, profileImage, name, about)
                } catch (e: Exception) {
                    e.printStackTrace()
                    exceptionThrown = true
                }

                dismissLoadingDialog()

                if (exceptionThrown) {
                    showUpdateErrorDialog()
                } else {
                    setResult(Activity.RESULT_OK)
                    callback()
                }
            }
        }
    }

    private suspend fun usernameIsInUse(username: String): Boolean {
        return userViewModel.isUsernameAlreadyInUse(username) &&
                username != userViewModel.loggedInUser.value!!.username
    }

    private suspend fun updateUser(
        username: String,
        avatar: String?,
        displayName: String,
        about: String
    ) = when (avatar != null) {
        true -> handleProfileUpdatedWithAvatar(avatar, username, displayName, about)
        false -> handleProfileUpdatedWithoutAvatar(username, displayName, about)
    }

    private suspend fun handleProfileUpdatedWithoutAvatar(
        username: String,
        displayName: String,
        about: String
    ) {
        val loggedInUser = userViewModel.loggedInUser.value!!

        val updatedUser = loggedInUser.copy(
            username = username,
            name = displayName,
            about = about
        )

        userViewModel.updateUser(updatedUser)
    }

    private suspend fun handleProfileUpdatedWithAvatar(
        avatar: String,
        username: String,
        displayName: String,
        about: String
    ) {
        val loggedInUser = userViewModel.loggedInUser.value!!
        val uploadedAvatar = updateAvatar(avatar)

        val updatedUser = loggedInUser.copy(
            username = username,
            avatar = uploadedAvatar,
            name = displayName,
            about = about
        )

        userViewModel.updateUser(updatedUser)
    }

    private suspend fun updateAvatar(photoUrl: String): String? {
        val currentUserUid = userViewModel.getCurrentUserFirebase()!!.uid
        userViewModel.uploadAvatarFirebase(photoUrl, currentUserUid)
        return userViewModel.getAvatarFirebase(currentUserUid).toString()
    }

    private fun showUsernameAlreadyInUseDialog() {
        ViewUtils.createInfoAlertDialog(
            this,
            message = getString(R.string.register_error_username_in_use),
            title = getString(R.string.update),
            negative = getString(R.string.ok)
        ).show()
    }

    private fun showUpdateErrorDialog() {
        ViewUtils.createInfoAlertDialog(
            this,
            message = getString(R.string.error_updating),
            title = getString(R.string.update),
            negative = getString(R.string.ok)
        ).show()
    }

    private fun showLoadingDialog() {
        dialog = ViewUtils
            .createNonCancellableDialog(this, R.layout.dialog_updating)
            .also { it.show() }
    }

    private fun dismissLoadingDialog() {
        dialog?.dismiss()
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
