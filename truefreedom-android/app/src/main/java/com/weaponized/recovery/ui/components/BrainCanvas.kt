package com.weaponized.recovery.ui.components

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.weaponized.recovery.data.BrainState
import kotlin.math.absoluteValue
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.hypot
import kotlin.math.sin
import kotlin.random.Random

private const val TAG = "BrainCanvas"

// ── Data classes for the neuron network ──────────────────────────────────────
private data class Neuron(
    var x: Float, var y: Float,
    val baseX: Float, val baseY: Float,
    val radius: Float,
    val dimmed: Boolean,
    val boundary: Boolean,
    val boundaryIdx: Int,
    var brightness: Float,
    var fireTimer: Float,
    var firePhase: Float,
    val connections: MutableList<Int>,
    val depth: Float
)

private data class Pulse(var from: Int, var to: Int, var progress: Float)

/**
 * A custom Canvas that draws the brain as a network of neurons and firing
 * signals, mirroring the JavaScript implementation in the web app.
 *
 * Neurons are placed via Poisson-disk sampling inside a parametric brain
 * silhouette; connections form the visible brain shape; pulses travel along
 * edges to simulate electrical activity. The state's `color`, `accent`,
 * `chaos`, `dimRatio`, `pulseSpeed`, and `fireChance` drive the look.
 */
@Composable
fun BrainCanvas(
    state: BrainState,
    modifier: Modifier = Modifier
) {
    var neurons by remember(state) { mutableStateOf<List<Neuron>>(emptyList()) }
    var pulses by remember(state) { mutableStateOf<List<Pulse>>(emptyList()) }
    var sizeW by remember { mutableStateOf(0f) }
    var sizeH by remember { mutableStateOf(0f) }

    // (Re)build the network whenever the state or canvas size changes
    LaunchedEffect(state, sizeW, sizeH) {
        if (sizeW <= 0f || sizeH <= 0f) return@LaunchedEffect
        neurons = buildNetwork(state, sizeW, sizeH)
        pulses = emptyList()
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(320.dp)
            .padding(0.dp)
    ) {
        sizeW = size.width
        sizeH = size.height

        // Fade-clear for subtle trails
        drawRect(color = Color.Black.copy(alpha = 0.32f))

        val cx = sizeW / 2f
        val cy = sizeH / 2f
        val scale = minOf(sizeW, sizeH) * 0.36f
        val t = System.nanoTime() / 1_000_000_000f
        val breathScale = 1f + sin(t * 0.7f) * 0.015f

        // Soft inner glow
        drawInnerGlow(state.color, cx, cy, scale)

        if (neurons.isEmpty()) return@Canvas

        // Update neuron positions (drift)
        val driftAmt = state.chaos * 1.2f
        val updatedNeurons = neurons.mapIndexed { i, n ->
            n.x = n.baseX + sin(t * 1.2f + i) * driftAmt
            n.y = n.baseY + cos(t * 1.0f + i * 1.1f) * driftAmt
            n.fireTimer -= 1f
            if (n.fireTimer <= 0f) {
                n.firePhase = 1f
                n.fireTimer = 80f + Random.nextFloat() * 180f
                if (n.connections.isNotEmpty() && Random.nextFloat() < state.fireChance * 8f) {
                    val targetIdx = n.connections[Random.nextInt(n.connections.size)]
                    if (targetIdx in neurons.indices) {
                        pulses = pulses + Pulse(i, targetIdx, 0f)
                    }
                }
            }
            if (n.firePhase > 0f) n.firePhase -= 0.035f
            n
        }

        // Sort neurons back-to-front for depth rendering
        val sorted = updatedNeurons.sortedByDescending { it.depth }

        // Draw dim connections (back layer)
        drawDimConnections(updatedNeurons, state.color)

        // Update + draw pulses
        val newPulses = mutableListOf<Pulse>()
        for (p in pulses) {
            p.progress += 0.022f * state.pulseSpeed
            if (p.progress >= 1f) continue
            if (p.from !in updatedNeurons.indices || p.to !in updatedNeurons.indices) continue
            val from = updatedNeurons[p.from]
            val to = updatedNeurons[p.to]
            val px = from.x + (to.x - from.x) * p.progress
            val py = from.y + (to.y - from.y) * p.progress
            drawPulse(px, py, state.accent, state.color)
            newPulses.add(p)
        }
        pulses = newPulses

        // Draw neurons (depth-sorted)
        for (n in sorted) {
            drawNeuron(n, state.color)
        }

        // Draw boundary silhouette (on top, for crisp bright edge)
        drawBoundary(updatedNeurons, state.color)
    }
}

