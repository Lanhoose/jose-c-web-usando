
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
import java.text.SimpleDateFormat
import java.util.*

data class Module(val route:String,val title:String,val icon:String,val description:String)

val managerModules=listOf(
 Module("geral","Visão Geral","🌐","Informações institucionais, escopo e indicadores."),
 Module("estoque","Gestão de Inventário","📦","Recebimento, estoque, movimentações e itens críticos."),
 Module("manutencao","Manutenção Ativa","⚙️","Gestão de máquinas, preventivas, O.S. e histórico."),
 Module("rh","RH & Ponto Digital","🕐","Cadastro de colaboradores e registro de jornada."),
 Module("pedidos","Ordens de Serviço","📋","Pedidos de produção, prioridade e acompanhamento."),
 Module("qualidade","Controle de Qualidade","✅","Inspeção, conformidade e relatórios."),
 Module("suprimentos","Suprimentos","🛒","Compras e fornecedores."),
 Module("producao","Linha de Produção","🏭","OEE, disponibilidade e performance."),
 Module("logs","Auditoria & Logs","📝","Busca e análise do histórico de eventos."),
 Module("sistema","S.I.U. - Gestão de Manutenção","🛠️","Ativos, ordens, cadastros e histórico global.")
)

@Composable
fun ManagerDashboard(repo:LocalRepository,onModule:(String)->Unit,onLogout:()->Unit,onBack:()->Unit,onTheme:()->Unit){
 val u=repo.session();val machines=repo.machines();val orders=repo.workOrders();val inv=repo.inventory()
 TechScaffold("Painel ERP",onBack=onBack,actions={TextButton(onClick=onTheme){Text("☾")};TextButton(onClick=onLogout){Text("Sair",color=Red)}}){pad->
  LazyColumn(Modifier.padding(pad).padding(18.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
   item{TechCard{
    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){Column(Modifier.weight(1f)){Text("Bem-vindo, ${u?.name?:"Gestor"}!",style=MaterialTheme.typography.headlineMedium);Text("Painel administrativo GeTech",color=MaterialTheme.colorScheme.onSurfaceVariant)};StatusBadge("● Operacional")}
   }}
   item{Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){StatCard(machines.size.toString(),"Ativos",Modifier.weight(1f));StatCard(orders.size.toString(),"O.S.",Modifier.weight(1f));StatCard(inv.size.toString(),"Itens",Modifier.weight(1f))}}
   item{SectionTitle("Módulos ERP")}
   items(managerModules){m->TechCard(title=m.title,icon=m.icon){Text(m.description,color=MaterialTheme.colorScheme.onSurfaceVariant);SecondaryButton("Abrir módulo",Modifier.fillMaxWidth()){onModule(m.route)}}}
  }
 }
}

@Composable
fun ManagerModuleScreen(module:Module,repo:LocalRepository,onBack:()->Unit){
 when(module.route){
  "manutencao"->MaintenanceModule(repo,onBack)
  "estoque"->InventoryModule(repo,onBack)
  "pedidos"->OrdersModule(repo,onBack)
  "rh"->HrModule(repo,onBack)
  "logs"->LogsModule(repo,onBack)
  else->GenericModule(module,repo,onBack)
 }
}

