package com.getech.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.getech.app.data.LocalRepository
import com.getech.app.ui.*

@Composable
fun LoginScreen(repo: LocalRepository, onLogged: () -> Unit, onRegister: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    TechScaffold("Acesso GeTech") { pad ->
        Column(Modifier.padding(pad).padding(20.dp).fillMaxSize(), verticalArrangement=Arrangement.Center) {
            Text("Bem-vindo de volta", style=MaterialTheme.typography.headlineLarge, color=TextPrimary)
            Text("Acesse sua conta do ERP Industrial", color=TextSecondary, modifier=Modifier.padding(top=6.dp,bottom=20.dp))
            TechCard(title="Entrar") {
                OutlinedTextField(email,{email=it},label={Text("Email")},modifier=Modifier.fillMaxWidth(),singleLine=true)
                OutlinedTextField(pass,{pass=it},label={Text("Senha")},modifier=Modifier.fillMaxWidth(),singleLine=true,visualTransformation=PasswordVisualTransformation())
                if(error.isNotBlank()) Text(error,color=Red)
                PrimaryButton("Entrar",Modifier.fillMaxWidth()) {
                    val u=repo.login(email,pass)
                    if(u==null) error="E-mail ou senha incorretos."
                    else {repo.setSession(u);onLogged()}
                }
                SecondaryButton("Não tem conta? Criar uma agora",Modifier.fillMaxWidth(),onRegister)
                Text("Demo Gestor: gestor@getech.local / 123456",color=TextSecondary)
                Text("Demo Cliente: cliente@getech.local / 123456",color=TextSecondary)
            }
        }
    }
}

@Composable
fun RegisterScreen(repo: LocalRepository, onRegistered: () -> Unit, onBack: () -> Unit) {
    var name by remember{mutableStateOf("")};var email by remember{mutableStateOf("")}
    var p1 by remember{mutableStateOf("")};var p2 by remember{mutableStateOf("")};var error by remember{mutableStateOf("")}
    TechScaffold("Criar conta",onBack=onBack){pad->
        Column(Modifier.padding(pad).padding(20.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
            Text("Comece a gerir a sua indústria hoje mesmo",style=MaterialTheme.typography.headlineMedium,color=TextPrimary)
            TechCard {
                OutlinedTextField(name,{name=it},label={Text("Nome")},modifier=Modifier.fillMaxWidth())
                OutlinedTextField(email,{email=it},label={Text("Email")},modifier=Modifier.fillMaxWidth())
                OutlinedTextField(p1,{p1=it},label={Text("Senha")},modifier=Modifier.fillMaxWidth(),visualTransformation=PasswordVisualTransformation())
                OutlinedTextField(p2,{p2=it},label={Text("Confirmar senha")},modifier=Modifier.fillMaxWidth(),visualTransformation=PasswordVisualTransformation())
                if(error.isNotBlank())Text(error,color=Red)
                PrimaryButton("Cadastrar",Modifier.fillMaxWidth()){
                    if(p1!=p2) error="As senhas não conferem."
                    else {error=repo.register(name,email,p1) ?: ""; if(error.isBlank()) onRegistered()}
                }
            }
        }
    }
}
