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

data class Module(val route:String,val title:String,val icon:String,val description:String)

val managerModules=listOf(
    Module("geral","Visão Geral","🌐","Informações institucionais, escopo e objetivos do projeto GeTech."),
    Module("estoque","Gestão de Inventário","📦","Controle de entrada e saída de materiais."),
    Module("manutencao","Manutenção Ativa","⚙️","Registro e preventivas de máquinas."),
    Module("rh","RH & Ponto Digital","🕐","Registro de jornada dos funcionários."),
    Module("pedidos","Ordens de Serviço","📋","Gestão de pedidos de produção."),
    Module("qualidade","Controle de Qualidade","✅","Inspeção e relatórios de conformidade."),
    Module("suprimentos","Suprimentos","🛒","Gestão de compras e fornecedores."),
    Module("producao","Linha de Produção","🏭","Monitoramento industrial em tempo real."),
    Module("logs","Logs de Operação","📝","Registro e análise de eventos do sistema."),
    Module("sistema","Sistema em Python","👨🏾‍💻","Sistema de gerenciamento de manutenção industrial.")
)

@Composable
fun ManagerDashboard(repo:LocalRepository,onModule:(String)->Unit,onLogout:()->Unit,onBack:()->Unit){
    val user=repo.session()
    TechScaffold("Painel ERP",onBack=onBack,actions={Text("🌙",modifier=Modifier.padding(end=8.dp));TextButton(onClick=onLogout){Text("Sair",color=Red)}}){pad->
        LazyColumn(Modifier.padding(pad).padding(18.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
            item{TechCard{Text("Bem-vindo, ${user?.name ?: "Usuário"}!",style=MaterialTheme.typography.headlineMedium,color=TextPrimary);Text("Servidor Principal • Operacional",color=Green)}}
            items(managerModules){m->TechCard(title=m.title,icon=m.icon){Text(m.description,color=TextSecondary);Spacer(Modifier.height(4.dp));SecondaryButton("Abrir módulo",onClick={onModule(m.route)})}}
        }
    }
}

@Composable
fun ManagerModuleScreen(module:Module,repo:LocalRepository,onBack:()->Unit){
    val tickets=repo.tickets()
    TechScaffold(module.title,onBack=onBack){pad->LazyColumn(Modifier.padding(pad).padding(18.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
        item{Text(module.description,style=MaterialTheme.typography.titleLarge,color=Cyan)}
        when(module.route){
            "geral"->{item{Row(horizontalArrangement=Arrangement.spacedBy(10.dp)){StatCard("+150","Plantas");StatCard("99.8%","Disponibilidade")}}}
            "estoque"->{item{TechCard(title="Indicadores"){Text("Itens cadastrados: 1.248",color=TextPrimary);Text("Itens abaixo do mínimo: 18",color=TextSecondary);Text("Movimentações hoje: 74",color=TextSecondary)}}}
            "manutencao"->{item{TechCard(title="Ativos críticos"){Text("3 equipamentos aguardando intervenção.",color=TextSecondary);Text("12 preventivas previstas nesta semana.",color=TextSecondary)}}}
            "rh"->{item{TechCard(title="Ponto Digital"){Text("Funcionários ativos: 42",color=TextPrimary);Text("Presentes hoje: 38",color=TextSecondary)}}}
            "pedidos"->{item{TechCard(title="Ordens abertas"){Text("17 ordens em execução.",color=TextPrimary);Text("5 aguardando aprovação.",color=TextSecondary)}}}
            "qualidade"->{item{TechCard(title="Conformidade"){Text("Inspeções do dia: 26",color=TextPrimary);Text("Aprovadas: 24 • Pendentes: 2",color=TextSecondary)}}}
            "suprimentos"->{item{TechCard(title="Compras"){Text("8 pedidos de compra em análise.",color=TextPrimary);Text("4 fornecedores ativos.",color=TextSecondary)}}}
            "producao"->{item{TechCard(title="Linha em tempo real"){Text("OEE atual: 87.4%",color=Cyan);Text("Disponibilidade: 92%",color=TextSecondary);Text("Performance: 94%",color=TextSecondary)}}}
            "logs"->{items(tickets.take(20)){t->TechCard(title="${t.date} • ${t.name}",icon="📝"){Text("E-mail: ${t.email}",color=TextSecondary);Text(t.problem,color=TextPrimary)}}}
            "sistema"->{item{TechCard(title="Arquitetura local"){Text("Painel administrativo nativo em Kotlin + Jetpack Compose.",color=TextPrimary);Text("Firebase desativado. Dados de autenticação e chamados permanecem no dispositivo.",color=TextSecondary)}}}
        }
    }}
}
