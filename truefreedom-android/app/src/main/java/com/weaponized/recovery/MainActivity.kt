package com.weaponized.recovery

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import com.weaponized.recovery.ui.components.startBorder
import com.weaponized.recovery.ui.components.endBorder
import com.weaponized.recovery.ui.components.topBorder
import com.weaponized.recovery.ui.components.bottomBorder
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.weaponized.recovery.ui.screens.BenefitsScreen
import com.weaponized.recovery.ui.screens.DashboardScreen
import com.weaponized.recovery.ui.screens.NoBenefitsScreen
import com.weaponized.recovery.ui.screens.SourcesScreen
import com.weaponized.recovery.ui.screens.TMaxxScreen
import com.weaponized.recovery.ui.theme.*
import com.weaponized.recovery.viewmodel.AppViewModel
import java.util.Calendar

class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "WEAPONIZED"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install a crash logger so any uncaught exception is written to logcat
        // with a clear tag — makes it possible to diagnose crashes from device logs.
        val previousHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            android.util.Log.e(TAG, "FATAL CRASH on ${thread.name}", throwable)
            previousHandler?.uncaughtException(thread, throwable)
        }

        super.onCreate(savedInstanceState)
        // Do NOT use enableEdgeToEdge() — it draws content behind the status bar
        // and navigation bar, hiding system UI (clock, battery, nav buttons).
        // Instead, set the status/nav bar colors to match the app background
        // and let the system handle insets normally.
        window.statusBarColor = android.graphics.Color.parseColor("#050204")
        window.navigationBarColor = android.graphics.Color.parseColor("#050204")
        setContent {
            WeaponizedTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Bg
                ) {
                    AppRoot()
                }
            }
        }
    }
}

@Composable
private fun AppRoot(vm: AppViewModel = viewModel()) {
    val state by vm.state.collectAsState()
    val tick by vm.tick.collectAsState()
    val relapseDialog by vm.relapseDialog.collectAsState()
    val startDialog by vm.startDialog.collectAsState()
    val resetDialog by vm.resetDialog.collectAsState()
    val devInfoDialog by vm.devInfoDialog.collectAsState()
    val ctx = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0A0303),
                        Bg,
                        BgDeep
                    )
                )
            )
    ) {
        // ── TOP BAR ──────────────────────────────────────────────────────────
        TopBar(vm, tick)

        // ── VIEW NAV ─────────────────────────────────────────────────────────
        ViewNav(state.activeView) { vm.setActiveView(it) }

        // ── CONTENT ──────────────────────────────────────────────────────────
        Box(modifier = Modifier.weight(1f)) {
            when (state.activeView) {
                "dashboard" -> DashboardScreen(vm)
                "sources" -> SourcesScreen()
                "benefits" -> BenefitsScreen()
                "nobenefits" -> NoBenefitsScreen()
                "tmaxx" -> TMaxxScreen(vm)
            }
        }

        // ── BOTTOM BAR ───────────────────────────────────────────────────────
        BottomBar(vm) { url ->
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            ctx.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }

    // ── DIALOGS ──────────────────────────────────────────────────────────────
    if (relapseDialog) RelapseDialog(vm)
    if (startDialog) StartDialog(vm)
    if (resetDialog) ResetDialog(vm)
    if (devInfoDialog) DevInfoDialog(vm)
}

// ─────────────────────────────────────────────────────────────────────────────
// Top bar — brand + live stats
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun TopBar(vm: AppViewModel, @Suppress("UNUSED_PARAMETER") tick: Long) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(Blood.copy(alpha = 0.6f), Color(0xFF140000).copy(alpha = 0.3f))))
            .bottomBorder(1.dp, LineStrong)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Brand + info icon
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(BloodBright, RoundedCornerShape(1.dp))
            )
            Spacer(Modifier.width(8.dp))
            Text(
                "TRUE FREEDOM",
                style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp, letterSpacing = 2.sp),
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.width(6.dp))
            // Info icon — opens dev info dialog
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clickable { vm.openDevInfoDialog() },
                contentAlignment = Alignment.Center
            ) {
                Text("ℹ", color = TextDim, fontSize = 13.sp)
            }
        }

        // Live stats — compact: PHASE + STREAK only (SENSORS shown in tracker panel)
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val (phaseLabel, _, _) = vm.phase()
            TopStat("PHASE", phaseLabel, AmberBright)
            TopStat("STREAK", "${vm.streakDays()}D", BloodBright)
        }
    }
}

