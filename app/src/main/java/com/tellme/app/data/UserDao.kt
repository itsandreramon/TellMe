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
import com.tellme.app.model.User
import kotlinx.coroutines.flow.Flow

interface UserDao {

    // Firebase
    fun logout()
    suspend fun register(email: String, password: String): Result<String>
    suspend fun login(email: String, password: String): Result<Boolean>

    fun getCurrentUserFirebase(): FirebaseUser?
    suspend fun sendEmailVerificationFirebase(user: FirebaseUser, language: String): Result<Boolean>
    suspend fun isEmailAlreadyInUseFirebase(email: String): Result<Boolean>
    suspend fun uploadAvatarFirebase(path: Uri, userUid: String): Result<Boolean>
    suspend fun getAvatarFirebase(userUid: String): Result<Uri>

    // Remote
    suspend fun addUserRemote(user: User): Result<Boolean>
    suspend fun getUserByUidRemote(uid: String): Result<User>
    suspend fun getUserByUsernameRemote(username: String): Result<User>
    suspend fun getUsersByQueryRemote(query: String, limit: Int): Result<List<User>>
    suspend fun updateUserRemote(updatedUser: User): Result<Boolean>
    suspend fun deleteUserByUidRemote(uid: String): Result<Boolean>

    // Local
    fun getLatestUserSearchesLocal(): Flow<List<User>>
    fun getUserByUidLocal(uid: String): LiveData<User>
    suspend fun clearLatestUserSearchesLocal()
    suspend fun addUserLocal(user: User)
    suspend fun updateUserLocal(user: User)
    suspend fun invalidateUserCache()
}
