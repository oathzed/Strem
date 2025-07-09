package com.strem.app.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFE50914),
    onPrimary = Color.White,
    secondary = Color(0xFFB20710),
    onSecondary = Color.White,
    background = Color(0xFF141414),
    onBackground = Color.White,
    surface = Color(0xFF1F1F1F),
    onSurface = Color.White
)

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun StremTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}