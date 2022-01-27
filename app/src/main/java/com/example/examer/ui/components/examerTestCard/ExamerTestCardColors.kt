package com.example.examer.ui.components.examerTestCard

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.examer.ui.theme.*

/**
 * A data class that is used to hold all colors related to the **test
 * status** of an instance of [com.example.examer.data.domain.TestDetails]
 * class.
 */
data class StatusColors(
    val open: Color,
    val scheduled: Color,
    val missed: Color,
    val completed: Color
)

/**
 * A data class that used to hold the [takeTestButtonColor] and
 * [statusColors] related to [ExamerExpandableTestCard].
 */
data class ExamerTestCardColors(
    val takeTestButtonColor: Color,
    val statusColors: StatusColors
) {
    companion object {
        /**
         * Instance of [StatusColors] containing the colors
         * to be used in dark mode.
         */
        private val darkStatusColors: StatusColors
            @Composable get() = StatusColors(
                open = Orange300,
                scheduled = Blue200,
                missed = MaterialTheme.colors.error,
                completed = Green600
            )

        /**
         * Instance of [StatusColors] containing the colors
         * to be used in light mode.
         */
        private val lightStatusColors: StatusColors
            @Composable get() = StatusColors(
                open = Orange500,
                scheduled = Blue700,
                missed = MaterialTheme.colors.error,
                completed = Green200
            )

        /**
         * Instance of [ExamerTestCardColors] containing the colors
         * to be used in dark mode.
         */
        val darkExamerTestCardColors: ExamerTestCardColors
            @Composable get() = ExamerTestCardColors(
                takeTestButtonColor = Green600,
                statusColors = darkStatusColors,
            )

        /**
         * Instance of [ExamerTestCardColors] containing the colors
         * to be used in light mode.
         */
        val lightExamerTestCardColors: ExamerTestCardColors
            @Composable get() = ExamerTestCardColors(
                takeTestButtonColor = Green200,
                statusColors = lightStatusColors,
            )
    }
}