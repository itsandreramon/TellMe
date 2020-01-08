/*
 * Copyright 2020 - André Thiele
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
        Timber.e("avatar has not been changed")

        val loggedInUser = userViewModel.loggedInUser.value!!

        val updatedUser = loggedInUser.copy(
            username = username,
            name = displayName,
            about = about
        )

        Timber.d(updatedUser.toString())

        userViewModel.updateUser(updatedUser)
    }

    private suspend fun handleProfileUpdatedWithAvatar(
        avatar: String,
        username: String,
        displayName: String,
        about: String
    ) {
        Timber.e("avatar has been changed")

        val loggedInUser = userViewModel.loggedInUser.value!!
        val uploadedAvatar = updateAvatar(avatar)

        Timber.d(loggedInUser.toString())

        val updatedUser = loggedInUser.copy(
            username = username,
            avatar = uploadedAvatar,
            name = displayName,
            about = about
        )

        Timber.e(updatedUser.toString())

        userViewModel.updateUser(updatedUser)
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
