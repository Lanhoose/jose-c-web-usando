package com.getech.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
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
 Module("geral","Visão Geral","🌐","Resumo operacional e indicadores."),
 Module("estoque","Gestão de Inventário","📦","Recebimento, estoque e movimentações."),
 Module("manutencao","Manutenção Ativa","⚙️","Máquinas, O.S. e histórico de intervenções."),
 Module("rh","RH & Ponto Digital","🕐","Cadastro de colaboradores e jornada."),
 Module("pedidos","Pedidos / LogiStock","📋","Ordens, etapas e acompanhamento."),
 Module("orcamento","Gerador de Orçamento","💰","Itens, quantidades e total da proposta."),
 Module("mensagens","Caixa de Mensagens","✉️","Contato público e chamados do cliente."),
 Module("qualidade","Controle de Qualidade","✅","Placeholder: sem página de origem correspondente."),
 Module("suprimentos","Suprimentos","🛒","Placeholder: sem página de origem correspondente."),
 Module("producao","Linha de Produção","🏭","Placeholder: sem página de origem correspondente."),
 Module("logs","Auditoria & Logs","📝","Histórico, busca e criticidade."),
 Module("sistema","S.I.U. - Gestão de Manutenção","🛠️","Ativos, ordens e histórico global; Pyodide não portado.")
)

