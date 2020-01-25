/*
 * Copyright 2020 - André Ramon Thiele
 *
 * Department of Computer Science and Media
 * University of Applied Sciences Brandenburg
 */

package com.tellme.app.util

import java.io.IOException

class UserNotFoundException(m: String) : Exception(m)
class UserNotRegisteredException(m: String) : Exception(m)
class NoConnectivityException : IOException()
