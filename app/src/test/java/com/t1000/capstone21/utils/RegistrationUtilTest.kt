package com.t1000.capstone21.utils

import org.junit.Assert.*
import org.junit.Test

class RegistrationUtilTest{

    @Test
    fun `empty username returns false`() {
        val result = RegistrationUtil.validateRegistrationInput(
            "",
            "123",
        )
        assertFalse(result)
    }


    @Test
    fun `empty password returns false`() {
        val result = RegistrationUtil.validateRegistrationInput(
            "Eman",
            ""
        )
        assertFalse(result)
    }

    @Test
    fun `less than 6 digit password returns false`() {
        val result = RegistrationUtil.validateRegistrationInput(
            "Eman",
            "342",
        )
        assertFalse(result)
    }
}