@Composable
fun ManagerDashboard(repo:LocalRepository,onModule:(String)->Unit,onLogout:()->Unit,onBack:()->Unit,onTheme:()->Unit){
 val u=repo.session(); val machines=repo.machines(); val orders=repo.workOrders(); val inv=repo.inventory()
 TechScaffold("Aplicação ERP",onBack=onBack,actions={
  TextButton(onClick=onTheme){Text("☾",color=MaterialTheme.colorScheme.onSurface)}
  TextButton(onClick=onLogout){Text("Sair",color=Red)}
 }){pad->
  LazyColumn(Modifier.padding(pad).padding(18.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
   item{TechCard{
    Text("Bem-vindo, ${u?.name ?: "Gestor"}!",style=MaterialTheme.typography.headlineMedium,color=MaterialTheme.colorScheme.onSurface)
    Text("Aplicação interna / ERP GeTech",color=MaterialTheme.colorScheme.onSurfaceVariant)
    Spacer(Modifier.height(8.dp)); StatusBadge("● Operacional")
   }}
   item{Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){
    StatCard(machines.size.toString(),"Ativos",Modifier.weight(1f)); StatCard(orders.size.toString(),"O.S.",Modifier.weight(1f)); StatCard(inv.size.toString(),"Itens",Modifier.weight(1f))
   }}
   item{SectionTitle("Módulos da aplicação interna")}
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
  "orcamento"->QuoteModule(repo,onBack)
  "mensagens"->MessagesModule(repo,onBack)
  "rh"->HrModule(repo,onBack)
  "logs"->LogsModule(repo,onBack)
  else->GenericModule(module,repo,onBack)
 }
}

@Composable private fun GenericModule(m:Module,repo:LocalRepository,onBack:()->Unit){
 TechScaffold(m.title,onBack=onBack){pad->LazyColumn(Modifier.padding(pad).padding(18.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
  item{Text(m.description,style=MaterialTheme.typography.titleLarge,color=MaterialTheme.colorScheme.primary)}
  when(m.route){
   "geral"->item{Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){StatCard("${repo.machines().size}","Ativos",Modifier.weight(1f));StatCard("99.8%","Disponibilidade",Modifier.weight(1f))}}
   "qualidade","suprimentos","producao"->item{TechCard(title="Módulo mantido como placeholder"){Text("O prompt v7 determinou não inventar conteúdo para este módulo porque não existe uma página correspondente no site.",color=MaterialTheme.colorScheme.onSurfaceVariant)}}
   "sistema"->item{TechCard(title="S.I.U."){Text("Status dos ativos, ordens de serviço, cadastros e histórico global.",color=MaterialTheme.colorScheme.onSurface);Text("Implementação nativa Kotlin/Compose. Pyodide/HTML não é executado no Android.",color=MaterialTheme.colorScheme.onSurfaceVariant)}}
  }
 }}
}

@Composable private fun MaintenanceModule(repo:LocalRepository,onBack:()->Unit){
 var showMachine by remember{mutableStateOf(false)}; var showOs by remember{mutableStateOf(false)}
 var name by remember{mutableStateOf("")}; var model by remember{mutableStateOf("")}; var serial by remember{mutableStateOf("")}; var date by remember{mutableStateOf("")}; var code by remember{mutableStateOf("")}; var sector by remember{mutableStateOf("")}; var location by remember{mutableStateOf("")}
 var selected by remember{mutableStateOf("")}; var description by remember{mutableStateOf("")}; var responsible by remember{mutableStateOf("")}; var priority by remember{mutableStateOf("Normal")}
 val machines=repo.machines(); val orders=repo.workOrders()
 TechScaffold("⚙️ Manutenção Preventiva",onBack=onBack){pad->LazyColumn(Modifier.padding(pad).padding(18.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){
  item{TechCard{Text("Gestão de Máquinas",style=MaterialTheme.typography.headlineSmall);Text("Campos alinhados ao site: nome, modelo, número de série e última manutenção.",color=MaterialTheme.colorScheme.onSurfaceVariant);PrimaryButton("Cadastrar Máquina"){showMachine=true}}}
  items(machines){m->TechCard(title="${m.name} • ${m.model.ifBlank{m.code}}"){
   Text("Série: ${m.serial.ifBlank{"não informada"}}",color=MaterialTheme.colorScheme.onSurfaceVariant)
   Text("Última manutenção: ${if(m.lastMaintenance>0) SimpleDateFormat("dd/MM/yyyy",Locale.getDefault()).format(Date(m.lastMaintenance)) else "—"}",color=MaterialTheme.colorScheme.onSurfaceVariant)
   StatusBadge(if(System.currentTimeMillis()-m.lastMaintenance>90L*24*60*60*1000) "MANUTENÇÃO RECOMENDADA" else "EM DIA",System.currentTimeMillis()-m.lastMaintenance<=90L*24*60*60*1000)
   val history=orders.filter{it.equipment==m.name}
   if(history.isNotEmpty()){Text("Histórico de Intervenções",color=MaterialTheme.colorScheme.primary);history.takeLast(5).forEach{Text("${it.status} • ${it.description}",color=MaterialTheme.colorScheme.onSurfaceVariant)}}
  }}
  item{TechCard(title="Ordens de Serviço"){Text("${orders.size} ordens registradas.",color=MaterialTheme.colorScheme.onSurfaceVariant);PrimaryButton("Nova O.S."){showOs=true}}}
 }}
 if(showMachine) AlertDialog(onDismissRequest={showMachine=false},title={Text("Cadastrar Máquina")},text={Column(Modifier.verticalScroll(androidx.compose.foundation.rememberScrollState()),verticalArrangement=Arrangement.spacedBy(7.dp)){
  OutlinedTextField(name,{name=it},label={Text("Nome")},singleLine=true);OutlinedTextField(model,{model=it},label={Text("Modelo")},singleLine=true);OutlinedTextField(serial,{serial=it},label={Text("Número de série")},singleLine=true);OutlinedTextField(date,{date=it},label={Text("Última manutenção (dd/MM/yyyy)")},singleLine=true);OutlinedTextField(code,{code=it},label={Text("Código interno")},singleLine=true);OutlinedTextField(sector,{sector=it},label={Text("Setor")},singleLine=true);OutlinedTextField(location,{location=it},label={Text("Localização")},singleLine=true)
 }},confirmButton={Button(onClick={if(name.isNotBlank()){val ts=runCatching{SimpleDateFormat("dd/MM/yyyy",Locale.getDefault()).parse(date)?.time}.getOrNull()?:System.currentTimeMillis();repo.addMachine(name,code,sector,location,model,serial,ts);showMachine=false;name="";model="";serial="";date=""}}){Text("Cadastrar")}},dismissButton={TextButton(onClick={showMachine=false}){Text("Cancelar")}})
 if(showOs) AlertDialog(onDismissRequest={showOs=false},title={Text("Nova Ordem de Serviço")},text={Column(verticalArrangement=Arrangement.spacedBy(8.dp)){
  if(machines.isEmpty()) Text("Cadastre uma máquina antes de criar uma O.S.",color=Red) else {
   var expanded by remember{mutableStateOf(false)}
   Box{OutlinedButton(onClick={expanded=true},Modifier.fillMaxWidth()){Text(if(selected.isBlank())"Selecionar máquina" else selected)};DropdownMenu(expanded,{expanded=false}){machines.forEach{m->DropdownMenuItem(text={Text("${m.name} — ${m.model}")},onClick={selected=m.name;expanded=false})}}}
   OutlinedTextField(description,{description=it},label={Text("Descrição")},singleLine=false);OutlinedTextField(responsible,{responsible=it},label={Text("Responsável")},singleLine=true);OutlinedTextField(priority,{priority=it},label={Text("Prioridade")},singleLine=true)
  }
 }},confirmButton={Button(enabled=machines.isNotEmpty()&&selected.isNotBlank(),onClick={repo.addWorkOrder(selected,description,priority,responsible);showOs=false;selected="";description="";responsible=""}){Text("Salvar")}},dismissButton={TextButton(onClick={showOs=false}){Text("Cancelar")}})
}

@Composable private fun InventoryModule(repo:LocalRepository,onBack:()->Unit){
 var movement by remember{mutableStateOf<LocalRepository.InventoryItem?>(null)}; var refresh by remember{mutableIntStateOf(0)}; refresh;var pending by remember{mutableStateOf(false)};var qty by remember{mutableStateOf("")};var add by remember{mutableStateOf(true)};var msg by remember{mutableStateOf("")};var name by remember{mutableStateOf("")};var code by remember{mutableStateOf("")};var receiveQty by remember{mutableStateOf("")};var unit by remember{mutableStateOf("un")}
 val inventoryItems=repo.inventory();val pendingItems=repo.pendingInventory()
 TechScaffold("📦 Gestão de Inventário",onBack=onBack){pad->LazyColumn(Modifier.padding(pad).padding(18.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){
  if(msg.isNotBlank())item{StatusBadge(msg,!msg.contains("insuficiente",true))}
  item{Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){StatCard(inventoryItems.size.toString(),"Itens",Modifier.weight(1f));StatCard(inventoryItems.count{it.quantity<it.minimum}.toString(),"Abaixo do mínimo",Modifier.weight(1f))}}
  item{TechCard(title="Recebimento") {OutlinedTextField(name,{name=it},label={Text("Item")},singleLine=true);OutlinedTextField(code,{code=it},label={Text("Código")},singleLine=true);OutlinedTextField(receiveQty,{receiveQty=it},label={Text("Quantidade")},singleLine=true);OutlinedTextField(unit,{unit=it},label={Text("Unidade")},singleLine=true);PrimaryButton("Enviar para liberação"){if(repo.addPendingInventory(name,code,receiveQty.toIntOrNull()?:0,unit)){name="";code="";receiveQty=""}}}}
  if(pendingItems.isNotEmpty())item{TechCard(title="Aguardando liberação"){pendingItems.forEach{p->Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){Text("${p.name}: ${p.quantity} ${p.unit}");TextButton(onClick={repo.releasePendingInventory(p.id);refresh++}){Text("Liberar")}}}}}
  items(inventoryItems){i->TechCard(title="${i.name} • ${i.code}"){Text("${i.quantity} ${i.unit} disponíveis",color=MaterialTheme.colorScheme.onSurface);Text("Mínimo: ${i.minimum} ${i.unit}",color=if(i.quantity<i.minimum)Red else MaterialTheme.colorScheme.onSurfaceVariant);Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){SecondaryButton("+ Entrada",Modifier.weight(1f)){movement=i;add=true;qty=""};SecondaryButton("- Saída",Modifier.weight(1f)){movement=i;add=false;qty=""}}}}
 }}
 movement?.let{item->AlertDialog(onDismissRequest={movement=null},title={Text(if(add)"Entrada" else "Saída")},text={OutlinedTextField(qty,{qty=it},label={Text("Quantidade")},singleLine=true)},confirmButton={Button(onClick={val n=qty.toIntOrNull()?:0;val r=repo.adjustInventory(item.id,if(add)n else -n);msg=r.fold({"Movimentação registrada: $it ${item.unit}"},{it.message?:"Erro na movimentação"});movement=null}){Text("Confirmar")}},dismissButton={TextButton(onClick={movement=null}){Text("Cancelar")}})}
}

