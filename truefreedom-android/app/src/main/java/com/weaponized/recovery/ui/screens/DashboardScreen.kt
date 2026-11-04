package com.weaponized.recovery.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weaponized.recovery.data.DAMAGE
import com.weaponized.recovery.data.FACTS
import com.weaponized.recovery.data.parseBold
import com.weaponized.recovery.ui.components.AppIcon
import com.weaponized.recovery.ui.components.StyledText
import com.weaponized.recovery.ui.theme.*
import com.weaponized.recovery.viewmodel.AppViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.weaponized.recovery.ui.components.startBorder
import com.weaponized.recovery.ui.components.endBorder
import com.weaponized.recovery.ui.components.topBorder
import com.weaponized.recovery.ui.components.bottomBorder
import androidx.compose.ui.platform.LocalConfiguration

@Composable
fun DashboardScreen(vm: AppViewModel) {
    val state by vm.state.collectAsState()
    val tick by vm.tick.collectAsState()
    val brainOpen by vm.brainOpen.collectAsState()
    val config = LocalConfiguration.current
    val isWide = config.screenWidthDp >= 900  // tablet / landscape / foldable
    val isMedium = config.screenWidthDp >= 600 && !isWide  // tablet portrait

    if (isWide) {
        // ── WIDE (≥900dp): 3-column side-by-side, same as before ──
        Row(
            modifier = Modifier.fillMaxSize().background(Bg),
            horizontalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            TrackerPanel(vm, tick, Modifier.weight(1f).fillMaxHeight().endBorder(1.dp, LineStrong))
            Column(
                modifier = Modifier.weight(1.35f).fillMaxHeight().endBorder(1.dp, LineStrong).padding(0.dp)
            ) { WarningHeader(); WarningStage(vm) }
            Column(
                modifier = Modifier.weight(1f).fillMaxHeight().padding(0.dp)
            ) { DamageHeader(); DamageStage(vm) }
        }
    } else if (isMedium) {
        // ── MEDIUM (600–899dp): tracker on top (fixed height), warning + damage below side-by-side.
        // NO verticalScroll — the stages use fillMaxSize() + weight() internally, which crash
        // with infinite constraints from a scrollable parent.
        Column(modifier = Modifier.fillMaxSize().background(Bg)) {
            TrackerPanel(vm, tick, Modifier.fillMaxWidth().bottomBorder(1.dp, LineStrong))
            Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
                Column(
                    modifier = Modifier.weight(1f).endBorder(1.dp, LineStrong),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    WarningHeader()
                    WarningStage(vm)
                }
                Column(modifier = Modifier.weight(1f)) {
                    DamageHeader()
                    DamageStage(vm)
                }
            }
        }
    } else {
        // ── NARROW (<600dp): everything stacked vertically.
        // NO verticalScroll on the outer Column — the stages use weight() internally.
        // Fixed heights for the stages so the pager carousels have bounded constraints.
        Column(modifier = Modifier.fillMaxSize().background(Bg).verticalScroll(rememberScrollState())) {
            TrackerPanel(vm, tick, Modifier.fillMaxWidth(), scrollable = false)
            HorizontalDivider(color = LineStrong, thickness = 1.dp)
            // Column (not Box) so header and stage stack vertically, not overlap
            Column(modifier = Modifier.fillMaxWidth().height(460.dp)) {
                WarningHeader()
                WarningStage(vm)
            }
            HorizontalDivider(color = LineStrong, thickness = 1.dp)
            Column(modifier = Modifier.fillMaxWidth().height(500.dp)) {
                DamageHeader()
                DamageStage(vm)
            }
        }
    }

    if (brainOpen) {
        BrainModal(vm)
    }
}

