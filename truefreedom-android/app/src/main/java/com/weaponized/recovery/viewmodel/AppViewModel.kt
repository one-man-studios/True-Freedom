package com.weaponized.recovery.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.weaponized.recovery.data.AppDataStore
import com.weaponized.recovery.data.AppState
import com.weaponized.recovery.data.BRAIN_RECOVERY
import com.weaponized.recovery.data.BRAIN_RELAPSE
import com.weaponized.recovery.data.BrainState
import com.weaponized.recovery.data.MILESTONES
import com.weaponized.recovery.data.Milestone
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

const val DAY_MS = 86_400_000L
const val HOUR_MS = 3_600_000L

class AppViewModel(app: Application) : AndroidViewModel(app) {

    private val store = AppDataStore(app)

    // ── In-memory state — updated INSTANTLY for responsive UI.
    // DataStore persistence happens in the background. This fixes the
    // "slow data loading" and "input takes a while" issues. ──────────────
    private val _state = MutableStateFlow(AppState())
    val state: StateFlow<AppState> = _state.asStateFlow()

    private val _todayHabits = MutableStateFlow<Set<String>>(emptySet())
    val todayHabits: StateFlow<Set<String>> = _todayHabits.asStateFlow()

    /** 1-second tick so time-based values (hours saved, chaser) update live. */
    private val _tick = MutableStateFlow(System.currentTimeMillis())
    val tick: StateFlow<Long> = _tick.asStateFlow()

    private val _brainTab = MutableStateFlow("current")  // default to "current" — syncs with status
    val brainTab: StateFlow<String> = _brainTab.asStateFlow()

    private val _brainOpen = MutableStateFlow(false)
    val brainOpen: StateFlow<Boolean> = _brainOpen.asStateFlow()

    private val _relapseDialog = MutableStateFlow(false)
    val relapseDialog: StateFlow<Boolean> = _relapseDialog.asStateFlow()

    private val _startDialog = MutableStateFlow(false)
    val startDialog: StateFlow<Boolean> = _startDialog.asStateFlow()

    private val _resetDialog = MutableStateFlow(false)
    val resetDialog: StateFlow<Boolean> = _resetDialog.asStateFlow()

    private val _devInfoDialog = MutableStateFlow(false)
    val devInfoDialog: StateFlow<Boolean> = _devInfoDialog.asStateFlow()

