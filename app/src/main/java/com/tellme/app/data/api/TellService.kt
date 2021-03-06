/*
 * Copyright 2020 - André Ramon Thiele
 *
 * Department of Computer Science and Media
 * University of Applied Sciences Brandenburg
 */

package com.tellme.app.data.api

import com.tellme.app.model.FeedItem
import com.tellme.app.model.ReplyItem
import com.tellme.app.model.Tell
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface TellService {

    @POST("tells")
    suspend fun addTell(
        @Body tell: Tell
    ): Response<Boolean>

    @GET("tells/id/{id}")
    suspend fun getTellById(
        @Path("id") id: String
    ): Response<Tell>

    @PUT("tells")
    suspend fun updateTell(
        @Body tell: Tell
    ): Response<Boolean>

    @DELETE("tells/id/{id}")
    suspend fun deleteTell(
        @Path("id") id: String
    ): Response<Boolean>

    // TODO Move to UserService
    @GET("users/uid/{uid}/inbox")
    suspend fun getInboxByUser(
        @Path("uid") uid: String
    ): Response<List<Tell>>

    // TODO Move to UserService
    @GET("users/uid/{uid}/feed")
    suspend fun getFeedByUser(
        @Path("uid") uid: String
    ): Response<List<FeedItem>>

    @GET("tells/receiver/{uid}")
    suspend fun findTellsByReceiverUid(
        @Path("uid") uid: String
    ): Response<List<Tell>>

    @GET("users/uid/{uid}/replies")
    suspend fun getRepliesByUidRemote(
        @Path("uid") uid: String
    ): Response<List<ReplyItem>>
}
