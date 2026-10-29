package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class Vec3(val x: Float, val y: Float, val z: Float) {
    operator fun plus(v: Vec3) = Vec3(x + v.x, y + v.y, z + v.z)
    operator fun minus(v: Vec3) = Vec3(x - v.x, y - v.y, z - v.z)
    operator fun times(s: Float) = Vec3(x * s, y * s, z * s)

    fun cross(v: Vec3) = Vec3(
        y * v.z - z * v.y,
        z * v.x - x * v.z,
        x * v.y - y * v.x
    )

    fun dot(v: Vec3) = x * v.x + y * v.y + z * v.z

    fun normalize(): Vec3 {
        val len = sqrt(x * x + y * y + z * z)
        return if (len > 0.0001f) Vec3(x / len, y / len, z / len) else Vec3(0f, 1f, 0f)
    }
}

data class Triangle3D(val a: Vec3, val b: Vec3, val c: Vec3, val baseColor: Color)

enum class ShadingMode {
    SHADED_3D,
    WIREFRAME,
    POINTS
}

@Composable
fun Interactive3DViewport(
    modelType: String = "CYBER_WARRIOR",
    modifier: Modifier = Modifier
) {
    var rotX by remember { mutableFloatStateOf(15f) }
    var rotY by remember { mutableFloatStateOf(45f) }
    var zoom by remember { mutableFloatStateOf(1.0f) }
    var autoSpin by remember { mutableStateOf(true) }
    var shadingMode by remember { mutableStateOf(ShadingMode.SHADED_3D) }
    var lightAngle by remember { mutableFloatStateOf(45f) }

    LaunchedEffect(autoSpin) {
        while (autoSpin) {
            delay(16)
            rotY = (rotY + 0.8f) % 360f
        }
    }

    val triangles = remember(modelType) {
        generateModelMesh(modelType)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(320.dp)
            .background(Color(0xFF090D14), RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { autoSpin = false },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            rotY = (rotY + dragAmount.x * 0.6f) % 360f
                            rotX = (rotX - dragAmount.y * 0.6f).coerceIn(-80f, 80f)
                        }
                    )
                }
        ) {
            val centerX = size.width / 2f
            val centerY = size.height / 2f
            val fov = 350f * zoom

            val radX = Math.toRadians(rotX.toDouble()).toFloat()
            val radY = Math.toRadians(rotY.toDouble()).toFloat()
            val cosX = cos(radX); val sinX = sin(radX)
            val cosY = cos(radY); val sinY = sin(radY)

            fun transform(v: Vec3): Vec3 {
                // Rotate around Y axis
                val x1 = v.x * cosY + v.z * sinY
                val y1 = v.y
                val z1 = -v.x * sinY + v.z * cosY

                // Rotate around X axis
                val x2 = x1
                val y2 = y1 * cosX - z1 * sinX
                val z2 = y1 * sinX + z1 * cosX

                // Translate into screen distance
                return Vec3(x2, y2, z2 + 3.0f)
            }

            // Directional Light Vector
            val radLight = Math.toRadians(lightAngle.toDouble()).toFloat()
            val lightDir = Vec3(cos(radLight), 0.7f, sin(radLight)).normalize()

            // Transform, sort by depth (Painter's algorithm)
            data class ProjectedTri(
                val p1: Offset,
                val p2: Offset,
                val p3: Offset,
                val depth: Float,
                val litColor: Color
            )

            val projectedList = mutableListOf<ProjectedTri>()

            for (tri in triangles) {
                val tA = transform(tri.a)
                val tB = transform(tri.b)
                val tC = transform(tri.c)

                // Clip if behind camera
                if (tA.z < 0.2f || tB.z < 0.2f || tC.z < 0.2f) continue

                // Normal vector calculation in camera space
                val edge1 = tB - tA
                val edge2 = tC - tA
                val normal = edge1.cross(edge2).normalize()

                // Backface culling
                val viewDir = Vec3(0f, 0f, 1f)
                if (normal.dot(viewDir) <= 0f) continue

                // Lighting calculation (ambient + diffuse)
                val diffuse = (normal.dot(lightDir)).coerceIn(0.15f, 1.0f)
                val base = tri.baseColor
                val litColor = Color(
                    red = (base.red * diffuse).coerceIn(0f, 1f),
                    green = (base.green * diffuse).coerceIn(0f, 1f),
                    blue = (base.blue * diffuse).coerceIn(0f, 1f),
                    alpha = 1f
                )

                val projA = Offset(centerX + (tA.x / tA.z) * fov, centerY - (tA.y / tA.z) * fov)
                val projB = Offset(centerX + (tB.x / tB.z) * fov, centerY - (tB.y / tB.z) * fov)
                val projC = Offset(centerX + (tC.x / tC.z) * fov, centerY - (tC.y / tC.z) * fov)

                val avgDepth = (tA.z + tB.z + tC.z) / 3f
                projectedList.add(ProjectedTri(projA, projB, projC, avgDepth, litColor))
            }

            // Draw grid in background
            val gridRadius = 1.6f
            for (i in -4..4) {
                val pA = transform(Vec3(i * 0.4f, -1.2f, -gridRadius))
                val pB = transform(Vec3(i * 0.4f, -1.2f, gridRadius))
                if (pA.z > 0.2f && pB.z > 0.2f) {
                    val oA = Offset(centerX + (pA.x / pA.z) * fov, centerY - (pA.y / pA.z) * fov)
                    val oB = Offset(centerX + (pB.x / pB.z) * fov, centerY - (pB.y / pB.z) * fov)
                    drawLine(Color(0xFF16233B), oA, oB, strokeWidth = 1f)
                }
            }

            // Sort triangles back to front
            projectedList.sortByDescending { it.depth }

            for (item in projectedList) {
                val path = Path().apply {
                    moveTo(item.p1.x, item.p1.y)
                    lineTo(item.p2.x, item.p2.y)
                    lineTo(item.p3.x, item.p3.y)
                    close()
                }

                when (shadingMode) {
                    ShadingMode.SHADED_3D -> {
                        drawPath(path, color = item.litColor)
                        drawPath(path, color = item.litColor.copy(alpha = 0.3f), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1f))
                    }
                    ShadingMode.WIREFRAME -> {
                        drawPath(path, color = Color(0xFF00F0FF), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5f))
                    }
                    ShadingMode.POINTS -> {
                        drawCircle(Color(0xFF00F0FF), radius = 2.5f, center = item.p1)
                        drawCircle(Color(0xFFFF007A), radius = 2.5f, center = item.p2)
                        drawCircle(Color(0xFF00F0FF), radius = 2.5f, center = item.p3)
                    }
                }
            }
        }

        // Viewport HUD Overlay
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(10.dp)
        ) {
            Surface(
                color = Color(0xCC0B111E),
                shape = RoundedCornerShape(6.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(Color(0xFF00F0FF), RoundedCornerShape(3.dp))
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "3D VIEWPORT // ${modelType.replace('_', ' ')}",
                        color = Color(0xFFE2E8F0),
                        fontSize = 11.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                    )
                }
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Rot: (${rotX.toInt()}°, ${rotY.toInt()}°) | Drag to orbit",
                color = Color(0xFF64748B),
                fontSize = 10.sp
            )
        }

        // Top right controls
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { autoSpin = !autoSpin },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Toggle Spin",
                    tint = if (autoSpin) Color(0xFF00F0FF) else Color(0xFF94A3B8)
                )
            }
            IconButton(
                onClick = {
                    rotX = 15f
                    rotY = 45f
                    zoom = 1f
                },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset Camera",
                    tint = Color(0xFF94A3B8)
                )
            }
        }

        // Bottom control toolbar
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color(0xE6080C14))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    AssistChip(
                        onClick = { shadingMode = ShadingMode.SHADED_3D },
                        label = { Text("Shaded", fontSize = 11.sp) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (shadingMode == ShadingMode.SHADED_3D) Color(0xFF00F0FF) else Color(0xFF1E293B),
                            labelColor = if (shadingMode == ShadingMode.SHADED_3D) Color.Black else Color.White
                        ),
                        modifier = Modifier.height(28.dp)
                    )
                    AssistChip(
                        onClick = { shadingMode = ShadingMode.WIREFRAME },
                        label = { Text("Wire", fontSize = 11.sp) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (shadingMode == ShadingMode.WIREFRAME) Color(0xFF00F0FF) else Color(0xFF1E293B),
                            labelColor = if (shadingMode == ShadingMode.WIREFRAME) Color.Black else Color.White
                        ),
                        modifier = Modifier.height(28.dp)
                    )
                    AssistChip(
                        onClick = { shadingMode = ShadingMode.POINTS },
                        label = { Text("Matrix", fontSize = 11.sp) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (shadingMode == ShadingMode.POINTS) Color(0xFF00F0FF) else Color(0xFF1E293B),
                            labelColor = if (shadingMode == ShadingMode.POINTS) Color.Black else Color.White
                        ),
                        modifier = Modifier.height(28.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.width(130.dp)
                ) {
                    Text("Zoom", color = Color(0xFF94A3B8), fontSize = 10.sp)
                    Spacer(Modifier.width(6.dp))
                    Slider(
                        value = zoom,
                        onValueChange = { zoom = it },
                        valueRange = 0.5f..2.0f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF00F0FF),
                            activeTrackColor = Color(0xFF00F0FF)
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

private fun generateModelMesh(type: String): List<Triangle3D> {
    val tris = mutableListOf<Triangle3D>()
    when (type) {
        "CYBER_MECHA" -> {
            val cyan = Color(0xFF00E5FF)
            val darkMetal = Color(0xFF2A364F)
            val redAccent = Color(0xFFFF2A6D)
            val gold = Color(0xFFFFC107)

            // Torso Core Cube
            addCube(tris, Vec3(0f, 0f, 0f), 0.5f, 0.6f, 0.4f, darkMetal)
            // Chest Reactor Plate
            addCube(tris, Vec3(0f, 0.1f, 0.22f), 0.25f, 0.25f, 0.08f, cyan)
            // Head Helmet
            addCube(tris, Vec3(0f, 0.55f, 0f), 0.35f, 0.35f, 0.35f, darkMetal)
            // Head Visor
            addCube(tris, Vec3(0f, 0.55f, 0.18f), 0.3f, 0.12f, 0.06f, redAccent)
            // Antennae
            addCube(tris, Vec3(-0.25f, 0.85f, 0f), 0.04f, 0.35f, 0.04f, gold)
            addCube(tris, Vec3(0.25f, 0.85f, 0f), 0.04f, 0.35f, 0.04f, gold)
            // Shoulders
            addCube(tris, Vec3(-0.55f, 0.25f, 0f), 0.35f, 0.3f, 0.45f, darkMetal)
            addCube(tris, Vec3(0.55f, 0.25f, 0f), 0.35f, 0.3f, 0.45f, darkMetal)
            // Arms
            addCube(tris, Vec3(-0.55f, -0.25f, 0f), 0.2f, 0.6f, 0.2f, cyan)
            addCube(tris, Vec3(0.55f, -0.25f, 0f), 0.2f, 0.6f, 0.2f, cyan)
            // Legs
            addCube(tris, Vec3(-0.2f, -0.85f, 0f), 0.22f, 0.8f, 0.25f, darkMetal)
            addCube(tris, Vec3(0.2f, -0.85f, 0f), 0.22f, 0.8f, 0.25f, darkMetal)
        }
        "MAGICAL_GIRL" -> {
            val violet = Color(0xFFB026FF)
            val pink = Color(0xFFFF529A)
            val white = Color(0xFFF1F5F9)
            val gold = Color(0xFFFFD700)

            // Head
            addCube(tris, Vec3(0f, 0.6f, 0f), 0.4f, 0.4f, 0.4f, white)
            // Twin Tails Hair left/right
            addCube(tris, Vec3(-0.4f, 0.65f, -0.1f), 0.15f, 0.7f, 0.15f, pink)
            addCube(tris, Vec3(0.4f, 0.65f, -0.1f), 0.15f, 0.7f, 0.15f, pink)
            // Eyes/Visor
            addCube(tris, Vec3(0f, 0.6f, 0.21f), 0.28f, 0.1f, 0.04f, violet)
            // Dress Torso
            addCube(tris, Vec3(0f, 0.1f, 0f), 0.35f, 0.45f, 0.3f, violet)
            // Ribbon / Star Bow
            addCube(tris, Vec3(0f, 0.25f, 0.18f), 0.18f, 0.18f, 0.08f, gold)
            // Wand Staff in hand
            addCube(tris, Vec3(0.45f, 0.2f, 0.2f), 0.04f, 1.1f, 0.04f, gold)
            addCube(tris, Vec3(0.45f, 0.8f, 0.2f), 0.18f, 0.18f, 0.18f, pink)
            // Floating Skirt Bell
            addPyramid(tris, Vec3(0f, -0.2f, 0f), 0.7f, 0.5f, pink)
            // Legs
            addCube(tris, Vec3(-0.12f, -0.75f, 0f), 0.12f, 0.7f, 0.12f, white)
            addCube(tris, Vec3(0.12f, -0.75f, 0f), 0.12f, 0.7f, 0.12f, white)
        }
        else -> {
            // CYBER_WARRIOR (Default)
            val armor = Color(0xFF1E293B)
            val cyanGlow = Color(0xFF00F0FF)
            val katanaSteel = Color(0xFFE2E8F0)
            val crimson = Color(0xFFFF0055)

            // Torso Jacket
            addCube(tris, Vec3(0f, 0.05f, 0f), 0.45f, 0.65f, 0.35f, armor)
            // Head with Cyber Mask
            addCube(tris, Vec3(0f, 0.6f, 0f), 0.35f, 0.35f, 0.35f, armor)
            // Cyan Optical Sensor Line
            addCube(tris, Vec3(0f, 0.6f, 0.19f), 0.3f, 0.08f, 0.04f, cyanGlow)
            // Scarf / Cyber Collar
            addCube(tris, Vec3(0f, 0.38f, 0.05f), 0.42f, 0.15f, 0.38f, crimson)
            // Cybernetic Right Arm
            addCube(tris, Vec3(0.42f, 0.05f, 0f), 0.16f, 0.6f, 0.16f, cyanGlow)
            // Cyber Katana Blade in Hand
            addCube(tris, Vec3(0.42f, 0.45f, 0.2f), 0.03f, 1.2f, 0.08f, katanaSteel)
            addCube(tris, Vec3(0.42f, -0.15f, 0.2f), 0.08f, 0.2f, 0.08f, crimson)
            // Left Arm
            addCube(tris, Vec3(-0.42f, 0.05f, 0f), 0.16f, 0.6f, 0.16f, armor)
            // Legs
            addCube(tris, Vec3(-0.18f, -0.75f, 0f), 0.18f, 0.8f, 0.2f, armor)
            addCube(tris, Vec3(0.18f, -0.75f, 0f), 0.18f, 0.8f, 0.2f, armor)
        }
    }
    return tris
}

private fun addCube(
    list: MutableList<Triangle3D>,
    center: Vec3,
    w: Float,
    h: Float,
    d: Float,
    color: Color
) {
    val hx = w / 2f
    val hy = h / 2f
    val hz = d / 2f

    // 8 vertices
    val p0 = center + Vec3(-hx, -hy, -hz)
    val p1 = center + Vec3(hx, -hy, -hz)
    val p2 = center + Vec3(hx, hy, -hz)
    val p3 = center + Vec3(-hx, hy, -hz)
    val p4 = center + Vec3(-hx, -hy, hz)
    val p5 = center + Vec3(hx, -hy, hz)
    val p6 = center + Vec3(hx, hy, hz)
    val p7 = center + Vec3(-hx, hy, hz)

    fun addQuad(a: Vec3, b: Vec3, c: Vec3, d: Vec3) {
        list.add(Triangle3D(a, b, c, color))
        list.add(Triangle3D(a, c, d, color))
    }

    // Front (Z+)
    addQuad(p4, p5, p6, p7)
    // Back (Z-)
    addQuad(p1, p0, p3, p2)
    // Top (Y+)
    addQuad(p3, p2, p6, p7)
    // Bottom (Y-)
    addQuad(p0, p1, p5, p4)
    // Left (X-)
    addQuad(p0, p4, p7, p3)
    // Right (X+)
    addQuad(p5, p1, p2, p6)
}

private fun addPyramid(
    list: MutableList<Triangle3D>,
    center: Vec3,
    baseSize: Float,
    height: Float,
    color: Color
) {
    val hs = baseSize / 2f
    val apex = center + Vec3(0f, height, 0f)
    val b0 = center + Vec3(-hs, 0f, -hs)
    val b1 = center + Vec3(hs, 0f, -hs)
    val b2 = center + Vec3(hs, 0f, hs)
    val b3 = center + Vec3(-hs, 0f, hs)

    list.add(Triangle3D(b0, b1, apex, color))
    list.add(Triangle3D(b1, b2, apex, color))
    list.add(Triangle3D(b2, b3, apex, color))
    list.add(Triangle3D(b3, b0, apex, color))
}
