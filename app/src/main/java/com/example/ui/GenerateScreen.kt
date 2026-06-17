package com.example.ui

import android.Manifest
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.util.QrCodeGenerator
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun GenerateScreen(viewModel: MainViewModel) {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    var showCamera by remember { mutableStateOf(false) }
    
    var inputText by remember { mutableStateOf("") }
    var batchMode by remember { mutableStateOf(false) } 
    var fgColor by remember { mutableStateOf(android.graphics.Color.BLACK) }
    var bgColor by remember { mutableStateOf(android.graphics.Color.WHITE) }
    var cornerType by remember { mutableStateOf("SQUARE") }

    val recognizeState by viewModel.recognizeState.collectAsState()
    
    LaunchedEffect(recognizeState) {
        if (recognizeState is ObjectRecognizeState.Success) {
            inputText = (recognizeState as ObjectRecognizeState.Success).recognizedText
            showCamera = false
        }
    }

    if (showCamera) {
        if (cameraPermissionState.status.isGranted) {
            Box(Modifier.fillMaxSize()) {
                CameraPreview(onImageCaptured = { bitmap ->
                    viewModel.analyzeImageAndGenerateQrText(bitmap)
                })
                IconButton(
                    onClick = { showCamera = false },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(Icons.Default.Close, "Back", tint = Color.White)
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.Warning, contentDescription = "Camera Permission", modifier = Modifier.size(64.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text("Camera permission needed to scan objects.")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                    Text("Grant Permission")
                }
                TextButton(onClick = { showCamera = false }) {
                    Text("Back to Manual Entry")
                }
            }
        }
        return
    }

    val qrsToGenerate = if (batchMode) {
        val lines = inputText.split("\n").map { it.trim() }.filter { it.isNotEmpty() }
        if (lines.isEmpty()) listOf(inputText) else lines
    } else {
        listOf(inputText)
    }

    val generatedBitmaps = qrsToGenerate.mapNotNull { text ->
        if (text.isBlank()) null
        else QrCodeGenerator.generateQrCode(text, fgColor = fgColor, bgColor = bgColor)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(com.example.ui.theme.SurfaceBg)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("QR Studio", style = MaterialTheme.typography.titleLarge, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold, color = com.example.ui.theme.TextCol)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(8.dp).background(androidx.compose.ui.graphics.Color(0xFF22C55E), androidx.compose.foundation.shape.CircleShape))
                    Spacer(Modifier.width(4.dp))
                    Text("CLOUD SYNCED", fontSize = 10.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = androidx.compose.ui.graphics.Color.Gray, letterSpacing = 0.5.sp)
                }
            }
            IconButton(
                onClick = { /* Settings */ },
                modifier = Modifier
                    .size(40.dp)
                    .background(com.example.ui.theme.BentoLightPurple, androidx.compose.foundation.shape.CircleShape)
            ) {
                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = com.example.ui.theme.BentoDarkPurple, modifier = Modifier.size(20.dp))
            }
        }

        // Bento Grid Content
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // First Bento: Camera Hero Card
            Box(modifier = Modifier
                .fillMaxWidth()
                .background(com.example.ui.theme.BentoDark, RoundedCornerShape(32.dp))
                .border(4.dp, Color.White, RoundedCornerShape(32.dp))
                .padding(24.dp)
            ) {
                Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Bottom) {
                    Row(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(Modifier.size(6.dp).background(Color.Red, androidx.compose.foundation.shape.CircleShape))
                        Spacer(Modifier.width(8.dp))
                        Text("AI OBJECT MODE", color = Color.White, fontSize = 12.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, letterSpacing = 1.sp)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Scan to Vector", fontSize = 24.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Point at any object to generate professional high-res QR codes instantly.", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    if (recognizeState is ObjectRecognizeState.Loading) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().height(56.dp).background(com.example.ui.theme.Indigo600, RoundedCornerShape(16.dp)), horizontalArrangement = Arrangement.Center) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                            Spacer(Modifier.width(12.dp))
                            Text("Analyzing Object...", color = Color.White, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold)
                        }
                    } else {
                        Button(
                            onClick = { showCamera = true },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = com.example.ui.theme.Indigo600)
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White)
                            Spacer(Modifier.width(12.dp))
                            Text("Launch Scanner", fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold)
                        }
                    }
                }
            }

            // Text Input Bento
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(com.example.ui.theme.BentoSecondary, RoundedCornerShape(32.dp))
                    .border(1.dp, com.example.ui.theme.BentoSecondaryBorder, RoundedCornerShape(32.dp))
                    .padding(20.dp)
            ) {
                Text("QR Data", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text(if (batchMode) "Enter data (one per line)" else "Enter URL, text, etc.") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = com.example.ui.theme.BentoLightPurple,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    minLines = if (batchMode) 3 else 1
                )
            }

            // Secondary Grid Row
            Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Max), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // Batch Protocol
                Card(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = com.example.ui.theme.BentoSecondary),
                    border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.BentoSecondaryBorder)
                ) {
                    Column(Modifier.clickable { batchMode = !batchMode }.padding(20.dp).fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                        Box(Modifier.size(40.dp).background(Color.White, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.DynamicFeed, contentDescription = null, tint = com.example.ui.theme.Indigo600)
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Column {
                            Text("Batch Export", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, fontSize = 14.sp)
                            Text(if (batchMode) "Enabled" else "Line by Line", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                }

                // Cloud Library Info
                Card(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = com.example.ui.theme.BentoSecondary),
                    border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.BentoSecondaryBorder)
                ) {
                    Column(Modifier.padding(20.dp).fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                        Box(Modifier.size(40.dp).background(Color.White, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.CloudDone, contentDescription = null, tint = com.example.ui.theme.Indigo600)
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Column {
                            Text("Designs", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, fontSize = 14.sp)
                            Text("Synced", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                }
            }

            // Customization Toolbar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(32.dp))
                    .border(1.dp, com.example.ui.theme.BentoSecondaryBorder, RoundedCornerShape(32.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy((-8).dp)) {
                    val colors = listOf(Color(0xFF4F46E5L), Color(0xFFF97316L), Color(0xFF14B8A6L), Color.Black)
                    colors.forEach { c ->
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .border(2.dp, Color.White, androidx.compose.foundation.shape.CircleShape)
                                .background(c, androidx.compose.foundation.shape.CircleShape)
                                .clickable { fgColor = c.toArgb() }
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .border(2.dp, Color.White, androidx.compose.foundation.shape.CircleShape)
                            .background(com.example.ui.theme.BentoSecondary, androidx.compose.foundation.shape.CircleShape)
                            .clickable { fgColor = android.graphics.Color.BLACK },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("+", color = Color.Gray)
                    }
                }
                Box(
                    modifier = Modifier
                        .height(44.dp)
                        .background(com.example.ui.theme.BentoLightPurple, RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Brush, contentDescription = null, tint = com.example.ui.theme.BentoDarkPurple, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("CUSTOMIZE", color = com.example.ui.theme.BentoDarkPurple, fontSize = 12.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    }
                }
            }

            // Results Previews
            if (generatedBitmaps.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text("Previews", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(horizontal = 8.dp))
                generatedBitmaps.forEachIndexed { index, bmp ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.BentoSecondaryBorder)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                bitmap = bmp.asImageBitmap(),
                                contentDescription = "QR Code Preview",
                                modifier = Modifier.size(100.dp).background(Color.White, RoundedCornerShape(12.dp)).padding(8.dp)
                            )
                            Spacer(Modifier.width(16.dp))
                            Column {
                                Text("QR Code ${index + 1}", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                                Spacer(Modifier.height(8.dp))
                                Row {
                                    Button(
                                        onClick = {
                                            val currentText = qrsToGenerate.getOrNull(index) ?: ""
                                            viewModel.saveDesign("QR Code ${index + 1}", currentText, fgColor, bgColor, cornerType)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = com.example.ui.theme.BentoLightPurple),
                                        contentPadding = PaddingValues(horizontal = 12.dp),
                                        modifier = Modifier.height(36.dp)
                                    ) {
                                        Icon(Icons.Default.Save, contentDescription = "Save", tint = com.example.ui.theme.BentoDarkPurple, modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("Save", color = com.example.ui.theme.BentoDarkPurple, fontSize = 12.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}

