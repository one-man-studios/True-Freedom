package com.weaponized.recovery.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val WeaponizedColors = darkColorScheme(
    primary = BloodBright,
    onPrimary = TextPrimary,
    primaryContainer = BloodDeep,
    onPrimaryContainer = TextSoft,
    secondary = AmberBright,
    onSecondary = BgDeep,
    secondaryContainer = Color(0xFF804000),
    onSecondaryContainer = TextSoft,
    tertiary = SurvBright,
    onTertiary = BgDeep,
    tertiaryContainer = SurvDim,
    onTertiaryContainer = TextSoft,
    background = Bg,
    onBackground = TextPrimary,
    surface = Surface,
    onSurface = TextPrimary,
    surfaceVariant = Surface2,
    onSurfaceVariant = TextSoft,
    surfaceTint = BloodBright,
    outline = LineStrong,
    outlineVariant = LineSoft,
    error = BloodBright,
    onError = TextPrimary,
    errorContainer = BloodDeep,
    onErrorContainer = TextSoft,
    scrim = androidx.compose.ui.graphics.Color(0xFF000000),
)

@Composable
fun WeaponizedTheme(
    @Suppress("UNUSED_PARAMETER") darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    // This app is always dark — it's a horror piece. The darkTheme flag is ignored.
    MaterialTheme(
        colorScheme = WeaponizedColors,
        typography = AppTypography,
        content = content
    )
}
