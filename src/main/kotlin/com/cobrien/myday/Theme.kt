package com.cobrien.myday

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

// Default Blue Theme
val DefaultBlueTheme = lightColorScheme(
    primary = Color(0xFF1976D2),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFBBDEFB),
    onPrimaryContainer = Color(0xFF0D47A1),
    secondary = Color(0xFF42A5F5),
    onSecondary = Color(0xFFFFFFFF),
    tertiary = Color(0xFF64B5F6),
    onTertiary = Color(0xFFFFFFFF),
    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF1A1A1A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1A1A1A),
    surfaceVariant = Color(0xFFE3F2FD),
    onSurfaceVariant = Color(0xFF424242)
)

// Forest Green Theme
val ForestGreenTheme = lightColorScheme(
    primary = Color(0xFF2E7D32),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFC8E6C9),
    onPrimaryContainer = Color(0xFF1B5E20),
    secondary = Color(0xFF66BB6A),
    onSecondary = Color(0xFFFFFFFF),
    tertiary = Color(0xFF81C784),
    onTertiary = Color(0xFFFFFFFF),
    background = Color(0xFFF1F8F1),
    onBackground = Color(0xFF1A1A1A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1A1A1A),
    surfaceVariant = Color(0xFFE8F5E9),
    onSurfaceVariant = Color(0xFF424242)
)

// Deep Purple Theme
val DeepPurpleTheme = lightColorScheme(
    primary = Color(0xFF6200EE),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD1C4E9),
    onPrimaryContainer = Color(0xFF4A148C),
    secondary = Color(0xFF9C27B0),
    onSecondary = Color(0xFFFFFFFF),
    tertiary = Color(0xFFBA68C8),
    onTertiary = Color(0xFFFFFFFF),
    background = Color(0xFFF9F8FB),
    onBackground = Color(0xFF1A1A1A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1A1A1A),
    surfaceVariant = Color(0xFFEDE7F6),
    onSurfaceVariant = Color(0xFF424242)
)

// Ocean Blue Theme
val OceanBlueTheme = lightColorScheme(
    primary = Color(0xFF006494),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFB3D9EC),
    onPrimaryContainer = Color(0xFF003554),
    secondary = Color(0xFF0288D1),
    onSecondary = Color(0xFFFFFFFF),
    tertiary = Color(0xFF29B6F6),
    onTertiary = Color(0xFFFFFFFF),
    background = Color(0xFFF0F7FB),
    onBackground = Color(0xFF1A1A1A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1A1A1A),
    surfaceVariant = Color(0xFFE1F5FE),
    onSurfaceVariant = Color(0xFF424242)
)

// Sunset Orange Theme
val SunsetOrangeTheme = lightColorScheme(
    primary = Color(0xFFE65100),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFCCBC),
    onPrimaryContainer = Color(0xFFBF360C),
    secondary = Color(0xFFFF6F00),
    onSecondary = Color(0xFFFFFFFF),
    tertiary = Color(0xFFFF9800),
    onTertiary = Color(0xFFFFFFFF),
    background = Color(0xFFFFF8F5),
    onBackground = Color(0xFF1A1A1A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1A1A1A),
    surfaceVariant = Color(0xFFFBE9E7),
    onSurfaceVariant = Color(0xFF424242)
)

// Rose Pink Theme
val RosePinkTheme = lightColorScheme(
    primary = Color(0xFFC2185B),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFF8BBD0),
    onPrimaryContainer = Color(0xFF880E4F),
    secondary = Color(0xFFE91E63),
    onSecondary = Color(0xFFFFFFFF),
    tertiary = Color(0xFFF06292),
    onTertiary = Color(0xFFFFFFFF),
    background = Color(0xFFFFF5F7),
    onBackground = Color(0xFF1A1A1A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1A1A1A),
    surfaceVariant = Color(0xFFFCE4EC),
    onSurfaceVariant = Color(0xFF424242)
)

