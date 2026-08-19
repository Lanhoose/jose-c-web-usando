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

    data class Machine(val id:String,val name:String,val code:String,val sector:String,val status:String,val location:String,val lastMaintenance:Long,val nextMaintenance:Long)
    data class InventoryItem(val id:String,val name:String,val code:String,var quantity:Int,val minimum:Int,val unit:String)
    data class Employee(val id:String,val name:String,val email:String,val department:String,val active:Boolean,val clockIn:Long?,val clockOut:Long?)
    data class WorkOrder(val id:String,val equipment:String,val description:String,val priority:String,val status:String,val responsible:String,val createdAt:Long)
    data class AuditLog(val id:String,val date:Long,val operator:String,val action:String,val detail:String,val severity:String)

    private fun readObjectArray(key:String):JSONArray = readArray(key)

    fun machines():List<Machine> = runCatching {
        val a=readObjectArray("machines"); buildList {
            for(i in 0 until a.length()) { val o=a.optJSONObject(i) ?: continue
                add(Machine(o.optString("id"),o.optString("name"),o.optString("code"),o.optString("sector"),
                    o.optString("status","Operacional"),o.optString("location"),o.optLong("lastMaintenance"),o.optLong("nextMaintenance")))
            }
        }
    }.getOrDefault(emptyList())

    fun addMachine(name:String,code:String,sector:String,location:String):Boolean = runCatching {
        val a=readObjectArray("machines")
        a.put(JSONObject().apply{put("id",UUID.randomUUID().toString());put("name",name);put("code",code);put("sector",sector);put("status","Operacional");put("location",location);put("lastMaintenance",System.currentTimeMillis());put("nextMaintenance",System.currentTimeMillis()+2592000000L)})
        writeArray("machines",a); addAudit("Cadastro","Equipamento $name cadastrado","info"); true
    }.getOrDefault(false)

    fun inventory():List<InventoryItem> = runCatching {
        val a=readObjectArray("inventory"); buildList {
            for(i in 0 until a.length()) { val o=a.optJSONObject(i) ?: continue
                add(InventoryItem(o.optString("id"),o.optString("name"),o.optString("code"),o.optInt("quantity"),o.optInt("minimum"),o.optString("unit","un")))
            }
        }
    }.getOrDefault(emptyList())

    fun seedOperationalData() {
        runCatching {
            if(machines().isEmpty()) {
                addMachine("Torno CNC Principal","CNC-001","Usinagem","Planta A")
                addMachine("Prensa Hidráulica","PH-014","Metalurgia","Planta B")
            }
            if(inventory().isEmpty()) {
                val a=JSONArray()
                listOf(
                    arrayOf("Rolamento 6205","ROL-6205",48,10,"un"),
                    arrayOf("Óleo hidráulico ISO 46","OLE-046",8,12,"L"),
                    arrayOf("Filtro industrial","FIL-112",23,8,"un")
                ).forEach { x -> a.put(JSONObject().apply{put("id",UUID.randomUUID().toString());put("name",x[0]);put("code",x[1]);put("quantity",x[2]);put("minimum",x[3]);put("unit",x[4])})}
                writeArray("inventory",a)
            }
        }
    }

    fun employees():List<Employee> = runCatching {
        val a=readObjectArray("employees"); buildList {
            for(i in 0 until a.length()) { val o=a.optJSONObject(i) ?: continue
                add(Employee(o.optString("id"),o.optString("name"),o.optString("email"),o.optString("department"),o.optBoolean("active",true),
                    if(o.has("clockIn")) o.optLong("clockIn") else null, if(o.has("clockOut")) o.optLong("clockOut") else null))
            }
        }
    }.getOrDefault(emptyList())

    fun workOrders():List<WorkOrder> = runCatching {
        val a=readObjectArray("orders"); buildList {
            for(i in 0 until a.length()) { val o=a.optJSONObject(i) ?: continue
                add(WorkOrder(o.optString("id"),o.optString("equipment"),o.optString("description"),o.optString("priority","Normal"),o.optString("status","Aberta"),o.optString("responsible"),o.optLong("createdAt")))
            }
        }
    }.getOrDefault(emptyList())

    fun addWorkOrder(equipment:String,description:String,priority:String,responsible:String):Boolean = runCatching {
        val a=readObjectArray("orders")
        a.put(JSONObject().apply{put("id","OS-"+System.currentTimeMillis().toString().takeLast(7));put("equipment",equipment);put("description",description);put("priority",priority);put("status","Aberta");put("responsible",responsible);put("createdAt",System.currentTimeMillis())})
        writeArray("orders",a); addAudit("Nova O.S.","Ordem para $equipment","info"); true
    }.getOrDefault(false)

    fun auditLogs():List<AuditLog> = runCatching {
        val a=readObjectArray("audit"); buildList {
            for(i in 0 until a.length()) { val o=a.optJSONObject(i) ?: continue
                add(AuditLog(o.optString("id"),o.optLong("date"),o.optString("operator"),o.optString("action"),o.optString("detail"),o.optString("severity","info")))
            }
        }.sortedByDescending { it.date }
    }.getOrDefault(emptyList())

    fun addAudit(action:String,detail:String,severity:String="info") {
        runCatching { val a=readObjectArray("audit"); a.put(JSONObject().apply{put("id",UUID.randomUUID().toString().take(8));put("date",System.currentTimeMillis());put("operator",session()?.name ?: "Sistema");put("action",action);put("detail",detail);put("severity",severity)});writeArray("audit",a) }
    }

    fun isDarkTheme():Boolean = prefs.getBoolean("dark_theme", true)
    fun setDarkTheme(value:Boolean) { runCatching { prefs.edit().putBoolean("dark_theme",value).apply() } }

}
