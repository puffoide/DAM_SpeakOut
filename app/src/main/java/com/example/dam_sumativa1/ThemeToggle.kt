package com.example.dam_sumativa1

import android.os.Bundle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ThemeToggle(isDarkTheme: MutableState<Boolean>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = 16.dp,
                start = 16.dp,
                end = 16.dp
                ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            imageVector = if (isDarkTheme.value) Icons.Filled.NightsStay else Icons.Filled.WbSunny,
            contentDescription = if (isDarkTheme.value) "Modo Oscuro" else "Modo Claro",
            tint = if (isDarkTheme.value) Color(0xFFF1C07E) else Color(0xFFFFC107),
            modifier = Modifier.size(24.dp)
        )

        Switch(
            checked = isDarkTheme.value,
            onCheckedChange = { isDarkTheme.value = it }
        )
    }
}