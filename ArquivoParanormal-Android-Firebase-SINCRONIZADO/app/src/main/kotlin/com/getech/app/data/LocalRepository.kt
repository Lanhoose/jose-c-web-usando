package com.getech.app.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

data class LocalUser(
    val name: String,
    val email: String,
    val password: String,
    val role: String,
    val photo: String = ""
)

data class ChatTicket(val name:String,val email:String,val problem:String,val date:String)

class LocalRepository(context: Context) {
    private val prefs = context.getSharedPreferences("getech_local", Context.MODE_PRIVATE)

    init { ensureDemoUsers() }

    private fun users(): MutableList<LocalUser> {
        val arr = JSONArray(prefs.getString("users", "[]"))
        return buildList {
            for(i in 0 until arr.length()) {
                val o=arr.getJSONObject(i)
                add(LocalUser(o.optString("name"),o.optString("email"),o.optString("password"),o.optString("role"),o.optString("photo")))
            }
        }.toMutableList()
    }
    private fun saveUsers(list: List<LocalUser>) {
        val arr=JSONArray()
        list.forEach { u -> arr.put(JSONObject().apply {
            put("name",u.name);put("email",u.email);put("password",u.password);put("role",u.role);put("photo",u.photo)
        }) }
        prefs.edit().putString("users",arr.toString()).apply()
    }
    private fun ensureDemoUsers() {
        val u=users()
        if(u.none { it.email=="gestor@getech.local" })
            saveUsers(u + LocalUser("Gestor GeTech","gestor@getech.local","123456","gestor"))
        val u2=users()
        if(u2.none { it.email=="cliente@getech.local" })
            saveUsers(u2 + LocalUser("Cliente GeTech","cliente@getech.local","123456","cliente"))
    }
    fun login(email:String,password:String): LocalUser? =
        users().find { it.email.equals(email.trim(),true) && it.password==password }

    fun register(name:String,email:String,password:String): String? {
        if(name.trim().isEmpty()) return "Informe seu nome."
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) return "Informe um e-mail válido."
        if(password.length < 6) return "A senha precisa ter pelo menos 6 caracteres."
        if(users().any { it.email.equals(email.trim(),true) }) return "Este e-mail já está cadastrado."
        saveUsers(users()+LocalUser(name.trim(),email.trim().lowercase(),password,"cliente"))
        return null
    }
    fun session(): LocalUser? {
        val email=prefs.getString("session_email",null) ?: return null
        return users().find { it.email==email }
    }
    fun setSession(u:LocalUser){prefs.edit().putString("session_email",u.email).apply()}
    fun logout(){prefs.edit().remove("session_email").apply()}

    fun addTicket(name:String,email:String,problem:String) {
        val arr=JSONArray(prefs.getString("tickets","[]"))
        arr.put(JSONObject().apply {
            put("id",UUID.randomUUID().toString());put("name",name);put("email",email);put("problem",problem)
            put("date",java.text.SimpleDateFormat("dd/MM/yyyy HH:mm",java.util.Locale("pt","BR")).format(java.util.Date()))
            put("status","novo")
        })
        prefs.edit().putString("tickets",arr.toString()).apply()
    }
    fun tickets(): List<ChatTicket> {
        val arr=JSONArray(prefs.getString("tickets","[]"))
        return buildList { for(i in arr.length()-1 downTo 0) {
            val o=arr.getJSONObject(i); add(ChatTicket(o.optString("name"),o.optString("email"),o.optString("problem"),o.optString("date")))
        }}
    }
    fun clearData(){prefs.edit().clear().apply();ensureDemoUsers()}
}
