package com.example.dam_sumativa1.modelo

data class User(
    val uid: String? = null,
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val savedTexts: List<String> = emptyList()
)