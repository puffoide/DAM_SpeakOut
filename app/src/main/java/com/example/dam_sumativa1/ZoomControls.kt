package com.example.dam_sumativa1

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ZoomControls(globalScale: MutableState<Float>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        val canZoomIn = globalScale.value < 1.7f
        val canZoomOut = globalScale.value > 0.7f

        IconButton(
            onClick = { if (canZoomIn) globalScale.value += 0.1f },
            enabled = canZoomIn
        ) {
            Icon(
                imageVector = Icons.Filled.ZoomIn,
                contentDescription = "Aumentar Zoom",
                tint = if (canZoomIn) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                modifier = Modifier
                    .background(
                        if (canZoomIn) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                    )
                    .padding(6.dp)
                    .size(35.dp)

            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            onClick = { if (canZoomOut) globalScale.value -= 0.1f },
            enabled = canZoomOut
        ) {
            Icon(
                imageVector = Icons.Filled.ZoomOut,
                contentDescription = "Disminuir Zoom",
                tint = if (canZoomOut) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                modifier = Modifier
                    .background(
                        if (canZoomOut) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                        shape = MaterialTheme.shapes.small
                    )
                    .padding(6.dp)
                    .size(35.dp)
            )
        }

    }
}