@Composable private fun GenericModule(m:Module,repo:LocalRepository,onBack:()->Unit){
 TechScaffold(m.title,onBack=onBack){pad->LazyColumn(Modifier.padding(pad).padding(18.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
  item{Text(m.description,style=MaterialTheme.typography.titleLarge,color=MaterialTheme.colorScheme.primary)}
  when(m.route){
   "geral"->{item{Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){StatCard("+150","Plantas",Modifier.weight(1f));StatCard("99.8%","Disponibilidade",Modifier.weight(1f))}}}
   "qualidade"->{item{TechCard(title="Conformidade"){Text("26 inspeções do dia",color=MaterialTheme.colorScheme.onSurface);Text("24 aprovadas • 2 pendentes",color=MaterialTheme.colorScheme.onSurfaceVariant)}}}
   "suprimentos"->{item{TechCard(title="Compras"){Text("8 pedidos em análise",color=MaterialTheme.colorScheme.onSurface);Text("4 fornecedores ativos",color=MaterialTheme.colorScheme.onSurfaceVariant)}}}
   "producao"->{item{TechCard(title="Linha em tempo real"){Text("OEE 87.4%",color=MaterialTheme.colorScheme.primary);Text("Disponibilidade 92% • Performance 94%",color=MaterialTheme.colorScheme.onSurfaceVariant)}}}
   "sistema"->{item{TechCard(title="S.I.U."){Text("Status dos ativos, ordens de serviço, cadastros e histórico global.",color=MaterialTheme.colorScheme.onSurface);Text("Implementação nativa Kotlin/Compose; não executa Pyodide/HTML.",color=MaterialTheme.colorScheme.onSurfaceVariant)}}}
  }
 }}
}

@Composable private fun MaintenanceModule(repo:LocalRepository,onBack:()->Unit){
 var show by remember{mutableStateOf(false)};var name by remember{mutableStateOf("")};var code by remember{mutableStateOf("")};var sector by remember{mutableStateOf("")};var location by remember{mutableStateOf("")}
 TechScaffold("⚙️ Manutenção Preventiva",onBack=onBack){pad->LazyColumn(Modifier.padding(pad).padding(18.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){
  item{TechCard{Text("Gestão de Máquinas",style=MaterialTheme.typography.headlineSmall);Text("Cadastre, consulte e acompanhe equipamentos.",color=MaterialTheme.colorScheme.onSurfaceVariant);PrimaryButton("Cadastrar Equipamento"){show=true}}}
  items(repo.machines()){m->TechCard(title="${m.name} • ${m.code}"){StatusBadge(m.status);Text("${m.sector} • ${m.location}",color=MaterialTheme.colorScheme.onSurfaceVariant)}}
  item{TechCard(title="Ordens de Serviço"){Text("${repo.workOrders().size} ordens registradas localmente.",color=MaterialTheme.colorScheme.onSurfaceVariant)}}
 }}
 if(show) AlertDialog(onDismissRequest={show=false},title={Text("Cadastrar Equipamento")},text={Column{OutlinedTextField(name,{name=it},label={Text("Nome")},singleLine=true);OutlinedTextField(code,{code=it},label={Text("Código")},singleLine=true);OutlinedTextField(sector,{sector=it},label={Text("Setor")},singleLine=true);OutlinedTextField(location,{location=it},label={Text("Localização")},singleLine=true)}},confirmButton={Button(onClick={if(name.isNotBlank()&&code.isNotBlank()){repo.addMachine(name,code,sector,location);show=false}}){Text("Cadastrar")}},dismissButton={TextButton(onClick={show=false}){Text("Cancelar")}})
}

@Composable private fun InventoryModule(repo:LocalRepository,onBack:()->Unit){
 val items=repo.inventory()
 TechScaffold("📦 Gestão de Inventário",onBack=onBack){pad->LazyColumn(Modifier.padding(pad).padding(18.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){
  item{Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){StatCard(items.size.toString(),"Itens",Modifier.weight(1f));StatCard(items.count{it.quantity<it.minimum}.toString(),"Abaixo do mínimo",Modifier.weight(1f))}}
  items(items){i->TechCard(title="${i.name} • ${i.code}"){Text("${i.quantity} ${i.unit} disponíveis",color=MaterialTheme.colorScheme.onSurface);Text("Mínimo: ${i.minimum} ${i.unit}",color=if(i.quantity<i.minimum)Red else MaterialTheme.colorScheme.onSurfaceVariant);Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){SecondaryButton("+ Entrada",Modifier.weight(1f)){};SecondaryButton("- Saída",Modifier.weight(1f)){}}}}
  item{TechCard(title="Movimentações"){Text("A entrada e saída ficam preparadas para expansão do inventário local.",color=MaterialTheme.colorScheme.onSurfaceVariant)}}
 }}
}

