/*
 * Copyright 2020 - André Thiele
 *
 * Fachbereich Informatik und Medien
 * Technische Hochschule Brandenburg
 */

package com.tellme.app.data.api

import com.tellme.app.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserService {

    @POST("users")
    suspend fun addUser(
        @Body user: User
    ): Response<Boolean>

    @GET("users/uid/{uid}")
    suspend fun getUserByUid(
        @Path("uid") uid: String
    ): Response<User>

    @GET("users/username/{username}")
    suspend fun getUserByUsername(
        @Path("username") username: String
    ): Response<User>

    @GET("users/username/{query}/{limit}")
    suspend fun getUsersByQueryRemote(
        @Path("query") query: String,
        @Path("limit") limit: Int
    ): Response<List<User>>

    @GET("users/uid/{uid}/follows")
    suspend fun getFollowsByUid(
        @Path("uid") uid: String
    ): Response<List<User>>

    @PUT("users")
    suspend fun updateUser(
        @Body updatedUser: User
    ): Response<Boolean>

    @POST("users/{uid}/follow/{uidToFollow}")
    suspend fun followUserByUid(
        @Path("uid") uid: String,
        @Path("uidToFollow") uidToFollow: String
    ): Response<Boolean>

    @POST("users/{uid}/unfollow/{uidToUnfollow}")
    suspend fun unfollowUserByUid(
        @Path("uid") uid: String,
        @Path("uidToUnfollow") uidToUnfollow: String
    ): Response<Boolean>

    @DELETE("users/uid/{uid}")
    suspend fun deleteUser(
        @Path("uid") uid: String
    ): Response<Boolean>
}