// Teal Mint Theme
val TealMintTheme = lightColorScheme(
    primary = Color(0xFF00796B),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFB2DFDB),
    onPrimaryContainer = Color(0xFF004D40),
    secondary = Color(0xFF26A69A),
    onSecondary = Color(0xFFFFFFFF),
    tertiary = Color(0xFF4DB6AC),
    onTertiary = Color(0xFFFFFFFF),
    background = Color(0xFFF0F9F8),
    onBackground = Color(0xFF1A1A1A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1A1A1A),
    surfaceVariant = Color(0xFFE0F2F1),
    onSurfaceVariant = Color(0xFF424242)
)

// Midnight Dark Theme
val MidnightDarkTheme = darkColorScheme(
    primary = Color(0xFF90CAF9),
    onPrimary = Color(0xFF003258),
    primaryContainer = Color(0xFF004A77),
    onPrimaryContainer = Color(0xFFD1E4FF),
    secondary = Color(0xFFB0BEC5),
    onSecondary = Color(0xFF1B3A4B),
    tertiary = Color(0xFF80DEEA),
    onTertiary = Color(0xFF00363F),
    background = Color(0xFF1A1C1E),
    onBackground = Color(0xFFE3E2E6),
    surface = Color(0xFF1A1C1E),
    onSurface = Color(0xFFE3E2E6),
    surfaceVariant = Color(0xFF42474E),
    onSurfaceVariant = Color(0xFFC2C7CF)
)

val availableThemes = listOf(
    "Default Blue",
    "Forest Green",
    "Deep Purple",
    "Ocean Blue",
    "Sunset Orange",
    "Rose Pink",
    "Teal Mint",
    "Midnight Dark"
)

fun getThemeByName(themeName: String, isDarkMode: Boolean = false): ColorScheme {
    if (themeName == "Midnight Dark") {
        return MidnightDarkTheme
    }

    if (!isDarkMode) {
        return when (themeName) {
            "Default Blue" -> DefaultBlueTheme
            "Forest Green" -> ForestGreenTheme
            "Deep Purple" -> DeepPurpleTheme
            "Ocean Blue" -> OceanBlueTheme
            "Sunset Orange" -> SunsetOrangeTheme
            "Rose Pink" -> RosePinkTheme
            "Teal Mint" -> TealMintTheme
            else -> DefaultBlueTheme
        }
    } else {
        return when (themeName) {
            "Default Blue" -> DefaultBlueDarkTheme
            "Forest Green" -> ForestGreenDarkTheme
            "Deep Purple" -> DeepPurpleDarkTheme
            "Ocean Blue" -> OceanBlueDarkTheme
            "Sunset Orange" -> SunsetOrangeDarkTheme
            "Rose Pink" -> RosePinkDarkTheme
            "Teal Mint" -> TealMintDarkTheme
            else -> DefaultBlueDarkTheme
        }
    }
}

// Dark theme variants
val DefaultBlueDarkTheme = darkColorScheme(
    primary = Color(0xFF90CAF9),
    onPrimary = Color(0xFF003258),
    primaryContainer = Color(0xFF004A77),
    onPrimaryContainer = Color(0xFFD1E4FF),
    secondary = Color(0xFF64B5F6),
    onSecondary = Color(0xFF00344F),
    tertiary = Color(0xFFBBDEFB),
    onTertiary = Color(0xFF001D32),
    background = Color(0xFF1A1C1E),
    onBackground = Color(0xFFE3E2E6),
    surface = Color(0xFF1A1C1E),
    onSurface = Color(0xFFE3E2E6),
    surfaceVariant = Color(0xFF42474E),
    onSurfaceVariant = Color(0xFFC2C7CF)
)

val ForestGreenDarkTheme = darkColorScheme(
    primary = Color(0xFF81C784),
    onPrimary = Color(0xFF003910),
    primaryContainer = Color(0xFF1B5E20),
    onPrimaryContainer = Color(0xFFC8E6C9),
    secondary = Color(0xFFA5D6A7),
    onSecondary = Color(0xFF00390F),
    tertiary = Color(0xFFC8E6C9),
    onTertiary = Color(0xFF00210A),
    background = Color(0xFF1A1C1A),
    onBackground = Color(0xFFE1E3E1),
    surface = Color(0xFF1A1C1A),
    onSurface = Color(0xFFE1E3E1),
    surfaceVariant = Color(0xFF404943),
    onSurfaceVariant = Color(0xFFC0C9C1)
)

