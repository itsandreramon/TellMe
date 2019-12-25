/*
 * Copyright © 2019 - André Thiele
 *
 * Fachbereich Informatik und Medien
 * Technische Hochschule Brandenburg
 */

package com.tellme.app.util

import java.io.IOException

class UserNotFoundException(m: String) : Exception(m)
class UserNotRegisteredException(m: String) : Exception(m)
class NoConnectivityException : IOException()
