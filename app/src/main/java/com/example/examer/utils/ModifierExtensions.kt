package com.example.examer.utils

import androidx.compose.ui.Modifier

/**
 * An extension function that can be used to conditionally chain a
 * modifier.
 *
 * @param condition the condition based on which the modifier will be
 * chained/not chained.
 * @param modifierScope a lambda with an instance of [Modifier] as receiver.
 */
fun Modifier.conditional(
    condition: Boolean,
    modifierScope: Modifier.() -> Modifier,
): Modifier = if (condition) this.then(modifierScope()) else this