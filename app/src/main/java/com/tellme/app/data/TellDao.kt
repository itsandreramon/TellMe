/*
 * Copyright 2020 - André Ramon Thiele
 *
 * Department of Computer Science and Media
 * University of Applied Sciences Brandenburg
 */

package com.tellme.app.data

import com.tellme.app.model.Tell

interface TellDao {
    suspend fun getTellByIdRemote(id: String): Result<Tell>
    suspend fun deleteTellRemote(id: String): Result<Boolean>
    suspend fun insertTellRemote(tell: Tell): Result<Boolean>
    suspend fun updateTellRemote(tell: Tell): Result<Boolean>
    suspend fun findTellsByReceiverUid(uid: String): Result<List<Tell>>
}
