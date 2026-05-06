package com.example.chefconnect.ui.theme

import android.app.Activity
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Paleta de colores extraída del Mockup
val PrimaryGreen = Color(0xFF4CAF50)
val DarkGreen = Color(0xFF2E7D32)
val LightGreen = Color(0xFFE8F5E9)
val BackgroundColor = Color(0xFFFAFAFA)
val SurfaceColor = Color(0xFFFFFFFF)
val TextDark = Color(0xFF1E1E1E)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    secondary = DarkGreen,
    tertiary = LightGreen,
    background = BackgroundColor,
    surface = SurfaceColor,
    onPrimary = Color.White,
    onBackground = TextDark,
    onSurface = TextDark
)

@Composable
fun ChefConnectTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = BackgroundColor.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}