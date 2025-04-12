package com.example.astrotrack.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

val AstroBlue = Color(0xFF3E539C)
val AstroBlueLight = Color(0xFFDEE5FA)
val AstroGray = Color(0xFF2C2C2C)
val BrightTextColor = Color.White
val DarkBackground = Color(0xFF121212)
val DarkSurface = Color(0xFF1C1C1C)

//  Custom app-wide extra colors
data class CustomColors(
    val drawerBackground: Color
)

val LocalCustomColors = staticCompositionLocalOf {
    CustomColors(
        drawerBackground = AstroBlue // default fallback
    )
}

private val DarkColorScheme = darkColorScheme(
    primary = AstroGray,
    secondary = AstroGray,
    tertiary = AstroGray,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = BrightTextColor,
    onSecondary = BrightTextColor,
    onTertiary = BrightTextColor,
    onBackground = BrightTextColor,
    onSurface = BrightTextColor
)

private val LightColorScheme = lightColorScheme(
    primary = AstroBlue,
    secondary = AstroBlue,
    tertiary = AstroBlue,
    background = Color(0xFFF8F9FA),
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black
)

@Composable
fun AstroTrackTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val customColors = if (darkTheme) {
        CustomColors(drawerBackground = AstroGray)
    } else {
        CustomColors(drawerBackground = AstroBlue)
    }

    CompositionLocalProvider(LocalCustomColors provides customColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