// ── Network builder ───────────────────────────────────────────────────────────
private fun buildNetwork(state: BrainState, W: Float, H: Float): List<Neuron> {
    if (W <= 0f || H <= 0f) return emptyList()
    val cx = W / 2f
    val cy = H / 2f
    val scale = minOf(W, H) * 0.36f
    val out = mutableListOf<Neuron>()

    // 1) Boundary nodes — evenly spaced along the brain curve
    val boundaryCount = 72
    for (i in 0 until boundaryCount) {
        val tParam = i.toFloat() / boundaryCount
        val p = brainPoint(tParam, cx, cy, scale * 0.94f)
        val dimmed = Random.nextFloat() < state.dimRatio * 0.5f
        out.add(Neuron(
            x = p.x, y = p.y, baseX = p.x, baseY = p.y,
            radius = 2.0f + Random.nextFloat() * 0.5f,
            dimmed = dimmed,
            boundary = true,
            boundaryIdx = i,
            brightness = if (dimmed) 0.35f else 0.85f + Random.nextFloat() * 0.15f,
            fireTimer = Random.nextFloat() * 150f + 30f,
            firePhase = 0f,
            connections = mutableListOf(),
            depth = Random.nextFloat() * 0.6f
        ))
    }

    // 2) Interior nodes — Poisson-disk sampling
    val target = 42
    val minDist = scale * 0.19f
    val edgePad = 10f
    var attempts = 0
    val maxAttempts = target * 150
    while (out.size < boundaryCount + target && attempts < maxAttempts) {
        attempts++
        var x: Float; var y: Float
        if (out.size > boundaryCount && Random.nextFloat() < 0.85f) {
            val seed = out[boundaryCount + Random.nextInt(out.size - boundaryCount)]
            val ang = Random.nextFloat() * Math.PI.toFloat() * 2f
            val r = minDist * (1f + Random.nextFloat() * 1.4f)
            x = seed.baseX + cos(ang) * r
            y = seed.baseY + sin(ang) * r
        } else {
            x = cx + (Random.nextFloat() - 0.5f) * scale * 1.6f
            y = cy + (Random.nextFloat() - 0.5f) * scale * 1.6f
        }
        if (!isInsideBrain(cx, cy, scale - edgePad, x, y)) continue
        var ok = true
        for (k in boundaryCount until out.size) {
            val n = out[k]
            if (hypot(n.baseX - x, n.baseY - y) < minDist) { ok = false; break }
        }
        if (!ok) continue
        val dimmed = Random.nextFloat() < state.dimRatio
        out.add(Neuron(
            x = x, y = y, baseX = x, baseY = y,
            radius = 2.3f + Random.nextFloat() * 0.6f,
            dimmed = dimmed,
            boundary = false,
            boundaryIdx = -1,
            brightness = if (dimmed) 0.25f + Random.nextFloat() * 0.15f else 0.7f + Random.nextFloat() * 0.25f,
            fireTimer = Random.nextFloat() * 150f + 30f,
            firePhase = 0f,
            connections = mutableListOf(),
            depth = 0.2f + Random.nextFloat() * 0.8f
        ))
    }

    // 3) Build connections
    for (i in out.indices) {
        val n = out[i]
        if (n.boundary) {
            val next = (n.boundaryIdx + 1) % boundaryCount
            val prev = (n.boundaryIdx - 1 + boundaryCount) % boundaryCount
            val nextIdx = out.indexOfFirst { it.boundary && it.boundaryIdx == next }
            val prevIdx = out.indexOfFirst { it.boundary && it.boundaryIdx == prev }
            if (nextIdx >= 0) n.connections.add(nextIdx)
            if (prevIdx >= 0) n.connections.add(prevIdx)
            // 1 nearest interior
            var nearestI = -1
            var nearestD = Float.MAX_VALUE
            for (k in boundaryCount until out.size) {
                val d = hypot(out[k].baseX - n.baseX, out[k].baseY - n.baseY)
                if (d < nearestD) { nearestD = d; nearestI = k }
            }
            if (nearestI >= 0) n.connections.add(nearestI)
        }
    }
    for (i in out.indices) {
        val n = out[i]
        if (n.boundary) continue
        val dists = out.mapIndexed { j, o -> j to hypot(o.baseX - n.baseX, o.baseY - n.baseY) }
            .filter { it.first != i }
            .sortedBy { it.second }
            .take(3)
            .map { it.first }
        n.connections.addAll(dists)
    }
    return out
}

