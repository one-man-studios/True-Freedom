package com.weaponized.recovery.ui.screens

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.weaponized.recovery.data.TMAXX_DIET
import com.weaponized.recovery.data.TMAXX_EXERCISE
import com.weaponized.recovery.data.TMAXX_HABITS
import com.weaponized.recovery.data.TCard
import com.weaponized.recovery.ui.components.AppIcon
import com.weaponized.recovery.ui.components.StyledText
import com.weaponized.recovery.ui.theme.*
import com.weaponized.recovery.viewmodel.AppViewModel
import com.weaponized.recovery.ui.components.startBorder
import com.weaponized.recovery.ui.components.endBorder
import com.weaponized.recovery.ui.components.topBorder
import com.weaponized.recovery.ui.components.bottomBorder

@Composable
fun TMaxxScreen(vm: AppViewModel) {
    val todayHabits by vm.todayHabits.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "TESTOSTERONE MAXXING",
            style = MaterialTheme.typography.labelLarge,
            color = BloodBright,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Rebuild the man.",
            style = MaterialTheme.typography.displayMedium,
            color = TextPrimary,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Porn nukes your testosterone. Quitting is step one. Step two: actively rebuild with sleep, food, sun, and iron. Track your daily habits below.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSoft,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
        )
        Spacer(Modifier.height(16.dp))

        // Tracker card
        HabitTracker(vm, todayHabits)

        Spacer(Modifier.height(20.dp))

        // Diet section
        TMaxxSection("DIET — EAT FOR TESTOSTERONE", TMAXX_DIET)

        Spacer(Modifier.height(20.dp))

        // Exercise section
        TMaxxSection("EXERCISE — LIFT FOR TESTOSTERONE", TMAXX_EXERCISE)
    }
}

@Composable
private fun HabitTracker(vm: AppViewModel, todayHabits: Set<String>) {
    val ctx = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, LineStrong)
            .background(Brush.linearGradient(listOf(Blood.copy(alpha = 0.12f), Color(0xFF020100).copy(alpha = 0.4f))))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "DAILY HABIT TRACKER",
                style = MaterialTheme.typography.labelMedium,
                color = BloodBright,
                fontWeight = FontWeight.Bold
            )
            Text(
                "${todayHabits.size}/${TMAXX_HABITS.size}",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.height(12.dp))
        // Habits grid — 2 columns
        for (chunk in TMAXX_HABITS.chunked(2)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (habit in chunk) {
                    HabitItem(
                        habit = habit,
                        done = habit.id in todayHabits,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            vm.toggleHabit(habit.id)
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                val vb = ctx.getSystemService(android.content.Context.VIBRATOR_SERVICE) as android.os.Vibrator
                                vb.vibrate(android.os.VibrationEffect.createOneShot(30, android.os.VibrationEffect.DEFAULT_AMPLITUDE))
                            }
                        }
                    )
                }
                if (chunk.size == 1) Spacer(Modifier.weight(1f))
            }
            Spacer(Modifier.height(6.dp))
        }
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = { vm.resetTodayHabits() }) {
                Text(
                    "RESET TODAY",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextDim,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun HabitItem(
    habit: com.weaponized.recovery.data.Habit,
    done: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .height(48.dp)
            .border(1.dp, if (done) SurvDim else LineSoft)
            .background(if (done) Surv.copy(alpha = 0.1f) else Color(0x40000000))
            .clickable { onClick() }
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .border(2.dp, if (done) SurvBright else LineStrong),
            contentAlignment = Alignment.Center
        ) {
            if (done) Text("✓", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
        Spacer(Modifier.width(8.dp))
        Text(
            habit.name,
            style = MaterialTheme.typography.bodyMedium,
            color = if (done) TextPrimary else TextSoft,
            fontWeight = if (done) FontWeight.Medium else FontWeight.Normal
        )
    }
}

@Composable
private fun TMaxxSection(label: String, cards: List<TCard>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .bottomBorder(1.dp, LineStrong)
                .padding(bottom = 8.dp)
        ) {
            Text(
                label,
                style = MaterialTheme.typography.labelMedium,
                color = AmberBright,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.height(10.dp))
        for (chunk in cards.chunked(2)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (card in chunk) {
                    TMaxxCard(card, Modifier.weight(1f))
                }
                if (chunk.size == 1) Spacer(Modifier.weight(1f))
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun TMaxxCard(card: TCard, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .border(1.dp, LineStrong, RectangleShape)
            .startBorder(3.dp, AmberBright)
            .background(
                Brush.linearGradient(listOf(AmberBright.copy(alpha = 0.08f), Color(0xFF020100).copy(alpha = 0.3f)))
            )
            .padding(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .border(1.dp, Amber)
                .background(Amber.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            AppIcon(card.iconKey, AmberBright, size = 20)
        }
        Spacer(Modifier.height(8.dp))
        Text(
            card.title,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(4.dp))
        Text(
            card.why,
            style = MaterialTheme.typography.labelSmall,
            color = AmberBright,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(6.dp))
        StyledText(
            text = card.desc,
            style = MaterialTheme.typography.bodySmall,
            boldColor = TextPrimary
        )
    }
}
