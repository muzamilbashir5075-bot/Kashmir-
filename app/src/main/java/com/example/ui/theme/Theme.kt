package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
    darkColorScheme(
        primary = KashmiriCrimsonDark,
        onPrimary = Color.White,
        secondary = KashmiriGold,
        onSecondary = Color.Black,
        tertiary = ForestGreen,
        background = DarkBackground,
        surface = DarkSurface,
        onBackground = TextLight,
        onSurface = TextLight
    )

private val LightColorScheme =
    lightColorScheme(
        primary = KashmiriCrimson,
        onPrimary = Color.White,
        secondary = KashmiriGold,
        onSecondary = Color.Black,
        tertiary = ForestGreen,
        background = WarmCream,
        surface = WarmSurface,
        onBackground = TextDark,
        onSurface = TextDark
    )

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // false to preserve Kashmiri rich palette
    content: @Composable () -> Unit,
) {
    val colorScheme =
        when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }
            darkTheme -> DarkColorScheme
            else -> LightColorScheme
        }

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