// ── Parametric brain shape ────────────────────────────────────────────────────
private fun brainPoint(t: Float, cx: Float, cy: Float, s: Float): Offset {
    val ang = t * Math.PI.toFloat() * 2f
    var r = s
    // 2-lobe bulge (hemispheres)
    r += s * 0.08f * cos(ang * 2f)
    // Deep top fissure (top is at ang = -PI/2)
    val topAng = -Math.PI.toFloat() / 2f
    var dang = ang - topAng
    while (dang > Math.PI.toFloat()) dang -= Math.PI.toFloat() * 2f
    while (dang < -Math.PI.toFloat()) dang += Math.PI.toFloat() * 2f
    val topFissure = exp(-((dang / 0.22f) * (dang / 0.22f)))
    r -= s * 0.22f * topFissure
    // Cerebellum taper at bottom
    val botAng = Math.PI.toFloat() / 2f
    var bdang = ang - botAng
    while (bdang > Math.PI.toFloat()) bdang -= Math.PI.toFloat() * 2f
    while (bdang < -Math.PI.toFloat()) bdang += Math.PI.toFloat() * 2f
    val bottomTaper = exp(-((bdang / 0.5f) * (bdang / 0.5f)))
    r -= s * 0.15f * bottomTaper
    // Frontal lobe bumps
    val frontalL = exp(-((dang + 0.45f) / 0.3f).let { it * it })
    val frontalR = exp(-((dang - 0.45f) / 0.3f).let { it * it })
    r += s * 0.06f * (frontalL + frontalR)
    // Temporal lobe bumps
    val tempR = exp(-((ang) / 0.4f).let { it * it })
    val tempL = exp(-((ang - Math.PI.toFloat()) / 0.4f).let { it * it })
    r += s * 0.05f * (tempR + tempL)
    // Organic wobble
    r += s * 0.01f * cos(ang * 7f)
    val x = cx + cos(ang) * r * 1.05f
    val y = cy + sin(ang) * r * 0.98f
    return Offset(x, y)
}

private fun isInsideBrain(cx: Float, cy: Float, s: Float, x: Float, y: Float): Boolean {
    // Sample 64 boundary points, ray-cast
    val pts = Array(64) { brainPoint(it.toFloat() / 64f, cx, cy, s) }
    var inside = false
    var j = pts.size - 1
    for (i in pts.indices) {
        val xi = pts[i].x; val yi = pts[i].y
        val xj = pts[j].x; val yj = pts[j].y
        if (((yi > y) != (yj > y)) && (x < (xj - xi) * (y - yi) / (yj - yi) + xi)) {
            inside = !inside
        }
        j = i
    }
    return inside
}

// ── Drawing helpers ──────────────────────────────────────────────────────────
private fun DrawScope.drawInnerGlow(color: Color, cx: Float, cy: Float, scale: Float) {
    val inner = scale * 0.1f
    val outer = scale * 1.15f
    // Approximate the radial gradient with concentric circles
    repeat(12) { i ->
        val frac = i / 11f
        val r = inner + (outer - inner) * frac
        val alpha = (0.08f * (1f - frac)).coerceAtLeast(0f)
        drawCircle(
            color = color.copy(alpha = alpha),
            radius = r,
            center = Offset(cx, cy)
        )
    }
}

