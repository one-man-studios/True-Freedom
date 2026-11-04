package com.weaponized.recovery.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weaponized.recovery.data.SOURCES
import com.weaponized.recovery.data.Source
import com.weaponized.recovery.ui.theme.*
import com.weaponized.recovery.ui.components.startBorder
import com.weaponized.recovery.ui.components.endBorder
import com.weaponized.recovery.ui.components.topBorder
import com.weaponized.recovery.ui.components.bottomBorder

@Composable
fun SourcesScreen() {
    val ctx = LocalContext.current
    val scroll = rememberScrollState()

    // Group by category, preserving insertion order
    val cats = SOURCES.map { it.cat }.distinct()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
            .verticalScroll(scroll)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Page head
        Text(
            "SOURCES & STUDIES",
            style = MaterialTheme.typography.labelLarge,
            color = BloodBright,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "The evidence.",
            style = MaterialTheme.typography.displayMedium,
            color = TextPrimary,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Every claim is grounded in declassified documents, peer-reviewed research, or the Easy Peasy Method. Plus the tools that actually work: DNS porn blockers, blocking apps, browser extensions, and communities — the infrastructure for staying clean.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSoft,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
        )
        Spacer(Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (cat in cats) {
                val sources = SOURCES.filter { it.cat == cat }
                SourceCategoryHeader(cat, sources.size)
                Spacer(Modifier.height(2.dp))
                for (src in sources) {
                    SourceCard(src) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(src.link))
                        ctx.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                    }
                }
            }
        }
    }
}

/** Per-category accent color — mirrors the web app's category color scheme. */
private fun accentForCategory(cat: String): Color = when (cat) {
    "DNS PORNOGRAPHY BLOCKERS" -> Color(0xFF22F08A)
    "BLOCKING APPS & SOFTWARE" -> Color(0xFFFFAA33)
    "BROWSER EXTENSIONS" -> Color(0xFF3AA0FF)
    "COMMUNITIES" -> Color(0xFFC87AFF)
    "EDUCATIONAL" -> Color(0xFFFFD93A)
    else -> BloodBright  // METHOD & BOOKS, DECLASSIFIED & STUDIES
}

/** Dimmed version for the card left border (before hover). */
private fun dimAccentForCategory(cat: String): Color = when (cat) {
    "DNS PORNOGRAPHY BLOCKERS" -> Color(0xFF005A32)
    "BLOCKING APPS & SOFTWARE" -> Color(0xFF804000)
    "BROWSER EXTENSIONS" -> Color(0xFF2A6AAA)
    "COMMUNITIES" -> Color(0xFF7A4AAA)
    "EDUCATIONAL" -> Color(0xFF9A7A00)
    else -> Blood
}

@Composable
private fun SourceCategoryHeader(cat: String, count: Int) {
    val accent = accentForCategory(cat)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .topBorder(1.dp, LineStrong)
            .bottomBorder(1.dp, LineStrong)
            .background(
                Brush.linearGradient(listOf(accent.copy(alpha = 0.18f), Color(0xFF020100).copy(alpha = 0.2f)))
            )
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(accent, RoundedCornerShape(1.dp))
        )
        Spacer(Modifier.width(10.dp))
        Text(
            cat,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.labelLarge,
            color = accent,
            fontWeight = FontWeight.Bold
        )
        Box(
            modifier = Modifier
                .border(1.dp, LineStrong)
                .background(Color(0x40000000))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                count.toString(),
                style = MaterialTheme.typography.labelSmall,
                color = TextDim,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun SourceCard(src: Source, onClick: () -> Unit) {
    val accent = accentForCategory(src.cat)
    val dimAccent = dimAccentForCategory(src.cat)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, LineStrong, RectangleShape)
            .startBorder(3.dp, dimAccent)
            .background(
                Brush.linearGradient(listOf(dimAccent.copy(alpha = 0.08f), Color(0xFF020100).copy(alpha = 0.3f)))
            )
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Text(
            src.type,
            style = MaterialTheme.typography.labelSmall,
            color = accent,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(4.dp))
        Text(
            src.title,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(4.dp))
        Text(
            src.meta,
            style = MaterialTheme.typography.bodySmall,
            color = TextSoft
        )
        Spacer(Modifier.height(6.dp))
        Text(
            src.link,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
            color = TextDim
        )
    }
}