// ── Tracker panel — extracted so it can be reused across layouts ──────────────
// `scrollable` controls whether the panel has its own verticalScroll. In the
// narrow layout the outer parent is already scrollable, so we pass false to
// avoid nested-scroll touch conflicts.
@Composable
private fun TrackerPanel(vm: AppViewModel, tick: Long, modifier: Modifier = Modifier, scrollable: Boolean = true) {
    Column(
        modifier = if (scrollable) modifier.verticalScroll(rememberScrollState()).padding(12.dp)
                   else modifier.padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PanelHead("YOUR STATUS")
        Spacer(Modifier.height(8.dp))

        val days = vm.streakDays()
        StreakBlock(days)
        Spacer(Modifier.height(12.dp))

        MarkTodayButtons(vm)
        Spacer(Modifier.height(12.dp))

        MiniStatsGrid(vm, tick)
        Spacer(Modifier.height(12.dp))

        ChaserBox(vm, tick)
        Spacer(Modifier.height(12.dp))

        MilestoneNext(vm)
        Spacer(Modifier.height(12.dp))

        BrainButton(onClick = { vm.openBrain() })
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Shared panel head
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun PanelHead(label: String, accent: Color = BloodBright) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(accent, RoundedCornerShape(1.dp))
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = accent
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Left: streak block
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun StreakBlock(days: Int) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "DAYS CLEAN",
            style = MaterialTheme.typography.labelMedium,
            color = BloodBright
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "$days",
            fontSize = 72.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
            color = if (days > 0) TextPrimary else TextDim
        )
        Text(
            "D",
            style = MaterialTheme.typography.labelMedium,
            color = BloodBright
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Left: mark today buttons
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun MarkTodayButtons(vm: AppViewModel) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            "MARK TODAY",
            style = MaterialTheme.typography.labelMedium,
            color = BloodBright,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            MarkButton("URGE BEATEN", Amber, AmberBright, Modifier.weight(1f)) {
                vm.markUrge()
            }
            MarkButton("RELAPSE", BloodBright, BloodBright, Modifier.weight(1f), filled = false) {
                vm.openRelapseDialog()
            }
        }
    }
}

@Composable
private fun MarkButton(
    text: String,
    borderColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
    filled: Boolean = false
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .border(1.dp, borderColor, RectangleShape)
            .background(if (filled) borderColor.copy(alpha = 0.15f) else Color.Transparent)
            .clickable { /* onClick handled by parent */ },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text,
            style = MaterialTheme.typography.labelMedium,
            color = textColor,
            fontWeight = FontWeight.Bold
        )
    }
}

// Workaround: clickable on Box requires lambda; expose via re-overload
@Composable
private fun MarkButton(
    text: String,
    borderColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
    filled: Boolean = false,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(52.dp)
            .border(1.dp, borderColor, RectangleShape)
            .background(if (filled) borderColor.copy(alpha = 0.15f) else Color.Transparent)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text,
            style = MaterialTheme.typography.labelLarge,
            color = textColor,
            fontWeight = FontWeight.Bold
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Left: mini stats grid
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun MiniStatsGrid(vm: AppViewModel, tick: Long) {
    // tick is read here (via the if-statement) to force recomposition every second
    // so that HOURS SAVED updates live. Without reading it, Compose would skip
    // recomposition since vm.hoursSinceStart() isn't a observable state read.
    if (tick > 0) { /* trigger recomposition */ }
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(1.dp)) {
            MiniStat("HOURS SAVED", vm.hoursSinceStart().toString(), TextPrimary, Modifier.weight(1f))
            MiniStat("URGES BEATEN", vm.state.value.urgeCount.toString(), SurvBright, Modifier.weight(1f))
        }
        Spacer(Modifier.height(1.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(1.dp)) {
            MiniStat("RELAPSES", vm.state.value.relapseCount.toString(), BloodBright, Modifier.weight(1f))
            MiniStat("WIN RATE", vm.winRate(), TextPrimary, Modifier.weight(1f))
        }
    }
}

