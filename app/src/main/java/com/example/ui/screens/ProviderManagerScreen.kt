package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.StudioViewModel

@Composable
fun ProviderManagerScreen(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val providers by viewModel.providers.collectAsState()
    val testResult by viewModel.providerTestResult.collectAsState()
    val isTesting by viewModel.isTestingConnection.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF080C14))
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CloudQueue, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "AI PROVIDER MANAGER",
                        color = Color(0xFF00F0FF),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Configure and test real LLM reasoning, voice synthesis, video, and animation engine providers.",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp
                )
            }
        }

        // Live Test Banner if test conducted
        if (testResult != null) {
            item {
                val res = testResult!!
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (res.isSuccess) Color(0xFF0D241E) else Color(0xFF261014),
                            RoundedCornerShape(10.dp)
                        )
                        .border(
                            1.dp,
                            if (res.isSuccess) Color(0xFF06D6A0) else Color(0xFFFF3366),
                            RoundedCornerShape(10.dp)
                        )
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = if (res.isSuccess) Icons.Default.CheckCircle else Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = if (res.isSuccess) Color(0xFF06D6A0) else Color(0xFFFF3366)
                        )
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (res.isSuccess) "Connection Successful" else "Connection Failed",
                                color = if (res.isSuccess) Color(0xFF06D6A0) else Color(0xFFFF3366),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(res.message, color = Color(0xFFF1F5F9), fontSize = 12.sp)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "Model: ${res.resolvedModelId} • Latency: ${res.latencyMs}ms • Code: ${res.httpStatusCode}",
                                color = Color(0xFF94A3B8),
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        items(providers) { prov ->
            var customKey by remember(prov.providerId) { mutableStateOf(prov.apiKey) }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0D1424), RoundedCornerShape(12.dp))
                    .border(
                        1.dp,
                        if (prov.isDefault) Color(0xFF00F0FF) else Color(0xFF1E293B),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(prov.name, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                if (prov.isDefault) {
                                    Spacer(Modifier.width(6.dp))
                                    Surface(color = Color(0x3300F0FF), shape = RoundedCornerShape(4.dp)) {
                                        Text("DEFAULT", color = Color(0xFF00F0FF), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                    }
                                }
                            }
                            Text(
                                text = "Model ID: ${prov.modelId}",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Surface(
                            color = Color(0xFF1E293B),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = prov.lastStatus,
                                color = if (prov.lastStatus.startsWith("Active")) Color(0xFF06D6A0) else Color(0xFFFFD166),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    if (prov.providerId == "gemini") {
                        Text(
                            text = "Primary orchestration model for outlines, character profiles, camera directions, and scene breakdowns.",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )

                        OutlinedTextField(
                            value = customKey,
                            onValueChange = {
                                customKey = it
                                viewModel.updateProviderApiKey(prov, it)
                            },
                            label = { Text("API Key (Uses AI Studio Secrets if blank)") },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF00F0FF),
                                unfocusedBorderColor = Color(0xFF1E293B),
                                focusedContainerColor = Color(0xFF080C14),
                                unfocusedContainerColor = Color(0xFF080C14),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        Button(
                            onClick = { viewModel.testProviderConnection(prov, customKey) },
                            enabled = !isTesting,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF00F0FF),
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(38.dp)
                        ) {
                            if (isTesting) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.Black, strokeWidth = 2.dp)
                                Spacer(Modifier.width(6.dp))
                                Text("Connecting...", fontSize = 11.sp)
                            } else {
                                Icon(Icons.Default.NetworkCheck, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Test Connection Handshake", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else if (prov.providerId == "local_canvas") {
                        Text(
                            text = "Native Android Media Engine rendering 1080p anime frames, 3D vertex perspectives, and subtitles without external cloud dependencies.",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                        Surface(color = Color(0xFF142018), shape = RoundedCornerShape(6.dp)) {
                            Text(
                                text = "Zero quota cost • Local device hardware acceleration enabled",
                                color = Color(0xFF06D6A0),
                                fontSize = 10.sp,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    } else {
                        Text(
                            text = "Modular adapter ready for external API keys. Configure credentials when ready.",
                            color = Color(0xFF64748B),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}
