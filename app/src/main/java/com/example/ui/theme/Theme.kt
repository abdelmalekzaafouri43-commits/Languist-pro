package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  darkColorScheme(
      primary = IndigoPrimary,
      onPrimary = Color.White,
      primaryContainer = IndigoContainer,
      onPrimaryContainer = TextPrimary,
      secondary = OrangeAccent,
      tertiary = GreenAccent,
      background = DarkBackground,
      onBackground = TextPrimary,
      surface = DarkSurface,
      onSurface = TextPrimary,
      surfaceVariant = DarkCard,
      onSurfaceVariant = TextSecondary,
      outline = TextMuted
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  MaterialTheme(colorScheme = DarkColorScheme, typography = Typography, content = content)
}
