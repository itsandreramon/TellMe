/*
 * Copyright 2020 - André Ramon Thiele
 *
 * Department of Computer Science and Media
 * University of Applied Sciences Brandenburg
 */

package com.tellme.app

import com.tellme.app.extensions.isValidUsername
import org.junit.Assert.assertEquals
import org.junit.Test

class UserTest {

    @Test
    fun isValidAlias() {
        val usernameOne = "andre.rxn!"
        val usernameTwo = "andre.rxn"

        val actualOne = usernameOne.isValidUsername()
        val actualTwo = usernameTwo.isValidUsername()

        assertEquals(false, actualOne)
        assertEquals(true, actualTwo)
    }
}
