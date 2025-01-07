package com.example.dam_sumativa1.modelo

data class User(
    val username: String,
    val email: String,
    val password: String
) {
    companion object {
        private val listaUsuarios = mutableListOf<User>()

        fun agregarUser(user: User) {
            listaUsuarios.add(user)
        }

        fun buscarUserPorUsername(username: String): User? {
            return listaUsuarios.find { it.username.equals(username, ignoreCase = true) }
        }

        fun buscarUserPorEmail(email: String): User? {
            return listaUsuarios.find { it.email.equals(email, ignoreCase = true) }
        }

        fun obtenerUsers(): List<User> = listaUsuarios
    }
}
