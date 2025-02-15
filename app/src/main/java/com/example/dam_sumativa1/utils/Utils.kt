package com.example.dam_sumativa1.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun manejarResultado(
    scope: CoroutineScope,
    operacion: suspend () -> Unit,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    scope.launch {
        try {
            operacion()
            onSuccess()
        } catch (e: Exception) {
            onError(e.message ?: "Error desconocido")
        }
    }
}