private fun DrawScope.drawDimConnections(neurons: List<Neuron>, color: Color) {
    for (i in neurons.indices) {
        val n = neurons[i]
        for (j in n.connections) {
            if (j !in neurons.indices) continue
            val o = neurons[j]
            if (n.boundary && o.boundary) continue   // boundary drawn separately
            val dim = n.dimmed || o.dimmed
            val depthAvg = (n.depth + o.depth) / 2f
            val depthFade = 1f - depthAvg * 0.5f
            val a1 = (if (dim) 0.03f else 0.12f) * depthFade
            val a2 = (if (o.dimmed) 0.03f else 0.12f) * depthFade
            drawLine(
                color = color.copy(alpha = maxOf(a1, a2) * 0.8f),
                start = Offset(n.x, n.y),
                end = Offset(o.x, o.y),
                strokeWidth = 0.5f + depthFade * 0.3f,
                cap = StrokeCap.Round
            )
        }
    }
}

private fun DrawScope.drawPulse(px: Float, py: Float, accent: Color, color: Color) {
    // Outer glow
    repeat(6) { i ->
        val frac = i / 5f
        val r = 14f * (1f - frac)
        val alpha = 0.9f * (1f - frac)
        drawCircle(
            color = accent.copy(alpha = alpha * 0.4f),
            radius = r,
            center = Offset(px, py)
        )
    }
    // Bright white core
    drawCircle(color = Color.White, radius = 2.2f, center = Offset(px, py))
}

private fun DrawScope.drawNeuron(n: Neuron, color: Color) {
    val fireBoost = n.firePhase * 0.7f
    val depthFade = 1f - n.depth * 0.5f
    val baseBright = (n.brightness + fireBoost).coerceAtMost(1f) * depthFade
    val r = (n.radius + fireBoost * 1.3f) * depthFade

    // Halo (wide)
    drawCircle(
        color = if (n.dimmed) Color(0xFF5A4646).copy(alpha = baseBright * 0.12f)
                else color.copy(alpha = baseBright * 0.2f),
        radius = r * 5.5f,
        center = Offset(n.x, n.y)
    )
    // Mid glow
    drawCircle(
        color = if (n.dimmed) Color(0xFF6E5A5A).copy(alpha = baseBright * 0.35f)
                else color.copy(alpha = baseBright * 0.65f),
        radius = r * 2.8f,
        center = Offset(n.x, n.y)
    )
    // Solid core
    drawCircle(
        color = if (n.dimmed) Color(0xFF8C7878).copy(alpha = baseBright)
                else color.copy(alpha = baseBright),
        radius = r,
        center = Offset(n.x, n.y)
    )
    // White-hot center when firing
    if (n.firePhase > 0.3f) {
        drawCircle(
            color = Color.White.copy(alpha = n.firePhase * 0.9f * depthFade),
            radius = r * 0.5f,
            center = Offset(n.x, n.y)
        )
    }
    // Boundary nodes: extra bright pinpoint
    if (n.boundary && !n.dimmed) {
        drawCircle(
            color = Color.White.copy(alpha = (0.5f + fireBoost * 0.4f) * depthFade),
            radius = r * 0.35f,
            center = Offset(n.x, n.y)
        )
    }
}

private fun DrawScope.drawBoundary(neurons: List<Neuron>, color: Color) {
    for (i in neurons.indices) {
        val n = neurons[i]
        if (!n.boundary) continue
        for (j in n.connections) {
            if (j !in neurons.indices) continue
            val o = neurons[j]
            if (!o.boundary) continue
            // Outer glow
            drawLine(
                color = color.copy(alpha = 0.15f),
                start = Offset(n.x, n.y),
                end = Offset(o.x, o.y),
                strokeWidth = 5f,
                cap = StrokeCap.Round
            )
            // Core
            drawLine(
                color = color.copy(alpha = 0.8f),
                start = Offset(n.x, n.y),
                end = Offset(o.x, o.y),
                strokeWidth = 1.8f,
                cap = StrokeCap.Round
            )
        }
    }
}
