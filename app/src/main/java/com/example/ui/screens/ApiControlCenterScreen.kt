package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ai.AICapability
import com.example.data.ai.ConnectionStatus
import com.example.data.ai.ProviderDirectory
import com.example.data.ai.ProviderMetadata
import com.example.data.model.AIProviderConfigEntity
import com.example.ui.viewmodel.StudioViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ApiControlCenterScreen(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val providers by viewModel.providers.collectAsState()
    val capabilityRouting by viewModel.capabilityRouting.collectAsState()
    val testingProviderId by viewModel.testingProviderId.collectAsState()
    val testResults by viewModel.providerTestResults.collectAsState()

    var selectedFilterTab by remember { mutableIntStateOf(0) } // 0: All, 1: Connected, 2: Not Configured, 3: Routing
    var providerToRemoveKey by remember { mutableStateOf<AIProviderConfigEntity?>(null) }

    // Merge static metadata with Room DB state
    val providerItems = remember(providers) {
        ProviderDirectory.ALL_PROVIDERS.map { meta ->
            val entity = providers.find { it.providerId == meta.id } ?: AIProviderConfigEntity(
                providerId = meta.id,
                name = meta.name,
                modelId = meta.defaultModel,
                capabilities = meta.capabilities.joinToString(",") { it.name },
                documentationUrl = meta.documentationUrl,
                requiresBillingWarning = meta.billingNotice
            )
            Pair(meta, entity)
        }
    }

    val connectedCount = providerItems.count { it.second.lastStatus.startsWith("Connected") }
    val notConfiguredCount = providerItems.count { it.second.apiKey.isEmpty() && it.second.maskedKey.isEmpty() }

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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CloudQueue, contentDescription = null, tint = Color(0xFF00F0FF), modifier = Modifier.size(22.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "API CONTROL CENTER",
                            color = Color(0xFF00F0FF),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                    }
                    Surface(color = Color(0xFF132238), shape = RoundedCornerShape(4.dp)) {
                        Text(
                            text = "13 PROVIDERS",
                            color = Color(0xFF00F0FF),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Configure live credentials, probe official API endpoints, inspect rate limits, and route models by capability.",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp
                )
            }
        }

        // Summary Metric Strip
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    color = Color(0xFF0B1917),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).border(1.dp, Color(0xFF06D6A0).copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("ONLINE", color = Color(0xFF06D6A0), fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        Text("$connectedCount Verified", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Surface(
                    color = Color(0xFF131826),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).border(1.dp, Color(0xFF1E293B), RoundedCornerShape(8.dp))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("UNCONFIGURED", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        Text("$notConfiguredCount Awaiting", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Surface(
                    color = Color(0xFF1A1326),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).border(1.dp, Color(0xFF9D4EDD).copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("DISPATCH", color = Color(0xFF9D4EDD), fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        Text("5 Engines", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Filter / Routing Tabs
        item {
            TabRow(
                selectedTabIndex = selectedFilterTab,
                containerColor = Color(0xFF0E1422),
                contentColor = Color(0xFF00F0FF),
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedFilterTab]),
                        color = Color(0xFF00F0FF)
                    )
                },
                modifier = Modifier.border(1.dp, Color(0xFF1E293B), RoundedCornerShape(8.dp))
            ) {
                listOf("All Providers (13)", "Routing Matrix", "Connected ($connectedCount)", "Pending ($notConfiguredCount)").forEachIndexed { index, title ->
                    Tab(
                        selected = selectedFilterTab == index,
                        onClick = { selectedFilterTab = index },
                        text = { Text(title, fontSize = 11.sp, fontWeight = if (selectedFilterTab == index) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }
        }

        // View 1: Routing Matrix (Capability Routing)
        if (selectedFilterTab == 1) {
            item {
                CapabilityRoutingPanel(
                    capabilityRouting = capabilityRouting,
                    providers = providerItems.map { it.second },
                    onSetRouting = { cap, provId -> viewModel.setCapabilityRouting(cap, provId) }
                )
            }
        } else {
            // View 2: Provider Cards
            val filtered = providerItems.filter { (meta, entity) ->
                when (selectedFilterTab) {
                    2 -> entity.lastStatus.startsWith("Connected")
                    3 -> entity.apiKey.isEmpty() && entity.maskedKey.isEmpty()
                    else -> true
                }
            }

            items(filtered) { (meta, entity) ->
                ProviderCard(
                    meta = meta,
                    entity = entity,
                    isTesting = testingProviderId == meta.id,
                    lastTestResult = testResults[meta.id],
                    onSaveKey = { newKey, selectedModel ->
                        viewModel.saveProviderKey(meta.id, newKey, selectedModel)
                    },
                    onTestConnection = {
                        viewModel.testProvider(meta.id)
                    },
                    onSetDefault = {
                        viewModel.setDefaultProvider(meta.id)
                    },
                    onRemoveKey = {
                        providerToRemoveKey = entity
                    }
                )
            }
        }
    }

    // Remove Confirmation Dialog
    if (providerToRemoveKey != null) {
        val target = providerToRemoveKey!!
        AlertDialog(
            onDismissRequest = { providerToRemoveKey = null },
            containerColor = Color(0xFF0D1424),
            title = { Text("Remove Credentials", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Are you sure you want to remove API credentials for ${target.name}? It will no longer be available for anime generation workflows.",
                    color = Color(0xFFE2E8F0),
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.removeProviderKey(target.providerId)
                        providerToRemoveKey = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF3366), contentColor = Color.White)
                ) {
                    Text("Remove Key")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { providerToRemoveKey = null }) {
                    Text("Cancel", color = Color.White)
                }
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProviderCard(
    meta: ProviderMetadata,
    entity: AIProviderConfigEntity,
    isTesting: Boolean,
    lastTestResult: com.example.data.ai.ProviderTestResult?,
    onSaveKey: (String, String) -> Unit,
    onTestConnection: () -> Unit,
    onSetDefault: () -> Unit,
    onRemoveKey: () -> Unit
) {
    var isEditingKey by remember(entity.apiKey, entity.maskedKey) {
        mutableStateOf(entity.apiKey.isEmpty() && entity.maskedKey.isEmpty())
    }
    var keyInput by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var selectedModel by remember(entity.modelId) {
        mutableStateOf(if (entity.modelId.isNotBlank()) entity.modelId else meta.defaultModel)
    }

    val isConnected = entity.lastStatus.startsWith("Connected")
    val hasKey = entity.apiKey.isNotEmpty() || entity.maskedKey.isNotEmpty()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0D1424), RoundedCornerShape(12.dp))
            .border(
                1.dp,
                if (isConnected) Color(0xFF06D6A0) else if (entity.isDefault) Color(0xFF00F0FF) else Color(0xFF1E293B),
                RoundedCornerShape(12.dp)
            )
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = meta.name,
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "by ${meta.company}",
                            color = Color(0xFF64748B),
                            fontSize = 11.sp
                        )
                        if (entity.isDefault) {
                            Spacer(Modifier.width(6.dp))
                            Surface(color = Color(0x3300F0FF), shape = RoundedCornerShape(4.dp)) {
                                Text("DEFAULT", color = Color(0xFF00F0FF), fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                            }
                        }
                    }
                }

                // Status Badge
                Surface(
                    color = when {
                        isConnected -> Color(0xFF0A261C)
                        entity.lastStatus.contains("Invalid") -> Color(0xFF261014)
                        entity.lastStatus.contains("Rate") -> Color(0xFF2B200E)
                        hasKey -> Color(0xFF162033)
                        else -> Color(0xFF1A1E29)
                    },
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.border(
                        1.dp,
                        when {
                            isConnected -> Color(0xFF06D6A0)
                            entity.lastStatus.contains("Invalid") -> Color(0xFFFF3366)
                            entity.lastStatus.contains("Rate") -> Color(0xFFFFB703)
                            hasKey -> Color(0xFF00F0FF)
                            else -> Color(0xFF334155)
                        },
                        RoundedCornerShape(6.dp)
                    )
                ) {
                    Text(
                        text = entity.lastStatus,
                        color = when {
                            isConnected -> Color(0xFF06D6A0)
                            entity.lastStatus.contains("Invalid") -> Color(0xFFFF3366)
                            entity.lastStatus.contains("Rate") -> Color(0xFFFFB703)
                            hasKey -> Color(0xFF00F0FF)
                            else -> Color(0xFF94A3B8)
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            // Description
            Text(
                text = meta.description,
                color = Color(0xFF94A3B8),
                fontSize = 12.sp,
                lineHeight = 16.sp
            )

            // Capability Tags
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                meta.capabilities.forEach { cap ->
                    Surface(
                        color = Color(cap.badgeColor).copy(alpha = 0.15f),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.border(1.dp, Color(cap.badgeColor).copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                    ) {
                        Text(
                            text = cap.label,
                            color = Color(cap.badgeColor),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Billing Notice / Permissions Warning
            if (meta.billingNotice.isNotBlank()) {
                Surface(
                    color = Color(0xFF1E170E),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFF5A4114), RoundedCornerShape(6.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFFFFB703), modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = meta.billingNotice,
                            color = Color(0xFFFFE082),
                            fontSize = 10.sp,
                            lineHeight = 14.sp
                        )
                    }
                }
            }

            // Model Selector Dropdown/Chips
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "TARGET MODEL SELECTION",
                    color = Color(0xFF64748B),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    meta.verifiedModels.forEach { modelName ->
                        val isChosen = selectedModel == modelName
                        FilterChip(
                            selected = isChosen,
                            onClick = {
                                selectedModel = modelName
                                onSaveKey(if (isEditingKey) keyInput else entity.apiKey, modelName)
                            },
                            label = { Text(modelName, fontSize = 10.sp, fontFamily = FontFamily.Monospace) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF00F0FF),
                                selectedLabelColor = Color.Black,
                                containerColor = Color(0xFF131A29),
                                labelColor = Color.White
                            )
                        )
                    }
                }
            }

            // Credential Management Area
            if (!isEditingKey && hasKey) {
                // Key is saved and masked: NEVER reveal full plaintext secret again
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF080C14), RoundedCornerShape(8.dp))
                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF06D6A0), modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Stored Secret",
                                color = Color(0xFF64748B),
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = if (entity.maskedKey.isNotBlank()) entity.maskedKey else "••••••••••••••••",
                                color = Color(0xFFE2E8F0),
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(
                            onClick = {
                                keyInput = ""
                                isEditingKey = true
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Replace Key", tint = Color(0xFF00F0FF), modifier = Modifier.size(16.dp))
                        }
                        IconButton(
                            onClick = onRemoveKey,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Remove Key", tint = Color(0xFFFF3366), modifier = Modifier.size(16.dp))
                        }
                    }
                }
            } else {
                // Edit / Add Key Input Field
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(
                        value = keyInput,
                        onValueChange = { keyInput = it },
                        placeholder = { Text(meta.keyPlaceholder, color = Color(0xFF475569), fontSize = 12.sp) },
                        label = { Text("Enter ${meta.name} API Key") },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (passwordVisible) "Hide key" else "Show key",
                                    tint = Color(0xFF94A3B8)
                                )
                            }
                        },
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

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = meta.keyPrefixHelp,
                            color = Color(0xFF64748B),
                            fontSize = 10.sp
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            if (hasKey) {
                                OutlinedButton(
                                    onClick = { isEditingKey = false },
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Text("Cancel", fontSize = 11.sp)
                                }
                            }
                            Button(
                                onClick = {
                                    if (keyInput.isNotBlank()) {
                                        onSaveKey(keyInput, selectedModel)
                                        isEditingKey = false
                                    }
                                },
                                enabled = keyInput.isNotBlank(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00F0FF), contentColor = Color.Black),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text("Save & Verify", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Connection Test Results Display
            if (lastTestResult != null && lastTestResult.providerId == meta.id) {
                Surface(
                    color = if (lastTestResult.status == ConnectionStatus.CONNECTED) Color(0xFF0A241C) else Color(0xFF261014),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (lastTestResult.status == ConnectionStatus.CONNECTED) Icons.Default.CheckCircle else Icons.Default.ErrorOutline,
                                contentDescription = null,
                                tint = if (lastTestResult.status == ConnectionStatus.CONNECTED) Color(0xFF06D6A0) else Color(0xFFFF3366),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = lastTestResult.summary,
                                color = if (lastTestResult.status == ConnectionStatus.CONNECTED) Color(0xFF06D6A0) else Color(0xFFFF3366),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        if (lastTestResult.latencyMs > 0) {
                            Text(
                                text = "Roundtrip Latency: ${lastTestResult.latencyMs}ms • HTTP ${lastTestResult.rawStatusCode}",
                                color = Color(0xFF94A3B8),
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(start = 20.dp)
                            )
                        }
                    }
                }
            }

            // Bottom Actions Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onSetDefault,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text(if (entity.isDefault) "Default Engine" else "Set as Default", fontSize = 10.sp)
                }

                Button(
                    onClick = onTestConnection,
                    enabled = !isTesting && hasKey,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isConnected) Color(0xFF162B22) else Color(0xFF1E293B),
                        contentColor = if (isConnected) Color(0xFF06D6A0) else Color.White
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    if (isTesting) {
                        CircularProgressIndicator(modifier = Modifier.size(14.dp), color = Color(0xFF00F0FF), strokeWidth = 2.dp)
                        Spacer(Modifier.width(6.dp))
                        Text("Probing API...", fontSize = 10.sp)
                    } else {
                        Icon(Icons.Default.NetworkCheck, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Test Connection", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun CapabilityRoutingPanel(
    capabilityRouting: Map<String, String>,
    providers: List<AIProviderConfigEntity>,
    onSetRouting: (String, String) -> Unit
) {
    val capabilities = listOf(
        AICapability.TEXT to "Text & Narrative Scriptwriting",
        AICapability.IMAGE to "Character Keyframes & 2D Sheets",
        AICapability.VIDEO to "Video Generation & Camera Motion",
        AICapability.ASSETS_3D to "3D Character Meshes & Retopology",
        AICapability.VOICE to "Voice Synthesis & Dialogue Acting"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0D1424), RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column {
            Text(
                text = "PRODUCTION CAPABILITY ROUTING MATRIX",
                color = Color(0xFF00F0FF),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "Select which verified provider powers each department of the anime pipeline.",
                color = Color(0xFF94A3B8),
                fontSize = 11.sp
            )
        }

        capabilities.forEach { (cap, desc) ->
            val assignedProviderId = capabilityRouting[cap.name] ?: when (cap) {
                AICapability.TEXT -> "gemini"
                AICapability.IMAGE -> "stability_ai"
                AICapability.VIDEO -> "runway"
                AICapability.ASSETS_3D -> "tripo_ai"
                AICapability.VOICE -> "elevenlabs"
            }

            val compatibleProviders = ProviderDirectory.ALL_PROVIDERS.filter {
                it.capabilities.contains(cap)
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF080C14), RoundedCornerShape(8.dp))
                    .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(8.dp))
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(cap.label, color = Color(cap.badgeColor), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(desc, color = Color(0xFF64748B), fontSize = 9.sp)
                    }
                    val assignedMeta = ProviderDirectory.getMetadata(assignedProviderId)
                    Surface(color = Color(0xFF132238), shape = RoundedCornerShape(4.dp)) {
                        Text(
                            text = assignedMeta?.name ?: assignedProviderId,
                            color = Color(0xFF00F0FF),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    compatibleProviders.forEach { provMeta ->
                        val isSelected = assignedProviderId == provMeta.id
                        val provEntity = providers.find { it.providerId == provMeta.id }
                        val isConfigured = provEntity != null && (provEntity.apiKey.isNotBlank() || provEntity.maskedKey.isNotBlank())

                        FilterChip(
                            selected = isSelected,
                            onClick = { onSetRouting(cap.name, provMeta.id) },
                            label = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (isConfigured) {
                                        Box(modifier = Modifier.size(5.dp).background(Color(0xFF06D6A0), CircleShape))
                                        Spacer(Modifier.width(4.dp))
                                    }
                                    Text(provMeta.name, fontSize = 10.sp)
                                }
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(cap.badgeColor),
                                selectedLabelColor = Color.Black,
                                containerColor = Color(0xFF101726),
                                labelColor = Color.White
                            )
                        )
                    }
                }
            }
        }
    }
}
