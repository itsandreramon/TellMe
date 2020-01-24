/*
 * Copyright 2020 - André Ramon Thiele
 *
 * Department of Computer Science and Media
 * University of Applied Sciences Brandenburg
 */

package com.tellme.app.data

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GetTokenResult
import com.google.firebase.auth.SignInMethodQueryResult
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask

class FirebaseSource {

    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firebaseStorage: FirebaseStorage by lazy { FirebaseStorage.getInstance() }

    fun login(email: String, password: String): Task<AuthResult> {
        return firebaseAuth.signInWithEmailAndPassword(email, password)
    }

    fun register(email: String, password: String): Task<AuthResult> {
        return firebaseAuth.createUserWithEmailAndPassword(email, password)
    }

    fun logout() {
        return firebaseAuth.signOut()
    }

    fun retrieveIdToken(firebaseUser: FirebaseUser): Task<GetTokenResult> {
        return firebaseUser.getIdToken(true)
    }

    fun sendEmailVerification(firebaseUser: FirebaseUser, language: String): Task<Void> {
        firebaseAuth.setLanguageCode(language)
        return firebaseUser.sendEmailVerification()
    }

    fun isEmailAlreadyInUse(email: String): Task<SignInMethodQueryResult> {
        return firebaseAuth.fetchSignInMethodsForEmail(email)
    }

    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    fun uploadAvatar(path: Uri, userUid: String): UploadTask {
        return firebaseStorage.reference.child("images/$userUid/avatar").putFile(path)
    }

    fun getAvatar(userUid: String): Task<Uri> {
        return firebaseStorage.reference.child("images/$userUid/avatar").downloadUrl
    }

    fun updateUserProfile(profile: UserProfileChangeRequest): Task<Void>? {
        return firebaseAuth.currentUser?.updateProfile(profile)
    }
}