@Composable
private fun MiniStat(label: String, value: String, valueColor: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(Surface)
            .border(1.dp, LineStrong)
            .padding(10.dp)
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = TextDim)
        Spacer(Modifier.height(4.dp))
        Text(
            value,
            style = MaterialTheme.typography.titleMedium,
            color = valueColor,
            fontWeight = FontWeight.Bold
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Left: chaser window
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun ChaserBox(vm: AppViewModel, @Suppress("UNUSED_PARAMETER") tick: Long) {
    val hoursLeft = vm.chaserHoursLeft()
    val progress = vm.chaserProgress()
    val active = hoursLeft > 0
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (active) BloodBright else LineStrong,
                RectangleShape
            )
            .background(
                if (active) Brush.linearGradient(
                    listOf(Blood.copy(alpha = 0.25f), BloodDeep.copy(alpha = 0.5f))
                )
                else Brush.linearGradient(listOf(Surface, Surface))
            )
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "CHASER WINDOW",
                style = MaterialTheme.typography.labelSmall,
                color = if (active) BloodBright else TextDim,
                fontWeight = FontWeight.Bold
            )
            Text(
                if (active) "${hoursLeft}H LEFT" else if (vm.state.value.lastRelapseDate != null) "CLEAR" else "—",
                style = MaterialTheme.typography.labelSmall,
                color = if (active) BloodBright else TextDim,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(Surface3)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(if (active) (1f - progress / 100f).coerceIn(0f, 1f) else 1f)
                    .height(4.dp)
                    .background(
                        if (active) Brush.linearGradient(listOf(Blood, BloodBright))
                        else Brush.linearGradient(listOf(SurvDim, SurvDim))
                    )
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Left: next milestone
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun MilestoneNext(vm: AppViewModel) {
    val (ms, fromDay, progress) = vm.nextMilestone()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, LineStrong)
            .startBorder(3.dp, BloodBright)
            .background(Brush.linearGradient(listOf(BloodBright.copy(alpha = 0.12f), Color.Transparent)))
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "NEXT · DAY ${ms.d}",
                style = MaterialTheme.typography.labelSmall,
                color = BloodBright,
                fontWeight = FontWeight.Bold
            )
            Text(
                "$progress%",
                style = MaterialTheme.typography.labelSmall,
                color = TextDim
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            ms.t,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Serif
        )
        Spacer(Modifier.height(2.dp))
        Text(
            ms.x,
            style = MaterialTheme.typography.bodySmall,
            color = TextSoft
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Left: brain view button
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun BrainButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(36.dp)
            .border(1.dp, LineStrong, RectangleShape)
            .background(Brush.linearGradient(listOf(BloodBright.copy(alpha = 0.18f), BloodDeep.copy(alpha = 0.4f))))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("◉", color = BloodBright, fontSize = 12.sp)
            Spacer(Modifier.width(6.dp))
            Text(
                "BRAIN VIEW",
                style = MaterialTheme.typography.labelMedium,
                color = TextSoft,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Center: warning header + carousel
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun WarningHeader() {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text(
            "READ THIS EVERY TIME",
            style = MaterialTheme.typography.labelMedium,
            color = BloodBright,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Porn is a weapon.",
            style = MaterialTheme.typography.displayMedium,
            color = TextPrimary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "Not a pleasure. Not a release. A debt you keep paying in dopamine, time, and self-respect.",
            style = MaterialTheme.typography.bodySmall,
            color = TextSoft,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
        )
    }
}

@Composable
private fun WarningStage(vm: AppViewModel) {
    val state by vm.state.collectAsState()
    val pagerState = rememberPagerState(initialPage = state.warnIndex.coerceIn(0, FACTS.size - 1)) { FACTS.size }
    val scope = rememberCoroutineScope()

    // Sync pager to persisted warnIndex
    LaunchedEffect(state.warnIndex) {
        if (pagerState.currentPage != state.warnIndex) {
            pagerState.scrollToPage(state.warnIndex)
        }
    }

    // Auto-scroll — read vm.state.value.warnAuto inside the loop for fresh reads
    LaunchedEffect(state.warnAuto) {
        while (vm.state.value.warnAuto) {
            kotlinx.coroutines.delay(11000)
            val next = (pagerState.currentPage + 1) % FACTS.size
            pagerState.animateScrollToPage(next)
            vm.setWarnIndex(next)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
            pageSpacing = 12.dp
        ) { page ->
            FactCard(FACTS[page])
        }
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Toggle auto
            val onAuto = state.warnAuto
            Box(
                modifier = Modifier
                    .border(1.dp, if (onAuto) SurvDim else LineStrong)
                    .background(if (onAuto) SurvDim.copy(alpha = 0.2f) else Color.Transparent)
                    .clickable { vm.toggleWarnAuto() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    if (onAuto) "AUTO" else "MANUAL",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (onAuto) SurvBright else TextDim,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.width(8.dp))
            // Prev / Next
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .border(1.dp, LineStrong)
                    .clickable {
                        val prev = (pagerState.currentPage - 1 + FACTS.size) % FACTS.size
                        scope.launch { pagerState.animateScrollToPage(prev) }
                        vm.setWarnIndex(prev)
                    },
                contentAlignment = Alignment.Center
            ) { Text("▲", color = TextDim) }
            Spacer(Modifier.width(4.dp))
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .border(1.dp, LineStrong)
                    .clickable {
                        val next = (pagerState.currentPage + 1) % FACTS.size
                        scope.launch { pagerState.animateScrollToPage(next) }
                        vm.setWarnIndex(next)
                    },
                contentAlignment = Alignment.Center
            ) { Text("▼", color = TextDim) }
            Spacer(Modifier.width(12.dp))
            // Progress bar
            Box(
                modifier = Modifier.weight(1f).height(4.dp).background(Surface3)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth((pagerState.currentPage + 1f) / FACTS.size)
                        .height(4.dp)
                        .background(Brush.linearGradient(listOf(Blood, BloodBright)))
                )
            }
            Spacer(Modifier.width(8.dp))
            Text(
                "${pagerState.currentPage + 1}/${FACTS.size}",
                style = MaterialTheme.typography.labelSmall,
                color = TextDim,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun FactCard(fact: com.weaponized.recovery.data.Fact) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "FACT ${fact.n} / ${FACTS.size.toString().padStart(2, '0')}",
            style = MaterialTheme.typography.labelMedium,
            color = BloodBright,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(12.dp))
        StyledText(
            text = fact.text,
            style = MaterialTheme.typography.titleLarge.copy(lineHeight = 24.sp),
            boldColor = BloodBright
        )
        Spacer(Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(LineSoft)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            fact.cite,
            style = MaterialTheme.typography.labelSmall,
            color = TextDim
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Right: damage header + carousel
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun DamageHeader() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(AmberBright, RoundedCornerShape(1.dp))
        )
        Spacer(Modifier.width(8.dp))
        Text(
            "THE DAMAGE",
            style = MaterialTheme.typography.labelLarge,
            color = AmberBright,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun DamageStage(vm: AppViewModel) {
    val state by vm.state.collectAsState()
    val pagerState = rememberPagerState(initialPage = state.dmgIndex.coerceIn(0, DAMAGE.size - 1)) { DAMAGE.size }
    val scope = rememberCoroutineScope()

    LaunchedEffect(state.dmgIndex) {
        if (pagerState.currentPage != state.dmgIndex) {
            pagerState.scrollToPage(state.dmgIndex)
        }
    }
    LaunchedEffect(state.dmgAuto) {
        while (vm.state.value.dmgAuto) {
            kotlinx.coroutines.delay(8000)
            val next = (pagerState.currentPage + 1) % DAMAGE.size
            pagerState.animateScrollToPage(next)
            vm.setDmgIndex(next)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
            pageSpacing = 12.dp
        ) { page ->
            DamageCard(DAMAGE[page])
        }
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val onAuto = state.dmgAuto
            Box(
                modifier = Modifier
                    .border(1.dp, if (onAuto) SurvDim else LineStrong)
                    .background(if (onAuto) SurvDim.copy(alpha = 0.2f) else Color.Transparent)
                    .clickable { vm.toggleDmgAuto() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    if (onAuto) "AUTO" else "MANUAL",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (onAuto) SurvBright else TextDim,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .border(1.dp, LineStrong)
                    .clickable {
                        val prev = (pagerState.currentPage - 1 + DAMAGE.size) % DAMAGE.size
                        scope.launch { pagerState.animateScrollToPage(prev) }
                        vm.setDmgIndex(prev)
                    },
                contentAlignment = Alignment.Center
            ) { Text("▲", color = TextDim) }
            Spacer(Modifier.width(4.dp))
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .border(1.dp, LineStrong)
                    .clickable {
                        val next = (pagerState.currentPage + 1) % DAMAGE.size
                        scope.launch { pagerState.animateScrollToPage(next) }
                        vm.setDmgIndex(next)
                    },
                contentAlignment = Alignment.Center
            ) { Text("▼", color = TextDim) }
            Spacer(Modifier.width(12.dp))
            Box(
                modifier = Modifier.weight(1f).height(4.dp).background(Surface3)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth((pagerState.currentPage + 1f) / DAMAGE.size)
                        .height(4.dp)
                        .background(Brush.linearGradient(listOf(Amber, AmberBright)))
                )
            }
            Spacer(Modifier.width(8.dp))
            Text(
                "${pagerState.currentPage + 1}/${DAMAGE.size}",
                style = MaterialTheme.typography.labelSmall,
                color = TextDim,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun DamageCard(dmg: com.weaponized.recovery.data.Damage) {
    val accent = if (dmg.amber) AmberBright else BloodBright
    Column(
        modifier = Modifier.fillMaxSize().padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Header: icon + category + title
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .border(1.dp, accent)
                    .background(
                        if (dmg.amber) Brush.linearGradient(listOf(AmberBright.copy(alpha = 0.2f), Color(0xFF502800).copy(alpha = 0.4f)))
                        else Brush.linearGradient(listOf(BloodBright.copy(alpha = 0.2f), BloodDeep.copy(alpha = 0.4f)))
                    ),
                contentAlignment = Alignment.Center
            ) {
                AppIcon(dmg.iconKey, accent, size = 20)
            }
            Spacer(Modifier.width(10.dp))
            Column {
                Text("DAMAGE · ${dmg.cat}", style = MaterialTheme.typography.labelSmall, color = accent, fontWeight = FontWeight.Bold)
                StyledText(text = dmg.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), boldColor = accent)
            }
        }
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(LineStrong))
        // Body text — grows to fill available space
        StyledText(text = dmg.body, style = MaterialTheme.typography.bodyMedium, boldColor = accent, modifier = Modifier.weight(1f))
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(LineStrong))
        // Stats row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            for (s in dmg.stats) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(s.k, style = MaterialTheme.typography.labelSmall, color = TextFaint, fontWeight = FontWeight.Bold)
                    Text(s.v, style = MaterialTheme.typography.titleLarge, color = accent, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Brain modal
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun BrainModal(vm: AppViewModel) {
    val tab by vm.brainTab.collectAsState()
    val brainState = vm.brainStateForTab(tab)

    AlertDialog(
        onDismissRequest = { vm.closeBrain() },
        modifier = Modifier.fillMaxWidth(),
        containerColor = BgDeep,
        titleContentColor = TextPrimary,
        title = {
            Column {
                Text(
                    "BRAIN ACTIVITY VIEW",
                    style = MaterialTheme.typography.labelMedium,
                    color = BloodBright,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    brainState.title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary
                )
            }
        },
        text = {
            Column {
                // Tabs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    BrainTab("relapse", "AFTER RELAPSE", "BURNT OUT", tab, Modifier.weight(1f)) { vm.setBrainTab("relapse") }
                    BrainTab("current", "CURRENT", "BASED ON STREAK", tab, Modifier.weight(1f)) { vm.setBrainTab("current") }
                    BrainTab("recovery", "AFTER RECOVERY", "HEALED", tab, Modifier.weight(1f)) { vm.setBrainTab("recovery") }
                }
                Spacer(Modifier.height(8.dp))
                com.weaponized.recovery.ui.components.BrainCanvas(
                    state = brainState,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                // Stats row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    BrainStat("PLEASURE", "${brainState.pleasure}%", brainState.pleasure, Modifier.weight(1f))
                    BrainStat("CONTROL", "${brainState.control}%", brainState.control, Modifier.weight(1f))
                    BrainStat("CRAVING", "${brainState.craving}%", 100 - brainState.craving, Modifier.weight(1f), invert = true)
                }
                Spacer(Modifier.height(8.dp))
                StyledText(
                    text = brainState.desc,
                    style = MaterialTheme.typography.bodyMedium,
                    boldColor = BloodBright
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    brainState.cite,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextDim
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { vm.closeBrain() }) {
                Text("CLOSE", color = BloodBright, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
private fun BrainTab(
    key: String,
    label: String,
    sub: String,
    active: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val isActive = key == active
    val accent = when (key) {
        "relapse" -> BloodBright
        "current" -> AmberBright
        else -> SurvBright
    }
    Column(
        modifier = modifier
            .border(1.dp, LineSoft)
            .background(if (isActive) Blood.copy(alpha = 0.25f) else Color.Transparent)
            .clickable { onClick() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isActive) accent else TextDim,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            sub,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
            color = if (isActive) accent else TextFaint
        )
    }
}

@Composable
private fun BrainStat(label: String, value: String, pct: Int, modifier: Modifier = Modifier, invert: Boolean = false) {
    val color = when {
        pct >= 90 -> if (invert) SurvBright else SurvBright
        pct >= 60 -> AmberBright
        else -> BloodBright
    }
    Column(
        modifier = modifier
            .border(1.dp, LineSoft)
            .background(Surface.copy(alpha = 0.5f))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = TextDim, fontWeight = FontWeight.Bold)
        Text(
            value,
            style = MaterialTheme.typography.titleLarge,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}
