package software.seriouschoi.timeisgold.core.common.ui

import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

/**
 * Created by jhchoi on 2025. 9. 16.
 * jhchoi
 */
@Composable
fun TigTheme(content: @Composable () -> Unit) {
    val colorScheme: ColorScheme = if (isSystemInDarkTheme()) {
        appDarkColorScheme()
    } else {
        appLightColorScheme()
    }

    MaterialTheme(
        colorScheme = colorScheme,
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground
        ) {
            content()
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, name = "Light theme")
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark theme")
annotation class TigThemePreview

private fun appLightColorScheme(): ColorScheme {
    return lightColorScheme(
        primary = LightAppColors.md_theme_light_primary,
        onPrimary = LightAppColors.md_theme_light_onPrimary,
        primaryContainer = LightAppColors.md_theme_light_primaryContainer,
        onPrimaryContainer = LightAppColors.md_theme_light_onPrimaryContainer,
        secondary = LightAppColors.md_theme_light_secondary,
        onSecondary = LightAppColors.md_theme_light_onSecondary,
        secondaryContainer = LightAppColors.md_theme_light_secondaryContainer,
        onSecondaryContainer = LightAppColors.md_theme_light_onSecondaryContainer,
        background = LightAppColors.md_theme_light_background,
        onBackground = LightAppColors.md_theme_light_onBackground,
        surface = LightAppColors.md_theme_light_surface,
        onSurface = LightAppColors.md_theme_light_onSurface,
        error = LightAppColors.md_theme_light_error,
        onError = LightAppColors.md_theme_light_onError
    )
}

private fun appDarkColorScheme(): ColorScheme {
    return darkColorScheme(
        primary = DarkAppColors.md_theme_dark_primary,
        onPrimary = DarkAppColors.md_theme_dark_onPrimary,
        primaryContainer = DarkAppColors.md_theme_dark_primaryContainer,
        onPrimaryContainer = DarkAppColors.md_theme_dark_onPrimaryContainer,
        secondary = DarkAppColors.md_theme_dark_secondary,
        onSecondary = DarkAppColors.md_theme_dark_onSecondary,
        secondaryContainer = DarkAppColors.md_theme_dark_secondaryContainer,
        onSecondaryContainer = DarkAppColors.md_theme_dark_onSecondaryContainer,
        background = DarkAppColors.md_theme_dark_background,
        onBackground = DarkAppColors.md_theme_dark_onBackground,
        surface = DarkAppColors.md_theme_dark_surface,
        onSurface = DarkAppColors.md_theme_dark_onSurface,
        error = DarkAppColors.md_theme_dark_error,
        onError = DarkAppColors.md_theme_dark_onError
    )
}

private fun appTestColorScheme(): ColorScheme {
    return darkColorScheme(
        primary = Color.Red,
        onPrimary = Color.Red,
        primaryContainer = Color.Red,
        onPrimaryContainer = Color.Red,
        secondary = Color.Red,
        onSecondary = Color.Red,
        secondaryContainer = Color.Red,
        onSecondaryContainer = Color.Red,
        background = Color.Red,
        onBackground = Color.Red,
        surface = DarkAppColors.md_theme_dark_surface,
        onSurface = DarkAppColors.md_theme_dark_onSurface,
        error = DarkAppColors.md_theme_dark_error,
        onError = DarkAppColors.md_theme_dark_onError
    )
}

