package com.example.dam_sumativa1.utils

fun manejarResultado(
    operacion: () -> Boolean,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    try {
        if (operacion()) {
            onSuccess()
        } else {
            onError("Ocurrió un error inesperado.")
        }
    } catch (e: Exception) {
        onError(e.message ?: "Error desconocido")
    }
}