/*
 * Copyright 2020 - André Ramon Thiele
 *
 * Department of Computer Science and Media
 * University of Applied Sciences Brandenburg
 */

package com.tellme.app.data

import android.net.Uri
import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseUser
import com.tellme.app.data.api.UserService
import com.tellme.app.data.database.UserRoomDao
import com.tellme.app.model.User
import com.tellme.app.util.UserNotFoundException
import com.tellme.app.util.UserNotRegisteredException
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.suspendCancellableCoroutine

class UserRepository private constructor(
    private val firebaseSource: FirebaseSource,
    private val userService: UserService,
    private val roomDao: UserRoomDao
) : UserDao {

    override suspend fun updateUserLocal(user: User) {
        roomDao.updateUser(user)
    }

    override suspend fun addUserLocal(user: User) {
        roomDao.insertUser(user)
    }

    override fun getUserByUidLocal(uid: String): LiveData<User> {
        return roomDao.getUserByUid(uid)
    }

    override fun getLatestUserSearchesLocal(): Flow<List<User>> {
        return roomDao.getLatestUserSearches()
    }

    override suspend fun clearLatestUserSearchesLocal() {
        roomDao.clearLatestUserSearches()
    }

    override suspend fun invalidateUserCache() {
        roomDao.deleteUsers()
    }

    override suspend fun getUserByUsernameRemote(username: String): Result<User> {
        return try {
            val response = userService.getUserByUsername(username)
            getResult(response = response, onError = {
                if (response.code() == 404) {
                    throw UserNotFoundException("User could not be found: ${response.code()} ${response.message()}")
                } else {
                    throw IOException("Error getting user by username: ${response.code()} ${response.message()}")
                }
            })
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getUserByUidRemote(uid: String): Result<User> {
        return try {
            val response = userService.getUserByUid(uid)
            getResult(response = response, onError = {
                throw IOException("Error getting user by uid: ${response.code()} ${response.message()}")
            })
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun addUserRemote(user: User): Result<Boolean> {
        return try {
            val response = userService.addUser(user)
            getResult(response = response, onError = {
                throw IOException("Error adding user to database: ${response.code()} ${response.message()}")
            })
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getUsersByQueryRemote(query: String, limit: Int): Result<List<User>> {
        return try {
            val response = userService.getUsersByQueryRemote(query, limit)
            getResult(response = response, onError = {
                throw IOException("Error getting users by query: ${response.code()} ${response.message()}")
            })
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateUserRemote(updatedUser: User): Result<Boolean> {
        return try {
            val response = userService.updateUser(updatedUser)
            getResult(response = response, onError = {
                throw IOException("Error updating user in Firestore: ${response.code()} ${response.message()}")
            })
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deleteUserByUidRemote(uid: String): Result<Boolean> {
        return try {
            val response = userService.deleteUser(uid)
            getResult(response = response, onError = {
                throw IOException("Error deleting auth user record in Firebase: ${response.code()} ${response.message()}")
            })
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun sendEmailVerificationFirebase(user: FirebaseUser, language: String): Result<Boolean> {
        return suspendCoroutine { cont ->
            firebaseSource.sendEmailVerification(user, language).apply {
                addOnSuccessListener { cont.resume(Result.Success(true)) }
                addOnFailureListener { e -> cont.resume(Result.Error(e)) }
            }
        }
    }

    override suspend fun isEmailAlreadyInUseFirebase(email: String): Result<Boolean> {
        return suspendCancellableCoroutine { cont ->
            firebaseSource.isEmailAlreadyInUse(email).apply {
                addOnSuccessListener { result ->
                    val signInMethods = result.signInMethods
                    if (signInMethods != null) {
                        cont.resume(Result.Success(signInMethods.isNotEmpty()))
                    } else {
                        cont.resume(Result.Error(IOException("An error occurred. Sign in methods are null.")))
                    }
                }
                addOnFailureListener { e -> cont.resume(Result.Error(e)) }
            }
        }
    }

    override suspend fun uploadAvatarFirebase(path: Uri, userUid: String): Result<Boolean> {
        return suspendCancellableCoroutine { cont ->
            firebaseSource.uploadAvatar(path, userUid).apply {
                addOnSuccessListener { cont.resume(Result.Success(true)) }
                addOnFailureListener { e -> cont.resume(Result.Error(e)) }
            }
        }
    }

    override suspend fun getAvatarFirebase(userUid: String): Result<Uri> {
        return suspendCancellableCoroutine { cont ->
            firebaseSource.getAvatar(userUid).apply {
                addOnSuccessListener { result -> cont.resume(Result.Success(result)) }
                addOnFailureListener { e -> cont.resume(Result.Error(e)) }
            }
        }
    }

    override suspend fun login(email: String, password: String): Result<Boolean> {
        return suspendCancellableCoroutine { cont ->
            firebaseSource.login(email, password).apply {
                addOnSuccessListener { cont.resume(Result.Success(true)) }
                addOnFailureListener { e -> cont.resume(Result.Error(e)) }
            }
        }
    }

    override suspend fun register(email: String, password: String): Result<String> {
        return suspendCancellableCoroutine { cont ->
            firebaseSource.register(email, password).apply {
                addOnSuccessListener {

                    // retrieve uid from newly registered user
                    val uid = result?.user?.uid

                    if (uid != null) {
                        cont.resume(Result.Success(uid))
                    } else {
                        cont.resume(Result.Error(UserNotRegisteredException("Error registering user: uid was null.")))
                    }
                }
                addOnFailureListener { e -> cont.resume(Result.Error(e)) }
            }
        }
    }

    override fun getCurrentUserFirebase(): FirebaseUser? {
        return firebaseSource.getCurrentUser()
    }

    override fun logout() {
        return firebaseSource.logout()
    }

    companion object {

        @Volatile private var instance: UserRepository? = null

        fun getInstance(
            firebaseSource: FirebaseSource,
            userService: UserService,
            roomDao: UserRoomDao
        ) = instance
            ?: UserRepository(
                firebaseSource,
                userService,
                roomDao
            ).also { instance = it }
    }
}