val DeepPurpleDarkTheme = darkColorScheme(
    primary = Color(0xFFCE93D8),
    onPrimary = Color(0xFF38006B),
    primaryContainer = Color(0xFF4A148C),
    onPrimaryContainer = Color(0xFFE1BEE7),
    secondary = Color(0xFFBA68C8),
    onSecondary = Color(0xFF31135E),
    tertiary = Color(0xFFE1BEE7),
    onTertiary = Color(0xFF1A0033),
    background = Color(0xFF1C1A1E),
    onBackground = Color(0xFFE6E1E6),
    surface = Color(0xFF1C1A1E),
    onSurface = Color(0xFFE6E1E6),
    surfaceVariant = Color(0xFF49454E),
    onSurfaceVariant = Color(0xFFCAC4CF)
)

val OceanBlueDarkTheme = darkColorScheme(
    primary = Color(0xFF81D4FA),
    onPrimary = Color(0xFF00344D),
    primaryContainer = Color(0xFF003554),
    onPrimaryContainer = Color(0xFFB3E5FC),
    secondary = Color(0xFF4FC3F7),
    onSecondary = Color(0xFF00344A),
    tertiary = Color(0xFFB3E5FC),
    onTertiary = Color(0xFF001F2D),
    background = Color(0xFF191C1D),
    onBackground = Color(0xFFE1E3E4),
    surface = Color(0xFF191C1D),
    onSurface = Color(0xFFE1E3E4),
    surfaceVariant = Color(0xFF40484C),
    onSurfaceVariant = Color(0xFFC0C8CD)
)

val SunsetOrangeDarkTheme = darkColorScheme(
    primary = Color(0xFFFFAB91),
    onPrimary = Color(0xFF5F1900),
    primaryContainer = Color(0xFFBF360C),
    onPrimaryContainer = Color(0xFFFFCCBC),
    secondary = Color(0xFFFFCC80),
    onSecondary = Color(0xFF5C2F00),
    tertiary = Color(0xFFFFE0B2),
    onTertiary = Color(0xFF3E1D00),
    background = Color(0xFF1E1B19),
    onBackground = Color(0xFFE7E2DF),
    surface = Color(0xFF1E1B19),
    onSurface = Color(0xFFE7E2DF),
    surfaceVariant = Color(0xFF51443C),
    onSurfaceVariant = Color(0xFFD5C4B9)
)

val RosePinkDarkTheme = darkColorScheme(
    primary = Color(0xFFF48FB1),
    onPrimary = Color(0xFF5E1133),
    primaryContainer = Color(0xFF880E4F),
    onPrimaryContainer = Color(0xFFFCE4EC),
    secondary = Color(0xFFF8BBD0),
    onSecondary = Color(0xFF5C1130),
    tertiary = Color(0xFFFCE4EC),
    onTertiary = Color(0xFF3B0720),
    background = Color(0xFF1E1A1C),
    onBackground = Color(0xFFEBE0E4),
    surface = Color(0xFF1E1A1C),
    onSurface = Color(0xFFEBE0E4),
    surfaceVariant = Color(0xFF514347),
    onSurfaceVariant = Color(0xFFD6C2C7)
)

val TealMintDarkTheme = darkColorScheme(
    primary = Color(0xFF80CBC4),
    onPrimary = Color(0xFF003731),
    primaryContainer = Color(0xFF004D40),
    onPrimaryContainer = Color(0xFFB2DFDB),
    secondary = Color(0xFFA7FFEB),
    onSecondary = Color(0xFF00382E),
    tertiary = Color(0xFFB2DFDB),
    onTertiary = Color(0xFF002119),
    background = Color(0xFF191C1C),
    onBackground = Color(0xFFE0E3E3),
    surface = Color(0xFF191C1C),
    onSurface = Color(0xFFE0E3E3),
    surfaceVariant = Color(0xFF3F4948),
    onSurfaceVariant = Color(0xFFBFC9C7)
)