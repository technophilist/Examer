package com.example.examer.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = Blue200,
    primaryVariant = Blue300,
    secondary = Orange300,
    secondaryVariant = Orange300,
    background = Black900,
    surface = Black800,
    error = Red200,
    onPrimary = Black900,
    onSecondary = Black900,
    onBackground = Color.White,
    onSurface = Color.White,
    onError = Black900
)

private val LightColorPalette = lightColors(
    primary = Blue700,
    primaryVariant = Blue800,
    secondary = Orange500,
    secondaryVariant = Orange400,
    background = Blue50,
    surface = Color.White,
    error = Red400,
    onPrimary = Color.White,
    onSecondary = Black900,
    onBackground = Black900,
    onError = Black900,
)

@Composable
fun ExamerTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) DarkColorPalette
    else LightColorPalette
    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}