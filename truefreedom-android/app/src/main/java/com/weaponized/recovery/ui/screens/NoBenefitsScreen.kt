package com.weaponized.recovery.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weaponized.recovery.data.MYTHS
import com.weaponized.recovery.ui.components.StyledText
import com.weaponized.recovery.ui.theme.*
import com.weaponized.recovery.ui.components.startBorder
import com.weaponized.recovery.ui.components.endBorder
import com.weaponized.recovery.ui.components.topBorder
import com.weaponized.recovery.ui.components.bottomBorder

@Composable
fun NoBenefitsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "THE TRUTH",
            style = MaterialTheme.typography.labelLarge,
            color = BloodBright,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        // Title with mixed styling
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                "Porn has ",
                style = MaterialTheme.typography.displayMedium,
                color = TextPrimary,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold
            )
            Text(
                "zero",
                style = MaterialTheme.typography.displayMedium,
                color = BloodBright,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic
            )
            Text(
                " benefits.",
                style = MaterialTheme.typography.displayMedium,
                color = TextPrimary,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            "Every \"benefit\" you think it gives you is the addiction pretending to be a friend. Here are the myths — and the truth behind each one.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSoft,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontStyle = FontStyle.Italic
        )
        Spacer(Modifier.height(16.dp))

        // Myth cards
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            for (myth in MYTHS) {
                MythCard(myth)
            }
        }

        Spacer(Modifier.height(24.dp))
        ZeroBenefitBanner()
    }
}

@Composable
private fun MythCard(myth: com.weaponized.recovery.data.Myth) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, LineStrong, RectangleShape)
            .startBorder(3.dp, BloodBright)
            .background(
                Brush.linearGradient(listOf(Blood.copy(alpha = 0.15f), Color(0xFF020100).copy(alpha = 0.3f)))
            )
            .padding(16.dp)
    ) {
        Text(
            "THE MYTH",
            style = MaterialTheme.typography.labelSmall,
            color = TextFaint,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(4.dp))
        Text(
            myth.myth,
            style = MaterialTheme.typography.titleMedium,
            color = BloodBright,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.SemiBold,
            fontStyle = FontStyle.Italic
        )
        Spacer(Modifier.height(12.dp))
        Text(
            "THE TRUTH",
            style = MaterialTheme.typography.labelSmall,
            color = SurvBright,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(4.dp))
        StyledText(
            text = myth.truth,
            style = MaterialTheme.typography.bodyMedium,
            boldColor = TextPrimary
        )
    }
}

@Composable
private fun ZeroBenefitBanner() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, BloodBright, RectangleShape)
            .background(
                Brush.radialGradient(
                    listOf(Blood.copy(alpha = 0.4f), Color(0xFF020100).copy(alpha = 0.6f))
                )
            )
            .padding(24.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "0",
            fontSize = 96.sp,
            color = BloodBright,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.width(16.dp))
        Column {
            Text(
                "BENEFITS OF PORNOGRAPHY",
                style = MaterialTheme.typography.labelLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "There are none. There never were.",
                style = MaterialTheme.typography.labelMedium,
                color = TextDim
            )
        }
    }
}
