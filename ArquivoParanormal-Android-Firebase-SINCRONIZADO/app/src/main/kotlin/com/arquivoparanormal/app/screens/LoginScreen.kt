package com.arquivoparanormal.app.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arquivoparanormal.app.data.Autenticacao
import com.arquivoparanormal.app.ui.Campo
import com.arquivoparanormal.app.ui.Painel
import com.arquivoparanormal.app.ui.Perigo
import com.arquivoparanormal.app.ui.Primaria
import com.arquivoparanormal.app.ui.RotuloOP
import com.arquivoparanormal.app.ui.TextoClaro
import com.arquivoparanormal.app.ui.TextoFraco
import com.arquivoparanormal.app.ui.Texto as CampoTexto

@Composable
fun LoginScreen(auth: Autenticacao, aoEntrar: (String) -> Unit) {
    var criando by remember { mutableStateOf(false) }
    var usuario by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var erro by remember { mutableStateOf<String?>(null) }
    var carregando by remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        RotuloOP("Ordem Paranormal · acesso restrito")
        Spacer(Modifier.height(8.dp))
        Text(
            "Arquivo Paranormal",
            style = MaterialTheme.typography.displaySmall,
            color = TextoClaro,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            if (criando) "Crie seu acesso. A conta fica sincronizada entre o app e o site."
            else "Identifique-se, agente.",
            style = MaterialTheme.typography.bodySmall,
            color = TextoFraco,
        )
        Spacer(Modifier.height(22.dp))

        Painel(titulo = if (criando) "Novo agente" else "Entrar") {
            Campo("E-mail") { CampoTexto(usuario, placeholder = "agente@exemplo.com") { usuario = it; erro = null } }
            Campo("Senha") { CampoTexto(senha, senha = true) { senha = it; erro = null } }

            erro?.let {
                Text(it, style = MaterialTheme.typography.bodySmall, color = Perigo)
            }

            Button(
                onClick = {
                    if (carregando) return@Button
                    carregando = true
                    val finaliza = { mensagem: String? ->
                        carregando = false
                        erro = mensagem
                        if (mensagem == null) auth.papelAtual(aoEntrar)
                    }
                    if (criando) auth.criarConta(usuario, senha, finaliza)
                    else auth.entrar(usuario, senha, finaliza)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Primaria),
            ) {
                Text(if (carregando) "Conectando…" else if (criando) "Criar acesso" else "Entrar")
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                TextButton(onClick = { criando = !criando; erro = null }) {
                    Text(
                        if (criando) "Já tenho acesso" else "Criar novo acesso",
                        color = TextoFraco,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
    }
}
