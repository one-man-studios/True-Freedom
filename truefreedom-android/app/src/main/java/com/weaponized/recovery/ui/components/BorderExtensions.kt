package com.weaponized.recovery.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

/**
 * Side-specific border modifiers.
 *
 * Compose's built-in Modifier.border() only borders all four sides. These
 * extensions draw a line on a single side, mirroring CSS border-left / border-top
 * / border-right / border-bottom semantics used throughout the web app.
 */

/** Draws a vertical line on the start (left in LTR) edge. */
fun Modifier.startBorder(width: Dp, color: Color): Modifier = this.drawWithContent {
    drawContent()
    val w = width.toPx()
    drawLine(
        color = color,
        start = Offset(0f, 0f),
        end = Offset(0f, size.height),
        strokeWidth = w
    )
}

/** Draws a vertical line on the end (right in LTR) edge. */
fun Modifier.endBorder(width: Dp, color: Color): Modifier = this.drawWithContent {
    drawContent()
    val w = width.toPx()
    drawLine(
        color = color,
        start = Offset(size.width, 0f),
        end = Offset(size.width, size.height),
        strokeWidth = w
    )
}

/** Draws a horizontal line on the top edge. */
fun Modifier.topBorder(width: Dp, color: Color): Modifier = this.drawWithContent {
    drawContent()
    val w = width.toPx()
    drawLine(
        color = color,
        start = Offset(0f, 0f),
        end = Offset(size.width, 0f),
        strokeWidth = w
    )
}

/** Draws a horizontal line on the bottom edge. */
fun Modifier.bottomBorder(width: Dp, color: Color): Modifier = this.drawWithContent {
    drawContent()
    val w = width.toPx()
    drawLine(
        color = color,
        start = Offset(0f, size.height),
        end = Offset(size.width, size.height),
        strokeWidth = w
    )
}
