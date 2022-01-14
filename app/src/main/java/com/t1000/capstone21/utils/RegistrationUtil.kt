package com.t1000.capstone21.utils

object RegistrationUtil {

    fun validateRegistrationInput(
        username: String,
        password: String
    ): Boolean {
        if(username.isEmpty() || password.isEmpty()) {
            return false
        }

        if(password.count { it.isDigit() } < 6) {
            return false
        }
        return true
    }
}