package com.weaponized.recovery.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Maps the icon keys used in the content data to Material icons.
 * Returns a fallback (warning) for unknown keys.
 */
fun iconForKey(key: String): ImageVector = when (key) {
    // Damage icons
    "brain" -> Icons.Outlined.Psychology
    "chemistry" -> Icons.Outlined.Science
    "state" -> Icons.Outlined.Shield
    "surveillance" -> Icons.Outlined.Visibility
    "body" -> Icons.Outlined.Person
    "time" -> Icons.Outlined.Schedule
    "mind" -> Icons.Outlined.Memory
    "psyche" -> Icons.Outlined.FavoriteBorder
    "future" -> Icons.Outlined.Public
    // Benefits icons
    "energy" -> Icons.Outlined.Bolt
    "focus" -> Icons.Outlined.CenterFocusStrong
    "testosterone" -> Icons.Outlined.FitnessCenter
    "memory" -> Icons.Outlined.Lightbulb
    "mood" -> Icons.Outlined.SentimentSatisfied
    "sleep" -> Icons.Outlined.Bedtime
    "social" -> Icons.Outlined.Group
    "attraction" -> Icons.Outlined.Favorite
    "timeReclaimed" -> Icons.Outlined.HourglassEmpty
    "selfRespect" -> Icons.Outlined.Verified
    "drive" -> Icons.Outlined.TrendingUp
    "emotional" -> Icons.Outlined.EmojiEmotions
    "worldview" -> Icons.Outlined.RemoveRedEye
    "freedom" -> Icons.Outlined.LockOpen
    // Diet icons
    "eggs" -> Icons.Outlined.Egg
    "redMeat" -> Icons.Outlined.Restaurant
    "oysters" -> Icons.Outlined.SetMeal
    "nuts" -> Icons.Outlined.Spa
    "avocado" -> Icons.Outlined.Grass
    "oil" -> Icons.Outlined.WaterDrop
    "vegetables" -> Icons.Outlined.Eco
    "pomegranate" -> Icons.Outlined.LocalFlorist
    "garlic" -> Icons.Outlined.Microwave
    "onions" -> Icons.Outlined.Circle
    // Exercise icons
    "squats" -> Icons.Outlined.AccessibilityNew
    "deadlift" -> Icons.Outlined.FitnessCenter
    "bench" -> Icons.Outlined.SelfImprovement
    "ohp" -> Icons.Outlined.Expand
    "pullups" -> Icons.Outlined.VerticalAlignTop
    "hiit" -> Icons.Outlined.DirectionsRun
    "avoid" -> Icons.Outlined.Block
    "frequency" -> Icons.Outlined.EventRepeat
    else -> Icons.Outlined.Warning
}

@Composable
fun AppIcon(key: String, tint: Color, size: Int = 22) {
    Icon(
        imageVector = iconForKey(key),
        contentDescription = null,
        tint = tint,
        modifier = Modifier.size(size.dp)
    )
}