@Composable private fun OrdersModule(repo:LocalRepository,onBack:()->Unit){
 var show by remember{mutableStateOf(false)};var refresh by remember{mutableIntStateOf(0)};refresh;var client by remember{mutableStateOf("")};var product by remember{mutableStateOf("")};var quantity by remember{mutableStateOf("")};var responsible by remember{mutableStateOf("")};var selectedTab by remember{mutableStateOf("A Fazer")};var priority by remember{mutableStateOf("Normal")}
 val statuses=listOf("A Fazer","Em Andamento","Qualidade","Finalizado");val orders=repo.orders().filter{it.status==selectedTab}
 TechScaffold("📋 Pedidos / LogiStock",onBack=onBack){pad->LazyColumn(Modifier.padding(pad).padding(18.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){
  item{PrimaryButton("+ Nova Ordem",Modifier.fillMaxWidth()){show=true}}
  item{Row(horizontalArrangement=Arrangement.spacedBy(4.dp)){statuses.forEach{s->if(s==selectedTab)PrimaryButton(s,Modifier.weight(1f)){selectedTab=s}else SecondaryButton(s,Modifier.weight(1f)){selectedTab=s}}}}
  items(orders){o->TechCard(title="${o.id} • ${o.client}"){Text("${o.product} • ${o.quantity} un",color=MaterialTheme.colorScheme.onSurface);Text("Responsável: ${o.responsible}",color=MaterialTheme.colorScheme.onSurfaceVariant);StatusBadge(o.status,o.status!="Finalizado");if(o.status!="Finalizado")SecondaryButton("Avançar etapa"){if(repo.advanceOrder(o.id))refresh++}}}
  if(orders.isEmpty())item{TechCard{Text("Nenhum pedido nesta etapa.",color=MaterialTheme.colorScheme.onSurfaceVariant)}}
 }}
 if(show)AlertDialog(onDismissRequest={show=false},title={Text("Nova Ordem")},text={Column(verticalArrangement=Arrangement.spacedBy(7.dp)){OutlinedTextField(client,{client=it},label={Text("Cliente")});OutlinedTextField(product,{product=it},label={Text("Produto")});OutlinedTextField(quantity,{quantity=it},label={Text("Quantidade")});OutlinedTextField(responsible,{responsible=it},label={Text("Responsável")});OutlinedTextField(priority,{priority=it},label={Text("Prioridade")})}},confirmButton={Button(onClick={if(repo.addOrder(client,product,quantity.toIntOrNull()?:0,responsible,"A Fazer",priority)){show=false;client="";product="";quantity="";responsible=""}}){Text("Criar")}},dismissButton={TextButton(onClick={show=false}){Text("Cancelar")}})
}

@Composable private fun QuoteModule(repo:LocalRepository,onBack:()->Unit){
 data class Line(val description:String,val price:Double,val quantity:Int)
 var lines by remember{mutableStateOf(listOf<Line>())};var desc by remember{mutableStateOf("")};var price by remember{mutableStateOf("")};var quantity by remember{mutableStateOf("1")};val total=lines.sumOf{it.price*it.quantity}
 TechScaffold("💰 Gerador de Orçamento",onBack=onBack){pad->LazyColumn(Modifier.padding(pad).padding(18.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){
  item{TechCard(title="Adicionar item"){OutlinedTextField(desc,{desc=it},label={Text("Descrição")});OutlinedTextField(price,{price=it},label={Text("Preço")});OutlinedTextField(quantity,{quantity=it},label={Text("Quantidade")});PrimaryButton("Adicionar"){val p=price.replace(',','.').toDoubleOrNull();val q=quantity.toIntOrNull()?:0;if(desc.isNotBlank()&&p!=null&&q>0){lines=lines+Line(desc,p,q);desc="";price="";quantity="1"}}}}
  items(lines){l->TechCard(title=l.description){Text("${l.quantity} × R$ ${"%.2f".format(Locale.getDefault(),l.price)}",color=MaterialTheme.colorScheme.onSurfaceVariant);Text("Subtotal: R$ ${"%.2f".format(Locale.getDefault(),l.price*l.quantity)}",color=MaterialTheme.colorScheme.primary)}}
  item{TechCard{Text("Total",style=MaterialTheme.typography.titleLarge);Text("R$ ${"%.2f".format(Locale.getDefault(),total)}",style=MaterialTheme.typography.headlineMedium,color=MaterialTheme.colorScheme.primary);DangerButton("Limpar tudo"){lines=emptyList()}}}
 }}
}

@Composable private fun MessagesModule(repo:LocalRepository,onBack:()->Unit){
 val messages=repo.messages();val tickets=repo.tickets();TechScaffold("✉️ Caixa de Mensagens",onBack=onBack,actions={TextButton(onClick=repo::clearMessages){Text("Limpar caixa")}}){pad->LazyColumn(Modifier.padding(pad).padding(18.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){
  item{TechCard{Text("Central unificada",style=MaterialTheme.typography.headlineSmall);Text("Mensagens do formulário de contato e chamados do Chatbot.",color=MaterialTheme.colorScheme.onSurfaceVariant)}}
  items(messages){m->TechCard(title="${m.source} • ${m.name}"){Text(m.email,color=MaterialTheme.colorScheme.onSurfaceVariant);Text(m.text,color=MaterialTheme.colorScheme.onSurface)}}
  items(tickets){t->TechCard(title="Chatbot • ${t.name}"){Text(t.email,color=MaterialTheme.colorScheme.onSurfaceVariant);Text(t.problem,color=MaterialTheme.colorScheme.onSurface);StatusBadge(t.status.uppercase())}}
  if(messages.isEmpty()&&tickets.isEmpty())item{TechCard{Text("Nenhuma mensagem recebida.",color=MaterialTheme.colorScheme.onSurfaceVariant)}}
 }}
}

@Composable private fun HrModule(repo:LocalRepository,onBack:()->Unit){
 var show by remember{mutableStateOf(false)};var refresh by remember{mutableIntStateOf(0)};refresh;var name by remember{mutableStateOf("")};var email by remember{mutableStateOf("")};var dept by remember{mutableStateOf("")};var cep by remember{mutableStateOf("")};var street by remember{mutableStateOf("")};var district by remember{mutableStateOf("")};var city by remember{mutableStateOf("")};var uf by remember{mutableStateOf("")}
 val employees=repo.employees();TechScaffold("🕐 RH & Ponto Digital",onBack=onBack){pad->LazyColumn(Modifier.padding(pad).padding(18.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){
  item{Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){StatCard(employees.size.toString(),"Funcionários",Modifier.weight(1f));StatCard(employees.count{it.active}.toString(),"Ativos",Modifier.weight(1f))}}
  item{PrimaryButton("+ Cadastrar funcionário",Modifier.fillMaxWidth()){show=true}}
  items(employees){e->TechCard(title=e.name){Text("${e.department} • ${e.email}",color=MaterialTheme.colorScheme.onSurfaceVariant);StatusBadge(if(e.clockIn!=null&&e.clockOut==null)"DENTRO DO EXPEDIENTE" else "FORA DO EXPEDIENTE",e.clockIn==null||e.clockOut!=null);Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){SecondaryButton("Entrada",Modifier.weight(1f)){if(repo.clockEmployee(e.id,true))refresh++};SecondaryButton("Saída",Modifier.weight(1f)){if(repo.clockEmployee(e.id,false))refresh++}}}}
 }}
 if(show)AlertDialog(onDismissRequest={show=false},title={Text("Cadastrar funcionário")},text={Column(Modifier.verticalScroll(androidx.compose.foundation.rememberScrollState()),verticalArrangement=Arrangement.spacedBy(6.dp)){OutlinedTextField(name,{name=it},label={Text("Nome")});OutlinedTextField(email,{email=it},label={Text("E-mail")});OutlinedTextField(dept,{dept=it},label={Text("Departamento")});OutlinedTextField(cep,{cep=it},label={Text("CEP")});OutlinedTextField(street,{street=it},label={Text("Rua")});OutlinedTextField(district,{district=it},label={Text("Bairro")});OutlinedTextField(city,{city=it},label={Text("Cidade")});OutlinedTextField(uf,{uf=it},label={Text("UF")})}},confirmButton={Button(onClick={if(repo.addEmployee(name,email,dept,cep,street,district,city,uf)){show=false;name="";email="";dept=""}}){Text("Cadastrar")}},dismissButton={TextButton(onClick={show=false}){Text("Cancelar")}})
}

@Composable private fun LogsModule(repo:LocalRepository,onBack:()->Unit){
 var filter by remember{mutableStateOf("")}
 val q=filter.trim().lowercase()
 val logs=repo.auditLogs().filter { log -> "${log.id} ${log.operator} ${log.action} ${log.detail} ${log.severity}".lowercase().contains(q) }
 TechScaffold("📝 Auditoria & Logs",onBack=onBack){pad->LazyColumn(Modifier.padding(pad).padding(18.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){
  item{TechCard{Text("Histórico Digital de Auditoria",style=MaterialTheme.typography.headlineSmall);OutlinedTextField(filter,{filter=it},Modifier.fillMaxWidth(),label={Text("🔎 Busca")},singleLine=true)}}
  items(logs.take(50)){l->TechCard(title="${l.id} • ${SimpleDateFormat("dd/MM/yyyy HH:mm",Locale.getDefault()).format(Date(l.date))}"){Text("${l.operator} • ${l.action}");Text(l.detail,color=MaterialTheme.colorScheme.onSurfaceVariant);StatusBadge(l.severity.uppercase(),l.severity!="critico")}}
  if(logs.isEmpty())item{TechCard{Text("Nenhum registro corresponde aos filtros.",color=MaterialTheme.colorScheme.onSurfaceVariant)}}
 }}
}
