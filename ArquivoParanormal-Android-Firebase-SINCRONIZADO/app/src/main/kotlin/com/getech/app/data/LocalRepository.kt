package com.getech.app.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

/**
 * Armazenamento local defensivo.
 * Nenhuma exceção de JSON/SharedPreferences deve derrubar a Activity.
 */
class LocalRepository(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences("getech_local_v5", Context.MODE_PRIVATE)

    data class User(
        val name: String,
        val email: String,
        val password: String,
        val role: String
    )

    data class Ticket(
        val id: String,
        val name: String,
        val email: String,
        val problem: String,
        val status: String,
        val createdAt: Long
    )

    private fun readArray(key: String): JSONArray {
        return try {
            JSONArray(prefs.getString(key, "[]") ?: "[]")
        } catch (_: Throwable) {
            prefs.edit().remove(key).apply()
            JSONArray()
        }
    }

    private fun writeArray(key: String, array: JSONArray) {
        runCatching { prefs.edit().putString(key, array.toString()).apply() }
    }

    fun users(): List<User> {
        val a = readArray("users")
        val out = mutableListOf<User>()
        for (i in 0 until a.length()) {
            runCatching {
                val o = a.optJSONObject(i) ?: return@runCatching
                val email = o.optString("email").trim().lowercase()
                if (email.isNotBlank()) {
                    out += User(
                        o.optString("name", "Usuário"),
                        email,
                        o.optString("password", ""),
                        o.optString("role", "cliente").ifBlank { "cliente" }
                    )
                }
            }
        }
        return out
    }

    fun ensureDemoUsers() {
        runCatching {
            val existing = users().toMutableList()
            val defaults = listOf(
                User("Gestor GeTech", "gestor@getech.local", "123456", "gestor"),
                User("Cliente GeTech", "cliente@getech.local", "123456", "cliente")
            )
            defaults.forEach { d ->
                if (existing.none { it.email == d.email }) existing += d
            }
            val a = JSONArray()
            existing.forEach {
                a.put(JSONObject().apply {
                    put("name", it.name)
                    put("email", it.email)
                    put("password", it.password)
                    put("role", if (it.role == "gestor") "gestor" else "cliente")
                })
            }
            writeArray("users", a)
        }
    }

    fun authenticate(email: String, password: String): User? {
        return runCatching {
            users().firstOrNull {
                it.email.equals(email.trim(), true) && it.password == password
            }
        }.getOrNull()
    }

    fun saveSession(user: User) {
        runCatching {
            prefs.edit()
                .putBoolean("logged_in", true)
                .putString("session_email", user.email)
                .putString("session_role", user.role)
                .apply()
        }
    }

    fun clearSession() {
        runCatching {
            prefs.edit()
                .remove("logged_in")
                .remove("session_email")
                .remove("session_role")
                .apply()
        }
    }

    fun currentUser(): User? {
        return runCatching {
            if (!prefs.getBoolean("logged_in", false)) return@runCatching null
            val email = prefs.getString("session_email", null) ?: return@runCatching null
            users().firstOrNull { it.email.equals(email, true) }
        }.getOrNull()
    }

    fun addTicket(name: String, email: String, problem: String): Ticket? {
        return runCatching {
            val ticket = Ticket(
                UUID.randomUUID().toString(),
                name.trim().ifBlank { "Cliente" },
                email.trim(),
                problem.trim(),
                "novo",
                System.currentTimeMillis()
            )
            val a = readArray("tickets")
            a.put(JSONObject().apply {
                put("id", ticket.id)
                put("name", ticket.name)
                put("email", ticket.email)
                put("problem", ticket.problem)
                put("status", ticket.status)
                put("createdAt", ticket.createdAt)
            })
            writeArray("tickets", a)
            ticket
        }.getOrNull()
    }

    fun tickets(): List<Ticket> {
        val a = readArray("tickets")
        val out = mutableListOf<Ticket>()
        for (i in 0 until a.length()) {
            runCatching {
                val o = a.optJSONObject(i) ?: return@runCatching
                out += Ticket(
                    o.optString("id"),
                    o.optString("name", "Cliente"),
                    o.optString("email"),
                    o.optString("problem"),
                    o.optString("status", "novo"),
                    o.optLong("createdAt", 0L)
                )
            }
        }
        return out
    }

    fun resetBrokenLocalData() {
        runCatching {
            prefs.edit()
                .remove("users")
                .remove("tickets")
                .remove("logged_in")
                .remove("session_email")
                .remove("session_role")
                .apply()
            ensureDemoUsers()
        }
    }

    fun login(email: String, password: String): User? = authenticate(email, password)
    fun setSession(user: User) = saveSession(user)
    fun session(): User? = currentUser()

    /** Retorna null quando cadastra com sucesso; caso contrário, retorna a mensagem do erro. */
    fun register(name: String, email: String, password: String): String? {
        return runCatching {
            val cleanEmail = email.trim().lowercase()
            require(name.trim().isNotBlank()) { "Informe seu nome." }
            require(cleanEmail.contains("@")) { "Informe um e-mail válido." }
            require(password.length >= 6) { "A senha precisa ter pelo menos 6 caracteres." }

            val current = users().toMutableList()
            require(current.none { it.email == cleanEmail }) { "Este e-mail já está cadastrado." }

            val user = User(name.trim(), cleanEmail, password, "cliente")
            current += user
            val a = JSONArray()
            current.forEach {
                a.put(JSONObject().apply {
                    put("name", it.name)
                    put("email", it.email)
                    put("password", it.password)
                    put("role", if (it.role == "gestor") "gestor" else "cliente")
                })
            }
            writeArray("users", a)
            null
        }.getOrElse { it.message ?: "Não foi possível criar a conta." }
    }

}
