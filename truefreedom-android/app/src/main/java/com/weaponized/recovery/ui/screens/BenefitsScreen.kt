package com.weaponized.recovery.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.weaponized.recovery.data.BENEFITS
import com.weaponized.recovery.ui.components.AppIcon
import com.weaponized.recovery.ui.theme.*
import com.weaponized.recovery.ui.components.startBorder
import com.weaponized.recovery.ui.components.endBorder
import com.weaponized.recovery.ui.components.topBorder
import com.weaponized.recovery.ui.components.bottomBorder

@Composable
fun BenefitsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "WHAT RETURNS",
            style = MaterialTheme.typography.labelLarge,
            color = BloodBright,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "The benefits of quitting.",
            style = MaterialTheme.typography.displayMedium,
            color = TextPrimary,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "These are not rewards for quitting. They are the floor returning to its natural level once the chemical debt is paid.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSoft,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
        )
        Spacer(Modifier.height(16.dp))

        // Grid of benefit cards — flow in a scrollable column
        for (chunk in BENEFITS.chunked(2)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (b in chunk) {
                    BenefitCard(b, Modifier.weight(1f))
                }
                // Pad the row if odd count
                if (chunk.size == 1) Spacer(Modifier.weight(1f))
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun BenefitCard(benefit: com.weaponized.recovery.data.Benefit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .border(1.dp, LineStrong, RectangleShape)
            .startBorder(3.dp, Surv)
            .background(
                Brush.linearGradient(listOf(Color(0xFF003C28).copy(alpha = 0.12f), Color(0xFF020100).copy(alpha = 0.3f)))
            )
            .padding(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .border(1.dp, SurvDim, RectangleShape)
                .background(Surv.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            AppIcon(benefit.iconKey, SurvBright, size = 20)
        }
        Spacer(Modifier.height(8.dp))
        Text(
            benefit.title,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(4.dp))
        Text(
            benefit.desc,
            style = MaterialTheme.typography.bodySmall,
            color = TextSoft
        )
    }
}
