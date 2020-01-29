/*
 * Copyright 2020 - André Ramon Thiele
 *
 * Department of Computer Science and Media
 * University of Applied Sciences Brandenburg
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

    @GET("users/search/{query}/{limit}")
    suspend fun getUsersByQueryRemote(
        @Path("query") query: String,
        @Path("limit") limit: Int
    ): Response<List<User>>

    @PUT("users")
    suspend fun updateUser(
        @Body updatedUser: User
    ): Response<Boolean>

    @DELETE("users/uid/{uid}")
    suspend fun deleteUser(
        @Path("uid") uid: String
    ): Response<Boolean>
}