@Composable private fun OrdersModule(repo:LocalRepository,onBack:()->Unit){
 var show by remember{mutableStateOf(false)};var equip by remember{mutableStateOf("")};var desc by remember{mutableStateOf("")};var resp by remember{mutableStateOf("")}
 TechScaffold("📋 LogiStock PRO",onBack=onBack){pad->LazyColumn(Modifier.padding(pad).padding(18.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){
  item{TechCard{Text("Gestão de Pedidos de Produção",style=MaterialTheme.typography.headlineSmall);Text("Kanban adaptado para smartphone, recebimento, rastreio e auditoria.",color=MaterialTheme.colorScheme.onSurfaceVariant);PrimaryButton("+ Nova Ordem"){show=true}}}
  items(repo.workOrders()){o->TechCard(title="${o.id} • ${o.equipment}"){StatusBadge("${o.priority} • ${o.status}",o.priority!="Urgente");Text(o.description,color=MaterialTheme.colorScheme.onSurfaceVariant);Text("Responsável: ${o.responsible}",color=MaterialTheme.colorScheme.onSurfaceVariant)}}
 }}
 if(show) AlertDialog(onDismissRequest={show=false},title={Text("Nova Ordem de Produção")},text={Column{OutlinedTextField(equip,{equip=it},label={Text("Equipamento")});OutlinedTextField(desc,{desc=it},label={Text("Descrição")});OutlinedTextField(resp,{resp=it},label={Text("Responsável")})}},confirmButton={Button(onClick={if(equip.isNotBlank()){repo.addWorkOrder(equip,desc,"Normal",resp);show=false}}){Text("Salvar")}},dismissButton={TextButton(onClick={show=false}){Text("Cancelar")}})
}

@Composable private fun HrModule(repo:LocalRepository,onBack:()->Unit){
 val employees=repo.employees()
 TechScaffold("🕐 RH & Ponto Digital",onBack=onBack){pad->LazyColumn(Modifier.padding(pad).padding(18.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){
  item{Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){StatCard(employees.size.toString(),"Funcionários",Modifier.weight(1f));StatCard(employees.count{it.active}.toString(),"Ativos",Modifier.weight(1f))}}
  item{TechCard(title="Cadastro de Funcionário"){Text("A estrutura de cadastro e ponto está disponível para armazenamento local.",color=MaterialTheme.colorScheme.onSurfaceVariant)}}
  items(employees){e->TechCard(title=e.name){Text("${e.department} • ${e.email}",color=MaterialTheme.colorScheme.onSurfaceVariant);StatusBadge(if(e.active)"ATIVO" else "INATIVO")}}
 }}
}

@Composable private fun LogsModule(repo:LocalRepository,onBack:()->Unit){
 var filter by remember{mutableStateOf("")};val logs=repo.auditLogs().filter{val q=filter.trim().lowercase();q.isBlank()||"${it.id} ${it.operator} ${it.action} ${it.detail}".lowercase().contains(q)}
 TechScaffold("📝 Auditoria & Logs",onBack=onBack){pad->LazyColumn(Modifier.padding(pad).padding(18.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){
  item{TechCard{Text("Histórico Digital de Auditoria",style=MaterialTheme.typography.headlineSmall);Text("Rastreabilidade local das ações executadas.",color=MaterialTheme.colorScheme.onSurfaceVariant);OutlinedTextField(filter,{filter=it},Modifier.fillMaxWidth(),label={Text("🔎 Busca geral")},singleLine=true)}}
  items(logs.take(50)){l->TechCard(title="${l.id} • ${SimpleDateFormat("dd/MM/yyyy HH:mm",Locale.getDefault()).format(Date(l.date))}"){Text("${l.operator} • ${l.action}",color=MaterialTheme.colorScheme.onSurface);Text(l.detail,color=MaterialTheme.colorScheme.onSurfaceVariant);StatusBadge(l.severity.uppercase(),l.severity!="critico")}}
  if(logs.isEmpty()) item{TechCard{Text("Nenhum registro corresponde aos filtros.",color=MaterialTheme.colorScheme.onSurfaceVariant)}}
 }}
}
