package com.arquivoparanormal.app.data

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

class ConfiguracoesApp(context: Context) {
    private val prefs = context.getSharedPreferences("arquivo_paranormal_config", Context.MODE_PRIVATE)

    var tema by mutableStateOf(prefs.getString("tema", "escuro") ?: "escuro")
        private set
    var corPrincipal by mutableStateOf(prefs.getString("corPrincipal", "vermelho") ?: "vermelho")
        private set
    var corHexPersonalizada by mutableStateOf(prefs.getString("corHexPersonalizada", "") ?: "")
        private set
    var escalaTexto by mutableStateOf(prefs.getFloat("escalaTexto", 1f).coerceIn(0.85f, 1.25f))
        private set
    var escalaIcones by mutableStateOf(prefs.getFloat("escalaIcones", 1f).coerceIn(0.85f, 1.25f))
        private set
    var animacoes by mutableStateOf(prefs.getBoolean("animacoes", true))
        private set
    var efeitos by mutableStateOf(prefs.getBoolean("efeitos", true))
        private set
    var altoContraste by mutableStateOf(prefs.getBoolean("altoContraste", false))
        private set

    private fun put(key: String, value: Any) {
        prefs.edit().apply {
            when (value) {
                is String -> putString(key, value)
                is Float -> putFloat(key, value)
                is Boolean -> putBoolean(key, value)
            }
        }.apply()
    }

    fun atualizarTema(valor: String) { tema = valor; put("tema", valor) }
    fun atualizarCorPrincipal(valor: String) { corPrincipal = valor; put("corPrincipal", valor) }
    fun atualizarCorHexPersonalizada(valor: String) {
        val normalizada = valor.trim().let { if (it.startsWith("#")) it else "#$it" }.uppercase()
        if (Regex("^#[0-9A-F]{6}$").matches(normalizada)) {
            corHexPersonalizada = normalizada
            corPrincipal = "personalizada"
            put("corHexPersonalizada", normalizada)
            put("corPrincipal", "personalizada")
        }
    }
    fun atualizarEscalaTexto(valor: Float) { val v = valor.coerceIn(0.85f, 1.25f); escalaTexto = v; put("escalaTexto", v) }
    fun atualizarEscalaIcones(valor: Float) { val v = valor.coerceIn(0.85f, 1.25f); escalaIcones = v; put("escalaIcones", v) }
    fun atualizarAnimacoes(valor: Boolean) { animacoes = valor; put("animacoes", valor) }
    fun atualizarEfeitos(valor: Boolean) { efeitos = valor; put("efeitos", valor) }
    fun atualizarAltoContraste(valor: Boolean) { altoContraste = valor; put("altoContraste", valor) }

    fun corSelecionada(): Color = when (corPrincipal) {
        "roxo" -> Color(0xFF8B5CF6)
        "azul" -> Color(0xFF3B82F6)
        "verde" -> Color(0xFF2E9D62)
        "laranja" -> Color(0xFFE67E22)
        "personalizada" -> runCatching { Color(android.graphics.Color.parseColor(corHexPersonalizada.ifBlank { "#B23A2E" })) }.getOrDefault(Color(0xFFB23A2E))
        else -> Color(0xFFB23A2E)
    }
}
