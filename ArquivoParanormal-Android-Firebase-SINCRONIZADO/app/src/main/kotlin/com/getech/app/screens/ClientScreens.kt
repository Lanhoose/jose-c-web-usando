
package com.getech.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.getech.app.data.LocalRepository
import com.getech.app.ui.*

@Composable
fun ClientDashboard(repo:LocalRepository,onChat:()->Unit,onAR:()->Unit,onLogout:()->Unit,onBack:()->Unit,onTheme:()->Unit){
 val u=repo.session()
 TechScaffold("Área do Cliente",onBack=onBack,actions={
  TextButton(onClick=onTheme){Text("☾",color=MaterialTheme.colorScheme.onSurface)}
  TextButton(onClick=onLogout){Text("Sair",color=Red)}
 }){pad->
  LazyColumn(Modifier.padding(pad).padding(18.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
   item{TechCard{
    Text("Olá, ${u?.name?:"Cliente"}",style=MaterialTheme.typography.headlineMedium,color=MaterialTheme.colorScheme.onSurface)
    Text("Acompanhe atendimento e recursos liberados para sua conta.",color=MaterialTheme.colorScheme.onSurfaceVariant)
    Spacer(Modifier.height(8.dp));StatusBadge("Conta Cliente • Ativa")
   }}
   item{Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){StatCard(repo.tickets().size.toString(),"Chamados",Modifier.weight(1f));StatCard("24/7","Suporte",Modifier.weight(1f))}}
   item{TechCard(title="💬 Atendimento"){Text("Abra o chatbot para registrar um problema de máquina.",color=MaterialTheme.colorScheme.onSurfaceVariant);PrimaryButton("Conversar com o chatbot",onClick=onChat)}}
   item{TechCard(title="📱 Realidade Aumentada"){Text("Use a câmera para inspeção e visualização industrial.",color=MaterialTheme.colorScheme.onSurfaceVariant);SecondaryButton("Abrir AR",onClick=onAR)}}
   item{TechCard(title="Central do Cliente"){Text("Consulte seus chamados e utilize os recursos liberados para sua conta. A área administrativa do Gestor fica protegida separadamente.",color=MaterialTheme.colorScheme.onSurfaceVariant)}}
   item{SectionTitle("Meus chamados")}
   itemsIndexed(repo.tickets().takeLast(8).reversed()){_,t->TechCard(title=t.problem.take(44).ifBlank{"Chamado"}){
     Text("${t.name} • ${t.email}",color=MaterialTheme.colorScheme.onSurfaceVariant)
     StatusBadge(t.status.uppercase(),t.status!="erro")
   }}
  }
 }
}

@Composable
fun ChatbotScreen(repo:LocalRepository,onBack:()->Unit){
 var step by remember{mutableIntStateOf(0)};var name by remember{mutableStateOf("")};var email by remember{mutableStateOf("")};var problem by remember{mutableStateOf("")};var input by remember{mutableStateOf("")}
 val messages=remember{mutableStateListOf("Olá! Sou o assistente da GeTech. Para começarmos, qual é o seu nome?")}
 fun send(){val v=input.trim();if(v.isBlank()||step>=3)return;messages.add(v);input="";when(step){0->{name=v;step=1;messages.add("Prazer, $name! Qual é o seu e-mail para contato?")};1->{email=v;step=2;messages.add("Ótimo! Agora, descreva brevemente o problema da sua máquina:")};2->{problem=v;step=3;messages.add(if(repo.addTicket(name,email,problem)!=null)"Perfeito, $name! Chamado registrado localmente. Nossa equipe técnica analisará as informações." else "Não consegui salvar o chamado localmente. Tente novamente.")}}}
 TechScaffold("Chatbot GeTech",onBack=onBack){pad->Column(Modifier.padding(pad).padding(14.dp).fillMaxSize()){
  TechCard(title="Suporte Técnico Industrial"){Text("Manutenção de máquinas pesadas • diagnóstico • reparo • prevenção.",color=MaterialTheme.colorScheme.onSurfaceVariant)}
  Spacer(Modifier.height(8.dp))
  LazyColumn(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(8.dp)){itemsIndexed(messages){i,m->Surface(color=if(i%2==0)MaterialTheme.colorScheme.surfaceVariant else Color(0xFF12506A),shape=MaterialTheme.shapes.medium){Text(m,color=Color.White,modifier=Modifier.padding(12.dp))}}}
  Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){OutlinedTextField(input,{input=it},Modifier.weight(1f),placeholder={Text(if(step>=3)"Atendimento concluído." else "Digite sua resposta...")},enabled=step<3,singleLine=true);Button(onClick=::send,enabled=step<3){Text("Enviar")}}
 }}
}
