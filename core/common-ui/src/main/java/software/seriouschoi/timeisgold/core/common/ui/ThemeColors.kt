package software.seriouschoi.timeisgold.core.common.ui

import androidx.compose.ui.graphics.Color

internal object ThemeColors {
    // 🎨 Racing Green 기반 컬러
    val racingGreen = Color(0xFF004225)
    val racingGreenLight = Color(0xFF006D3E) // 밝은 톤
    val racingGreenDark = Color(0xFF00301A) // 더 어두운 톤
}

internal object DarkAppColors {
    // Dark Mode
    val md_theme_dark_primary = ThemeColors.racingGreenLight
    val md_theme_dark_onPrimary = Color.Companion.White
    val md_theme_dark_primaryContainer = ThemeColors.racingGreen
    val md_theme_dark_onPrimaryContainer = Color.Companion.White

    val md_theme_dark_secondary = Color(0xFF80E27E)
    val md_theme_dark_onSecondary = Color.Companion.Black
    val md_theme_dark_secondaryContainer = Color(0xFF1B5E20)
    val md_theme_dark_onSecondaryContainer = Color(0xFFC8E6C9)

    val md_theme_dark_tertiary = Color(0xFF388E3C)
    val md_theme_dark_onTertiary = Color.Companion.White

    val md_theme_dark_background = Color(0xFF101010)
    val md_theme_dark_onBackground = Color(0xFFE0E0E0)

    val md_theme_dark_surface = Color(0xFF101010)
    val md_theme_dark_onSurface = Color(0xFFE0E0E0)

    val md_theme_dark_error = Color(0xFFCF6679)
    val md_theme_dark_onError = Color.Companion.Black
}

internal object LightAppColors {
    val md_theme_light_primary = ThemeColors.racingGreen
    val md_theme_light_onPrimary = Color.Companion.White
    val md_theme_light_primaryContainer = ThemeColors.racingGreenLight
    val md_theme_light_onPrimaryContainer = Color.Companion.White

    val md_theme_light_secondary = Color(0xFF4CAF50)  // 보조색: 녹색 계열 (살짝 밝게)
    val md_theme_light_onSecondary = Color.Companion.White
    val md_theme_light_secondaryContainer = Color(0xFFC8E6C9)
    val md_theme_light_onSecondaryContainer = Color(0xFF1B5E20)

    val md_theme_light_tertiary = Color(0xFF81C784) // 추가 포인트
    val md_theme_light_onTertiary = Color.Companion.Black

    val md_theme_light_background = Color(0xFFFDFDFD)
    val md_theme_light_onBackground = Color(0xFF101010)

    val md_theme_light_surface = Color.Companion.White
    val md_theme_light_onSurface = Color(0xFF101010)

    val md_theme_light_error = Color(0xFFBA1A1A)
    val md_theme_light_onError = Color.Companion.White


}