@Composable
private fun TopStat(label: String, value: String, valueColor: Color) {
    Column(horizontalAlignment = Alignment.End) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = TextFaint
        )
        Text(
            value,
            style = MaterialTheme.typography.labelMedium.copy(fontSize = 13.sp),
            color = valueColor,
            fontWeight = FontWeight.Bold
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// View navigation
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun ViewNav(active: String, onSelect: (String) -> Unit) {
    val tabs = listOf(
        "dashboard" to "DASH",
        "sources" to "SOURCES",
        "benefits" to "BENEFITS",
        "nobenefits" to "MYTHS",
        "tmaxx" to "T-MAXX"
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0x66000000))
            .bottomBorder(1.dp, LineStrong),
        horizontalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        for ((key, label) in tabs) {
            val isActive = key == active
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onSelect(key) }
                    .background(if (isActive) Blood.copy(alpha = 0.25f) else Color.Transparent)
                    .bottomBorder(2.dp, if (isActive) BloodBright else Color.Transparent)
                    .padding(vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    label,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isActive) BloodBright else TextDim,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Bottom bar — links + edit start + reset
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun BottomBar(vm: AppViewModel, onLink: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(Color.Transparent, Blood.copy(alpha = 0.3f))))
            .topBorder(1.dp, LineStrong)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left info — links
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = { onLink("https://easypeasymethod.org/") },
                contentPadding = PaddingValues(0.dp)) {
                Text("EASYPEASY", style = MaterialTheme.typography.labelSmall, color = TextSoft, fontWeight = FontWeight.Bold)
            }
            TextButton(onClick = { onLink("https://archive.org/details/cia-readingroom-document-06760269") },
                contentPadding = PaddingValues(0.dp)) {
                Text("MKULTRA", style = MaterialTheme.typography.labelSmall, color = TextSoft, fontWeight = FontWeight.Bold)
            }
        }

        // Right info — started + edit + reset
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                vm.fmtStartDate(),
                style = MaterialTheme.typography.labelSmall,
                color = TextDim,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = { vm.openStartDialog() }, contentPadding = PaddingValues(0.dp)) {
                Text("EDIT", style = MaterialTheme.typography.labelSmall, color = TextDim, fontWeight = FontWeight.Bold)
            }
            TextButton(onClick = { vm.openResetDialog() }, contentPadding = PaddingValues(0.dp)) {
                Text("RESET", style = MaterialTheme.typography.labelSmall, color = TextDim, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Relapse dialog
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun RelapseDialog(vm: AppViewModel) {
    val ctx = LocalContext.current
    val cal = remember { Calendar.getInstance() }
    cal.timeInMillis = System.currentTimeMillis() - 60_000
    var year by remember { mutableStateOf(cal.get(Calendar.YEAR)) }
    var month by remember { mutableStateOf(cal.get(Calendar.MONTH)) }
    var day by remember { mutableStateOf(cal.get(Calendar.DAY_OF_MONTH)) }
    var hour by remember { mutableStateOf(cal.get(Calendar.HOUR_OF_DAY)) }
    var minute by remember { mutableStateOf(cal.get(Calendar.MINUTE)) }
    var note by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        android.app.DatePickerDialog(ctx, { _, y, m, d ->
            year = y; month = m; day = d; showDatePicker = false
        }, year, month, day).apply {
            setOnCancelListener { showDatePicker = false }
        }.show()
    }
    if (showTimePicker) {
        android.app.TimePickerDialog(ctx, { _, h, min ->
            hour = h; minute = min; showTimePicker = false
        }, hour, minute, false).apply {  // false = 12-hour format with AM/PM
            setOnCancelListener { showTimePicker = false }
        }.show()
    }

    // Format time as 12-hour with AM/PM
    val ampm = if (hour < 12) "AM" else "PM"
    val hour12 = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
    val timeStr = String.format("%d:%02d %s", hour12, minute, ampm)
    val dateStr = String.format("%s %d, %d",
        java.text.SimpleDateFormat("MMM", java.util.Locale.US).format(cal.apply { set(year, month, day) }.time),
        day, year)

    AlertDialog(
        onDismissRequest = { vm.closeRelapseDialog() },
        modifier = Modifier.fillMaxWidth(),
        containerColor = BgDeep,
        title = {
            Column {
                Text("⚠ MARK RELAPSE", style = MaterialTheme.typography.labelMedium, color = BloodBright, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                Text("The chaser is now live for 72 hours.", style = MaterialTheme.typography.titleLarge, color = TextPrimary, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column {
                Text("This is not defeat — it is a reminder of how the trap works. The chemical debt is back on your books.", style = MaterialTheme.typography.bodyMedium, color = TextSoft)
                Spacer(Modifier.height(12.dp))
                Text("WHEN", style = MaterialTheme.typography.labelSmall, color = TextFaint, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                // Date button
                Box(modifier = Modifier.fillMaxWidth().border(1.dp, LineStrong).clickable { showDatePicker = true }.padding(12.dp)) {
                    Text("📅 $dateStr", style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
                }
                Spacer(Modifier.height(6.dp))
                // Time button
                Box(modifier = Modifier.fillMaxWidth().border(1.dp, LineStrong).clickable { showTimePicker = true }.padding(12.dp)) {
                    Text("🕐 $timeStr", style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
                }
                Spacer(Modifier.height(8.dp))
                Text("NOTES", style = MaterialTheme.typography.labelSmall, color = TextFaint, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 75.dp),
                    placeholder = { Text("What was the trigger?", color = TextFaint) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0x40000000), unfocusedContainerColor = Color(0x40000000),
                        focusedBorderColor = BloodBright, unfocusedBorderColor = LineStrong,
                        cursorColor = BloodBright, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary
                    )
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val c = Calendar.getInstance()
                c.set(year, month, day, hour, minute, 0)
                vm.confirmRelapse(c.timeInMillis, note)
            }) { Text("RESTART COUNT", color = BloodBright, fontWeight = FontWeight.Bold) }
        },
        dismissButton = {
            TextButton(onClick = { vm.closeRelapseDialog() }) { Text("CANCEL", color = TextSoft) }
        }
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Start date dialog
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun StartDialog(vm: AppViewModel) {
    val ctx = LocalContext.current
    val cal = remember { Calendar.getInstance() }
    cal.timeInMillis = System.currentTimeMillis() - 120_000
    var year by remember { mutableStateOf(cal.get(Calendar.YEAR)) }
    var month by remember { mutableStateOf(cal.get(Calendar.MONTH)) }
    var day by remember { mutableStateOf(cal.get(Calendar.DAY_OF_MONTH)) }
    var hour by remember { mutableStateOf(cal.get(Calendar.HOUR_OF_DAY)) }
    var minute by remember { mutableStateOf(cal.get(Calendar.MINUTE)) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        android.app.DatePickerDialog(ctx, { _, y, m, d ->
            year = y; month = m; day = d; showDatePicker = false
        }, year, month, day).apply { setOnCancelListener { showDatePicker = false } }.show()
    }
    if (showTimePicker) {
        android.app.TimePickerDialog(ctx, { _, h, min ->
            hour = h; minute = min; showTimePicker = false
        }, hour, minute, false).apply { setOnCancelListener { showTimePicker = false } }.show()
    }

    val ampm = if (hour < 12) "AM" else "PM"
    val hour12 = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
    val timeStr = String.format("%d:%02d %s", hour12, minute, ampm)
    val dateStr = String.format("%s %d, %d",
        java.text.SimpleDateFormat("MMM", java.util.Locale.US).format(cal.apply { set(year, month, day) }.time),
        day, year)

    AlertDialog(
        onDismissRequest = { vm.closeStartDialog() },
        modifier = Modifier.fillMaxWidth(),
        containerColor = BgDeep,
        title = {
            Column {
                Text("SET START DATE", style = MaterialTheme.typography.labelMedium, color = BloodBright, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                Text("When did your count begin?", style = MaterialTheme.typography.titleLarge, color = TextPrimary, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column {
                Text("Backdate if you were already clean before you found this app.", style = MaterialTheme.typography.bodyMedium, color = TextSoft)
                Spacer(Modifier.height(12.dp))
                Box(modifier = Modifier.fillMaxWidth().border(1.dp, LineStrong).clickable { showDatePicker = true }.padding(12.dp)) {
                    Text("📅 $dateStr", style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
                }
                Spacer(Modifier.height(6.dp))
                Box(modifier = Modifier.fillMaxWidth().border(1.dp, LineStrong).clickable { showTimePicker = true }.padding(12.dp)) {
                    Text("🕐 $timeStr", style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val c = Calendar.getInstance()
                c.set(year, month, day, hour, minute, 0)
                vm.setStart(c.timeInMillis)
            }) { Text("SET START", color = BloodBright, fontWeight = FontWeight.Bold) }
        },
        dismissButton = {
            TextButton(onClick = { vm.closeStartDialog() }) { Text("CANCEL", color = TextSoft) }
        }
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Reset dialog
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun ResetDialog(vm: AppViewModel) {
    AlertDialog(
        onDismissRequest = { vm.closeResetDialog() },
        containerColor = BgDeep,
        title = {
            Column {
                Text(
                    "⚠ ERASE ALL DATA",
                    style = MaterialTheme.typography.labelMedium,
                    color = BloodBright,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "This cannot be undone.",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Text(
                "Every entry, every count, every note — gone.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSoft
            )
        },
        confirmButton = {
            TextButton(onClick = { vm.resetAll() }) {
                Text("ERASE EVERYTHING", color = BloodBright, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = { vm.closeResetDialog() }) {
                Text("KEEP RECORD", color = TextSoft)
            }
        }
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Integer field — small numeric stepper
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun IntField(label: String, value: Int, min: Int, max: Int, onChange: (Int) -> Unit) {
    Column(
        modifier = Modifier
            .width(48.dp)
            .border(1.dp, LineStrong, RectangleShape)
            .background(Color(0x40000000))
            .padding(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp),
            color = TextFaint
        )
        Text(
            value.toString(),
            style = MaterialTheme.typography.labelMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )
        Row {
            TextButton(
                onClick = { if (value > min) onChange(value - 1) },
                modifier = Modifier.size(20.dp),
                contentPadding = PaddingValues(0.dp)
            ) { Text("−", color = BloodBright, fontSize = 14.sp) }
            TextButton(
                onClick = { if (value < max) onChange(value + 1) },
                modifier = Modifier.size(20.dp),
                contentPadding = PaddingValues(0.dp)
            ) { Text("+", color = BloodBright, fontSize = 14.sp) }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Dev Info Dialog — opened by the ℹ icon in the topbar
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun DevInfoDialog(vm: AppViewModel) {
    val ctx = LocalContext.current
    AlertDialog(
        onDismissRequest = { vm.closeDevInfoDialog() },
        containerColor = BgDeep,
        title = {
            Text(
                "DEVELOPED BY",
                style = MaterialTheme.typography.labelMedium,
                color = BloodBright,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    "One Man Studios",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(12.dp))
                TextButton(
                    onClick = {
                        ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://comfyinsta.neocities.org")).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                    },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("comfyinsta.neocities.org", style = MaterialTheme.typography.labelLarge, color = AmberBright, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(4.dp))
                TextButton(
                    onClick = {
                        ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/one-man-studios")).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                    },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("github.com/one-man-studios", style = MaterialTheme.typography.labelLarge, color = AmberBright, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(12.dp))
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(LineStrong))
                Spacer(Modifier.height(8.dp))
                Text(
                    "All data stored locally on your device. No server. No tracking. Fully offline.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextDim
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { vm.closeDevInfoDialog() }) {
                Text("CLOSE", color = BloodBright, fontWeight = FontWeight.Bold)
            }
        }
    )
}
