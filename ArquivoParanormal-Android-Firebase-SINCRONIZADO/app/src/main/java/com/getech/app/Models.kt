package com.getech.app

enum class Perfil { CLIENTE, GESTOR }

data class Usuario(
    val nome: String,
    val email: String,
    val senha: String,
    val perfil: Perfil,
    val foto: String = ""
)

data class Sessao(
    val nome: String,
    val email: String,
    val perfil: Perfil,
    val foto: String = ""
)

data class Registro(
    val id: String,
    val campos: Map<String, String>
)

data class ChatMessage(val fromUser: Boolean, val text: String)
