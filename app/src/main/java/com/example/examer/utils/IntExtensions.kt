package com.example.examer.utils


/**
 * Returns a [Boolean] indicating whether an integer has the ten's
 * place.
 */
fun Int.isSingleDigit(): Boolean = this / 10 == 0

/**
 * Returns a string with a zero appended at the beginning if
 * the integer [isSingleDigit] and also [appendZeroIfSingleDigit]
 * is set to true. Else it returns the default [Int.toString].
 */
fun Int.toString(appendZeroIfSingleDigit: Boolean): String =
    if (this.isSingleDigit() && appendZeroIfSingleDigit) "0$this" else this.toString()