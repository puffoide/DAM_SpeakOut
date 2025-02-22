package com.example.dam_sumativa1.services

import com.example.dam_sumativa1.modelo.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class UserService {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference.child("users")

    fun getCurrentUser(callback: (User?) -> Unit) {
        val firebaseUser: FirebaseUser? = auth.currentUser
        if (firebaseUser != null) {
            database.child(firebaseUser.uid).get()
                .addOnSuccessListener { snapshot ->
                    val user = snapshot.getValue(User::class.java)
                    callback(user)
                }
                .addOnFailureListener {
                    callback(null)
                }
        } else {
            callback(null)
        }
    }

    fun registrarUsuario(username: String, email: String, password: String, callback: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        val newUser = User(uid = userId, username = username, email = email, password = password)
                        database.child(userId).setValue(newUser)
                            .addOnSuccessListener { callback(true, null) }
                            .addOnFailureListener { e -> callback(false, e.message) }
                    } else {
                        callback(false, "Error al obtener el UID del usuario.")
                    }
                } else {
                    callback(false, task.exception?.message)
                }
            }
    }

    suspend fun buscarUserPorUsername(username: String): User? {
        return suspendCancellableCoroutine { continuation ->
            val database = FirebaseDatabase.getInstance().reference.child("users")
            database.orderByChild("username").equalTo(username).get()
                .addOnSuccessListener { snapshot ->
                    val user = snapshot.children.firstOrNull()?.getValue(User::class.java)
                    continuation.resume(user)
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
        }
    }


    suspend fun buscarUserPorEmail(email: String): User? {
        return suspendCancellableCoroutine { continuation ->
            val database = FirebaseDatabase.getInstance().reference.child("users")
            database.orderByChild("email").equalTo(email).get()
                .addOnSuccessListener { snapshot ->
                    val user = snapshot.children.firstOrNull()?.getValue(User::class.java)
                    continuation.resume(user)
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
        }
    }

    suspend fun buscarUsuarioPorIdentificador(identificador: String): User? {
        return suspendCancellableCoroutine { continuation ->
            val database = FirebaseDatabase.getInstance().reference.child("users")


            database.orderByChild("email").equalTo(identificador).get()
                .addOnSuccessListener { snapshot ->
                    val user = snapshot.children.firstOrNull()?.getValue(User::class.java)
                    if (user != null) {
                        continuation.resume(user)
                    } else {
                        database.orderByChild("username").equalTo(identificador).get()
                            .addOnSuccessListener { usernameSnapshot ->
                                val usernameUser = usernameSnapshot.children.firstOrNull()?.getValue(User::class.java)
                                continuation.resume(usernameUser)
                            }
                            .addOnFailureListener { e ->
                                continuation.resumeWithException(e)
                            }
                    }
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
        }
    }

    fun iniciarSesion(email: String, password: String, callback: (User?, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    getCurrentUser { user ->
                        if (user != null) {
                            callback(user, null)
                        } else {
                            callback(null, "No se encontró el usuario en la base de datos.")
                        }
                    }
                } else {
                    callback(null, "Usuario o contraseña incorrectos.")
                }
            }
    }


    fun cerrarSesion() {
        auth.signOut()
    }

    fun enviarCorreoRecuperacion(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                println("Correo de recuperación enviado a: $email")
            }
            .addOnFailureListener {
                println("Error al enviar correo de recuperación: ${it.message}")
            }
    }

    suspend fun verificarUsuarioEnFirebase(username: String, email: String) {
        val user = buscarUserPorUsername(username)
        if (user != null) {
            throw Exception("El nombre de usuario ya está en uso.")
        }

        val emailUser = buscarUserPorEmail(email)
        if (emailUser != null) {
            throw Exception("El correo ya está en uso.")
        }
    }

    suspend fun agregarTexto(uid: String, newText: String) {
        val userRef = database.child(uid)
        userRef.get().addOnSuccessListener { snapshot ->
            val user = snapshot.getValue(User::class.java)
            if (user != null) {
                val updatedTexts = user.savedTexts.toMutableList().apply { add(newText) }
                userRef.child("savedTexts").setValue(updatedTexts)
            }
        }
    }

    suspend fun eliminarTexto(uid: String, text: String) {
        val userRef = database.child(uid)
        userRef.get().addOnSuccessListener { snapshot ->
            val user = snapshot.getValue(User::class.java)
            if (user != null) {
                val updatedTexts = user.savedTexts.toMutableList().apply { remove(text) }
                userRef.child("savedTexts").setValue(updatedTexts)
            }
        }
    }

    suspend fun actualizarTexto(uid: String, oldText: String, newText: String) {
        val userRef = database.child(uid)
        userRef.get().addOnSuccessListener { snapshot ->
            val user = snapshot.getValue(User::class.java)
            if (user != null) {
                val updatedTexts = user.savedTexts.toMutableList().map { if (it == oldText) newText else it }
                userRef.child("savedTexts").setValue(updatedTexts)
            }
        }
    }

    fun obtenerTextos(userId: String, callback: (List<String>) -> Unit) {
        database.child(userId).child("savedTexts").get()
            .addOnSuccessListener { snapshot ->
                val texts = snapshot.children.mapNotNull { it.getValue(String::class.java) }
                callback(texts)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }

}