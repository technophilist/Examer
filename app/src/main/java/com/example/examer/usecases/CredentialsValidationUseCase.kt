package com.example.examer.usecases

import android.util.Patterns
import com.example.examer.utils.containsDigit
import com.example.examer.utils.containsLowercase
import com.example.examer.utils.containsUppercase

/**
 * An interface that contains the requisite methods required
 * for a concrete implementation of [CredentialsValidationUseCase].
 */
interface CredentialsValidationUseCase {
    fun isValidEmail(email: String): Boolean
    fun isValidPassword(password: String): Boolean
}

/**
 * A concrete implementation of [CredentialsValidationUseCase].
 */
class ExamerCredentialsValidationUseCase : CredentialsValidationUseCase {
    /**
     * The method is used to check whether the [email] is valid.
     * An email is valid if, and only if, it is not blank(ie. is
     * not empty and doesn't contain whitespace characters)
     * and matches the [Patterns.EMAIL_ADDRESS] regex.
     */
    override fun isValidEmail(email: String): Boolean =
        email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()

    /**
     * The method is used to check whether the [password] is valid.
     * A password is valid if, and only if,it is of length 8 , contains
     * at least one uppercase and lowercase letter and contains at least
     * one digit.
     */
    override fun isValidPassword(password: String): Boolean =
        password.length >= 8 && password.containsUppercase() && password.containsLowercase() && password.containsDigit()
}