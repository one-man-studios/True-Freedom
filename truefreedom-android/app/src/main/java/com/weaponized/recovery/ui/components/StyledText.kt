package com.weaponized.recovery.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.weaponized.recovery.data.parseBold

/**
 * Renders a string that may contain **bold** markers as a single Text composable
 * with mixed-weight segments. Italic markers *like this* are not supported —
 * only **bold**.
 */
@Composable
fun StyledText(
    text: String,
    modifier: Modifier = Modifier,
    style: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.bodyMedium,
    boldColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary,
    boldWeight: FontWeight = FontWeight.Bold
) {
    val annotated = buildAnnotatedStringFromBold(text, style.color, boldColor, boldWeight)
    Text(text = annotated, style = style, modifier = modifier)
}

fun buildAnnotatedStringFromBold(
    input: String,
    normalColor: androidx.compose.ui.graphics.Color,
    boldColor: androidx.compose.ui.graphics.Color,
    boldWeight: FontWeight
): AnnotatedString {
    val segments = parseBold(input)
    return buildAnnotatedString {
        for (seg in segments) {
            if (seg.bold) {
                withStyle(SpanStyle(color = boldColor, fontWeight = boldWeight)) { append(seg.text) }
            } else {
                withStyle(SpanStyle(color = normalColor)) { append(seg.text) }
            }
        }
    }
}
