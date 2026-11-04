package com.weaponized.recovery.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Mono family — used for labels, stats, badges
private val Mono = FontFamily.Monospace

// Serif family — used for headlines, fact text, milestone titles
private val Serif = FontFamily.Serif

// Sans family — body copy
private val Sans = FontFamily.SansSerif

val AppTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = Serif, fontWeight = FontWeight.Bold,
        fontSize = 40.sp, lineHeight = 40.sp, letterSpacing = (-1.5).sp
    ),
    displayMedium = TextStyle(
        fontFamily = Serif, fontWeight = FontWeight.Bold,
        fontSize = 28.sp, lineHeight = 30.sp, letterSpacing = (-0.5).sp
    ),
    headlineLarge = TextStyle(
        fontFamily = Serif, fontWeight = FontWeight.Bold,
        fontSize = 24.sp, lineHeight = 26.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = Serif, fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp, lineHeight = 24.sp
    ),
    titleLarge = TextStyle(
        fontFamily = Serif, fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp, lineHeight = 22.sp
    ),
    titleMedium = TextStyle(
        fontFamily = Sans, fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp, lineHeight = 22.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = Sans, fontWeight = FontWeight.Normal,
        fontSize = 15.sp, lineHeight = 22.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Sans, fontWeight = FontWeight.Normal,
        fontSize = 14.sp, lineHeight = 20.sp
    ),
    bodySmall = TextStyle(
        fontFamily = Sans, fontWeight = FontWeight.Normal,
        fontSize = 12.sp, lineHeight = 17.sp
    ),
    // Labels — bumped from 12/10/9sp to 13/11/10sp for readability on mobile.
    // The old sizes were too small (9sp was nearly unreadable on a phone).
    labelLarge = TextStyle(
        fontFamily = Mono, fontWeight = FontWeight.Bold,
        fontSize = 13.sp, lineHeight = 15.sp, letterSpacing = 1.2.sp
    ),
    labelMedium = TextStyle(
        fontFamily = Mono, fontWeight = FontWeight.Bold,
        fontSize = 11.sp, lineHeight = 13.sp, letterSpacing = 1.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = Mono, fontWeight = FontWeight.Normal,
        fontSize = 10.sp, lineHeight = 12.sp, letterSpacing = 1.2.sp
    )
)