    init {
        // Load persisted state from DataStore into the in-memory StateFlow
        viewModelScope.launch {
            store.stateFlow.collect { persisted ->
                _state.value = persisted
            }
        }
        // Load today's habits
        viewModelScope.launch {
            store.todayHabitIds().collect { habits ->
                _todayHabits.value = habits
            }
        }
        // Tick every second
        viewModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(1000)
                _tick.value = System.currentTimeMillis()
            }
        }
    }

    // ── Optimistic update helper — updates UI instantly, persists in background ─
    private fun updateState(transform: (AppState) -> AppState) {
        _state.value = transform(_state.value)                    // instant UI update
        viewModelScope.launch {
            store.update { transform(it) }                        // background persist
        }
    }

    // ── Reference time ───────────────────────────────────────────────────────
    fun refTimeMs(): Long {
        val s = _state.value
        val r = s.lastRelapseDate ?: s.startDate ?: return 0L
        return try { isoToMs(r) } catch (_: Exception) { 0L }
    }

    fun hasBegun(): Boolean = refTimeMs() > 0L

    fun streakDays(): Int {
        val ref = refTimeMs()
        if (ref <= 0L) return 0
        return ((System.currentTimeMillis() - ref) / DAY_MS).toInt().coerceAtLeast(0)
    }

    fun hoursSinceStart(): Long {
        val s = _state.value.startDate ?: return 0L
        return try {
            val ms = System.currentTimeMillis() - isoToMs(s)
            (ms / HOUR_MS).toInt().coerceAtLeast(0).toLong()
        } catch (_: Exception) { 0L }
    }

    fun chaserHoursLeft(): Int {
        val last = _state.value.lastRelapseDate ?: return 0
        val ms = try { isoToMs(last) } catch (_: Exception) { return 0 }
        val h = ((System.currentTimeMillis() - ms) / HOUR_MS).toInt()
        return if (h < 72) 72 - h else 0
    }

    fun chaserProgress(): Int {
        val last = _state.value.lastRelapseDate ?: return 100
        val ms = try { isoToMs(last) } catch (_: Exception) { return 100 }
        val h = ((System.currentTimeMillis() - ms) / HOUR_MS).toInt()
        return (h.coerceAtMost(72) * 100 / 72)
    }

    fun winRate(): String {
        val s = _state.value
        val total = s.urgeCount + s.relapseCount
        return if (total > 0) "${s.urgeCount * 100 / total}%" else "—"
    }

    fun phase(): Triple<String, String, Boolean> {
        val s = _state.value
        val days = streakDays()
        if (!hasBegun()) return Triple("DECISION", "amber", false)
        if (s.lastRelapseDate != null && chaserHoursLeft() > 0) return Triple("CHASER", "danger", true)
        if (days < 3) return Triple("WITHDRAWAL", "danger", true)
        if (days < 14) return Triple("EARLY", "amber", false)
        if (days < 90) return Triple("RECOVERING", "amber", false)
        return Triple("HEALED", "surv", false)
    }

    fun pleasurePercent(): Int {
        if (!hasBegun()) return 100
        val d = streakDays()
        if (d == 0) return 30
        return (30 + (70.0 * (1 - Math.exp(-d / 30.0)))).toInt().coerceIn(30, 100)
    }

    fun nextMilestone(): Triple<Milestone, Int, Int> {
        val days = streakDays()
        var next = MILESTONES[0]
        var idx = 0
        for ((i, m) in MILESTONES.withIndex()) {
            if (days < m.d) { next = m; idx = i; break }
            if (i == MILESTONES.lastIndex) { next = m; idx = i }
        }
        val prev = if (idx > 0) MILESTONES[idx - 1] else null
        val fromDay = prev?.d ?: 0
        val span = next.d - fromDay
        val progress = if (prev == null) 0 else ((days - fromDay) * 100 / span).coerceIn(0, 100)
        return Triple(next, fromDay, progress)
    }

    fun currentBrainState(): BrainState {
        val d = streakDays()
        val pct = pleasurePercent()
        val frac = ((pct - 30) / 70.0).coerceIn(0.0, 1.0)
        val r = BRAIN_RECOVERY
        val l = BRAIN_RELAPSE
        val title = "Current State"
        val sub = when {
            d < 3 -> "You're in the danger zone. The chaser is active. Your brain is depleted and screaming for more."
            d < 14 -> "Early recovery. Your brain is starting to heal. Sensors are coming back online."
            d < 90 -> "Recovery in progress. Your brain is rebuilding itself. The urge is fading."
            else -> "Fully healed. Your brain is back to its natural state."
        }
        val badge = when {
            d < 3 -> "CHASER ACTIVE"
            d < 14 -> "EARLY RECOVERY"
            d < 90 -> "RECOVERING"
            else -> "HEALED"
        }
        val badgeColor = when {
            d < 3 -> l.badgeColor
            d < 14 -> androidx.compose.ui.graphics.Color(0xFFFFAA33)
            else -> r.badgeColor
        }
        val control = (l.control + (r.control - l.control) * frac).toInt()
        val craving = (l.craving + (r.craving - l.craving) * frac).toInt()
        val desc = when {
            d < 3 -> "You're in the chaser window. Pleasure sensors at $pct%. Self-control at $control%. Craving at $craving%. **Do not act. The window closes in ${chaserHoursLeft()} hours.**"
            d < 14 -> "Early recovery. Pleasure sensors at $pct%. Your brain is starting to heal."
            d < 90 -> "Recovery in progress. Pleasure sensors at $pct%. Your brain is rebuilding."
            else -> "Fully healed. Pleasure sensors at $pct%."
        }
        val color = when {
            d < 3 -> l.color
            d < 14 -> androidx.compose.ui.graphics.Color(0xFFF5A623)
            else -> r.color
        }
        val accent = when {
            d < 3 -> l.accent
            d < 14 -> androidx.compose.ui.graphics.Color(0xFFFFD700)
            else -> r.accent
        }
        return r.copy(
            pleasure = pct, control = control, craving = craving,
            title = title, sub = sub, badge = badge, badgeColor = badgeColor,
            desc = desc,
            cite = "— Based on your current streak of $d days",
            color = color, accent = accent,
            chaos = (l.chaos + (r.chaos - l.chaos) * frac.toFloat()),
            dimRatio = (l.dimRatio + (r.dimRatio - l.dimRatio) * frac.toFloat()),
            pulseSpeed = (l.pulseSpeed + (r.pulseSpeed - l.pulseSpeed) * frac.toFloat()),
            fireChance = (l.fireChance + (r.fireChance - l.fireChance) * frac.toFloat())
        )
    }

    fun brainStateForTab(tab: String): BrainState {
        return when (tab) {
            "current" -> currentBrainState()
            "recovery" -> BRAIN_RECOVERY
            else -> BRAIN_RELAPSE
        }
    }

    // ── Actions — optimistic (instant UI) + background persist ──────────────
    fun markUrge() {
        updateState { s ->
            s.copy(
                startDate = s.startDate ?: nowIso(),
                urgeCount = s.urgeCount + 1
            )
        }
    }

    fun confirmRelapse(whenMs: Long, note: String) {
        val iso = msToIso(whenMs)
        updateState { s ->
            s.copy(
                startDate = s.startDate ?: iso,
                lastRelapseDate = iso,
                relapseCount = s.relapseCount + 1
            )
        }
        _relapseDialog.value = false
    }

    fun setStart(whenMs: Long) {
        val iso = msToIso(whenMs)
        updateState { it.copy(startDate = iso) }
        _startDialog.value = false
    }

    fun resetAll() {
        _state.value = AppState()
        viewModelScope.launch { store.resetAll() }
        _resetDialog.value = false
    }

    fun setWarnIndex(i: Int) {
        val total = com.weaponized.recovery.data.FACTS.size
        updateState { it.copy(warnIndex = ((i + total) % total)) }
    }

    fun setDmgIndex(i: Int) {
        val total = com.weaponized.recovery.data.DAMAGE.size
        updateState { it.copy(dmgIndex = ((i + total) % total)) }
    }

    fun toggleWarnAuto() {
        updateState { it.copy(warnAuto = !it.warnAuto) }
    }

    fun toggleDmgAuto() {
        updateState { it.copy(dmgAuto = !it.dmgAuto) }
    }

    fun setActiveView(v: String) {
        updateState { it.copy(activeView = v) }
    }

    fun toggleHabit(habitId: String) {
        val current = _todayHabits.value
        val next = if (habitId in current) current - habitId else current + habitId
        _todayHabits.value = next                      // instant UI
        viewModelScope.launch { store.toggleHabit(habitId) }  // background persist
    }

    fun resetTodayHabits() {
        val on = _todayHabits.value
        _todayHabits.value = emptySet()
        viewModelScope.launch {
            for (id in on) store.toggleHabit(id)
        }
    }

    fun setBrainTab(tab: String) { _brainTab.value = tab }
    fun openBrain() {
        _brainTab.value = "current"    // always sync with current status
        _brainOpen.value = true
    }
    fun closeBrain() { _brainOpen.value = false }
    fun openRelapseDialog() { _relapseDialog.value = true }
    fun closeRelapseDialog() { _relapseDialog.value = false }
    fun openStartDialog() { _startDialog.value = true }
    fun closeStartDialog() { _startDialog.value = false }
    fun openResetDialog() { _resetDialog.value = true }
    fun closeResetDialog() { _resetDialog.value = false }
    fun openDevInfoDialog() { _devInfoDialog.value = true }
    fun closeDevInfoDialog() { _devInfoDialog.value = false }

    // ── Time helpers ─────────────────────────────────────────────────────────
    private fun isoToMs(iso: String): Long {
        val s = if (iso.endsWith("Z")) iso else iso + "Z"
        return java.time.OffsetDateTime.parse(s).toInstant().toEpochMilli()
    }

    private fun msToIso(ms: Long): String =
        java.time.OffsetDateTime
            .ofInstant(java.time.Instant.ofEpochMilli(ms), java.time.ZoneOffset.UTC)
            .toString()

    private fun nowIso(): String = msToIso(System.currentTimeMillis())

    fun fmtStartDate(): String {
        val s = _state.value.startDate ?: return "NOT BEGUN"
        return try {
            val d = java.time.OffsetDateTime.parse(s.endsWith("Z").let { if (it) s else s + "Z" })
            val fmt = java.time.format.DateTimeFormatter.ofPattern("MMM d, yyyy", java.util.Locale.US)
            d.format(fmt).uppercase()
        } catch (_: Exception) { "NOT BEGUN" }
    }
}
