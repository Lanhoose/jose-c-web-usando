package com.getech.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.getech.app.data.LocalRepository
import com.getech.app.ui.*

@Composable
fun ClientDashboard(repo:LocalRepository,onChat:()->Unit,onAR:()->Unit,onPublic:(String)->Unit,onLogout:()->Unit,onBack:()->Unit){
    val u=repo.session()
    TechScaffold("Área do Cliente",onBack=onBack,actions={TextButton(onClick=onLogout){Text("Sair",color=Red)}}){pad->
        LazyColumn(Modifier.padding(pad).padding(18.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
            item{TechCard{Text("Olá, ${u?.name ?: "Cliente"}",style=MaterialTheme.typography.headlineMedium,color=TextPrimary);Text("Acompanhe atendimento e recursos liberados para sua conta.",color=TextSecondary)}}
            item{TechCard(title="💬 Atendimento"){Text("Abra o chatbot para registrar um problema de máquina.",color=TextSecondary);PrimaryButton("Abrir Chatbot",onClick=onChat)}}
            item{TechCard(title="📱 Realidade Aumentada"){Text("Use a câmera para inspeção e visualização industrial.",color=TextSecondary);SecondaryButton("Abrir AR",onClick=onAR)}}
            item{SectionTitle("Páginas públicas");Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){SecondaryButton("Funcionalidades",Modifier.weight(1f)){onPublic("funcionalidades")};SecondaryButton("FAQ",Modifier.weight(1f)){onPublic("faq")}}}
        }
    }
}

@Composable
fun ChatbotScreen(repo:LocalRepository,onBack:()->Unit){
    var step by remember{mutableIntStateOf(0)}
    var name by remember{mutableStateOf("")};var email by remember{mutableStateOf("")};var problem by remember{mutableStateOf("")};var input by remember{mutableStateOf("")}
    val messages=remember{mutableStateListOf("Olá! Sou o assistente da GeTech. Para começarmos, qual é o seu nome?")}
    fun send(){
        val v=input.trim();if(v.isBlank())return
        messages.add(v);input=""
        when(step){
            0->{name=v;step=1;messages.add("Prazer, $v! Qual o seu e-mail para contato?")}
            1->{email=v;step=2;messages.add("Ótimo! Agora, descreva brevemente o problema da sua máquina:")}
            2->{problem=v;step=3;repo.addTicket(name,email,problem);messages.add("Perfeito, $name! Recebemos as informações. Nossa equipe técnica analisará o problema e entrará em contato via e-mail ($email). 🛠️")}
        }
    }
    TechScaffold("Chatbot GeTech",onBack=onBack){pad->
        Column(Modifier.padding(pad).padding(14.dp).fillMaxSize()){
            LazyColumn(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(8.dp)){items(messages){m->
                val bot=!messages.indexOf(m).let{it%2==1}
                Surface(color=if(bot)Card2 else Color(0xFF12506A),shape=MaterialTheme.shapes.medium){Text(m,color=TextPrimary,modifier=Modifier.padding(12.dp))}
            }}}
            Row(horizontalArrangement=Arrangement.spacedBy(8.dp),modifier=Modifier.padding(top=8.dp)){
                OutlinedTextField(input,{input=it},modifier=Modifier.weight(1f),placeholder={Text(if(step>=3)"Atendimento concluído." else "Digite sua resposta...")},enabled=step<3,singleLine=true)
                Button(onClick={::send},enabled=step<3){Text("Enviar")}
            }
        }
    }
}
