package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.SavedDesign
import com.example.util.QrCodeGenerator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedScreen(viewModel: MainViewModel) {
    val items by viewModel.savedDesigns.collectAsStateWithLifecycle(initialValue = emptyList())

    Column(modifier = Modifier.fillMaxSize().background(com.example.ui.theme.SurfaceBg)) {
        CenterAlignedTopAppBar(
            title = { Text("Cloud Saved Designs") },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = com.example.ui.theme.SurfaceBg)
        )
        if (items.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No saved designs yet.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(items) { item ->
                    SavedDesignItem(item = item, onDelete = { viewModel.deleteDesign(item.id) })
                }
            }
        }
    }
}

@Composable
fun SavedDesignItem(item: SavedDesign, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val bmp = QrCodeGenerator.generateQrCode(
                content = item.data,
                size = 120,
                fgColor = item.fgColor,
                bgColor = item.bgColor
            )

            if (bmp != null) {
                Image(
                    bitmap = bmp.asImageBitmap(),
                    contentDescription = "Saved QR",
                    modifier = Modifier.size(80.dp)
                )
            } else {
                Box(modifier = Modifier.size(80.dp), contentAlignment = Alignment.Center) {
                    Text("Error")
                }
            }

            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, style = MaterialTheme.typography.titleMedium)
                Text(item.data, style = MaterialTheme.typography.bodySmall, maxLines = 1)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}
