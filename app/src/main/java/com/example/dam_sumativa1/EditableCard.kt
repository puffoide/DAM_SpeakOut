package com.example.dam_sumativa1

import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.*

@Composable
fun EditableCard(
    text: String,
    onDelete: () -> Unit,
    onEdit: (String) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var editedText by remember { mutableStateOf(text) }
    val snackbarHostState = remember { SnackbarHostState() }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            if (isEditing) {
                TextField(
                    value = editedText,
                    onValueChange = { editedText = it },
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = {
                    onEdit(editedText)
                    isEditing = false
                }) {
                    Icon(Icons.Default.Check, contentDescription = "Guardar edición")
                }
            } else {
                Text(text, modifier = Modifier.weight(1f))
                IconButton(onClick = { isEditing = true }) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar")
                }
            }

            IconButton(onClick = {
                onDelete()
            }) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar")
            }
        }
    }
}
