
package com.getech.app.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.getech.app.ui.*



/**
 * Camada intermediária autenticada da GeTech: Portal do Gestor.
 *
 * No site original esta é a área `site/public/`. No aplicativo ela NÃO é a
 * Home inicial. A Home inicial é a experiência do `Site C`.
 *
 * Regra de acesso:
 * Site C -> Login -> somente Gestor -> PublicPortal -> App/ERP.
 *
 * Clientes nunca recebem acesso a esta camada.
 */
@Composable
fun PublicPortalScreen(
    onOpenApp: () -> Unit,
    onPage: (String) -> Unit,
    onLogout: () -> Unit,
    onBack: () -> Unit,
    onTheme: () -> Unit
) {
    TechScaffold(
        "Portal do Gestor",
        onBack = onBack,
        actions = {
            TextButton(onClick = onTheme) {
                Text("☾", color = MaterialTheme.colorScheme.onSurface)
            }
            TextButton(onClick = onLogout) {
                Text("Sair", color = Red)
            }
        }
    ) { pad ->
        LazyColumn(
            Modifier
                .padding(pad)
                .padding(horizontal = 18.dp, vertical = 22.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                TechCard {
                    StatusBadge("●  Servidor Principal: Operacional")
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Plataforma Integrada de Gestão de Ativos",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Portal intermediário do Gestor. O acesso ao ERP fica disponível somente após a autenticação nesta camada.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(16.dp))
                    PrimaryButton(
                        "Entrar no ERP / Aplicação",
                        Modifier.fillMaxWidth(),
                        onOpenApp
                    )
                }
            }

            item { SectionTitle("Módulos Estruturais") }
            items(
                listOf(
                    "⚙️" to ("Controle de Manutenção" to "Ordens preventivas e corretivas."),
                    "📦" to ("Estoque de Peças" to "Rastreabilidade e níveis críticos."),
                    "📊" to ("Métricas de OEE" to "Eficiência, performance e qualidade."),
                    "🔒" to ("Auditoria & Logs" to "Histórico de eventos operacionais.")
                )
            ) { (icon, data) ->
                TechCard(title = data.first, icon = icon) {
                    Text(data.second, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    SecondaryButton("Acessar ERP", Modifier.fillMaxWidth(), onOpenApp)
                }
            }

            item { SectionTitle("Informações do GeTech") }
            item {
                TechCard {
                    Text(
                        "Conteúdo institucional",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SecondaryButton("Sobre", Modifier.weight(1f)) { onPage("sobre") }
                        SecondaryButton("Funcionalidades", Modifier.weight(1f)) {
                            onPage("funcionalidades")
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SecondaryButton("Planos", Modifier.weight(1f)) { onPage("planos") }
                        SecondaryButton("FAQ", Modifier.weight(1f)) { onPage("faq") }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SecondaryButton("Contato", Modifier.weight(1f)) { onPage("contato") }
                        SecondaryButton("Ajuda", Modifier.weight(1f)) { onPage("ajuda") }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SecondaryButton("Integrações", Modifier.weight(1f)) {
                            onPage("integrações")
                        }
                        SecondaryButton("Privacidade", Modifier.weight(1f)) {
                            onPage("privacidade")
                        }
                    }
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatCard("+150", "Plantas Industriais", Modifier.weight(1f))
                    StatCard("99.8%", "Disponibilidade", Modifier.weight(1f))
                    StatCard("24/7", "Monitoramento", Modifier.weight(1f))
                }
            }
        }
    }
}

data class PublicPage(val title:String,val intro:String,val sections:List<Pair<String,String>>)

val publicPages=mapOf(
 "funcionalidades" to PublicPage("Funcionalidades","Soluções digitais para transformar a gestão industrial.",listOf(
   "Cloud Computing" to "Infraestrutura escalável para dados e processos críticos.",
   "Inteligência Artificial" to "Automação e análise para apoiar decisões operacionais.",
   "Apps Mobile" to "Interfaces intuitivas para iOS e Android.",
   "Big Data" to "Análise estratégica de grandes volumes de informação.",
   "IoT Solutions" to "Conectividade entre dispositivos e sensores.",
   "Controle de Manutenção" to "Ordens preventivas e corretivas estruturadas.",
   "Estoque de Peças" to "Rastreabilidade física e níveis críticos de insumos.",
   "Métricas de OEE" to "Eficiência, performance e qualidade em um único painel."
 )),
 "planos" to PublicPage("Planos de Gestão GeTech","Escolha a inteligência ideal para a vida útil do seu maquinário.",listOf(
   "Essencial — R$ 499/mês" to "Manutenção corretiva agendada • Relatórios mensais em PDF • Suporte em até 24h • Até 5 máquinas.",
   "Pro Performance — R$ 1.299/mês" to "Manutenção preditiva com IoT • Dashboard em tempo real • Suporte prioritário 4h • Até 20 máquinas • Análise de vibração.",
   "Enterprise — Sob Consulta" to "Parque industrial ilimitado • Consultoria técnica dedicada • Integração via API • Treinamento in-loco."
 )),
 "depoimentos" to PublicPage("Depoimentos","Experiências sobre uma gestão industrial centralizada.",listOf(
   "Operação" to "Mais visibilidade sobre ativos e tarefas críticas.",
   "Manutenção" to "Histórico organizado e acompanhamento das preventivas.",
   "Gestão" to "Indicadores reunidos em um único ambiente."
 )),
 "integrações" to PublicPage("Integrações","Conectividade entre processos e informações da operação.",listOf(
   "Dados operacionais" to "Centralização das informações de ativos, ordens e movimentações.",
   "Indicadores" to "Estrutura preparada para métricas e relatórios.",
   "Sistemas" to "Arquitetura modular preparada para futuras integrações."
 )),
 "faq" to PublicPage("FAQ","Perguntas frequentes sobre a plataforma.",listOf(
   "O que é a GeTech?" to "Uma plataforma integrada de gestão de ativos e processos industriais.",
   "Os dados ficam onde?" to "Nesta edição, contas, sessão e chamados são armazenados localmente no dispositivo.",
   "Existe hierarquia?" to "Sim. Gestor acessa o ERP administrativo e Cliente acessa o portal de atendimento.",
   "O aplicativo usa Firebase?" to "Não. O armazenamento está local nesta versão."
 )),
 "sobre" to PublicPage("Sobre a GeTech","Tecnologia aplicada à gestão de manutenção e ativos industriais.",listOf(
   "Atuação" to "Setores automotivo, alimentício e metalúrgico com tecnologia de ponta.",
   "Preventiva" to "Redução de custos emergenciais e aumento da vida útil dos equipamentos.",
   "Ecossistema" to "Manutenção, estoque, produção, RH, pedidos, qualidade, suprimentos e auditoria."
 )),
 "ajuda" to PublicPage("Ajuda","Orientações para utilizar os recursos do aplicativo.",listOf(
   "Acesso" to "Entre com uma conta existente ou crie uma conta de Cliente.",
   "Gestor" to "Use o painel administrativo para acessar os módulos internos.",
   "Cliente" to "Use Atendimento, Chatbot e Realidade Aumentada."
 )),
 "contato" to PublicPage("Contato","Entre em contato com a GeTech ou registre uma solicitação.",listOf(
   "Atendimento técnico" to "O Chatbot permite registrar nome, e-mail e problema em um chamado local.",
   "Informações" to "Descreva o equipamento e o problema com o máximo de clareza possível."
 )),
 "privacidade" to PublicPage("Política de Privacidade","Informações sobre o tratamento local dos dados desta versão.",listOf(
   "Armazenamento" to "Contas, sessão e chamados são mantidos no armazenamento privado do aplicativo.",
   "Controle" to "O usuário pode sair da conta. A limpeza completa pode ser feita pela recuperação de dados locais.",
   "Firebase" to "Firebase permanece desativado nesta versão."
 )),
 "configuracoes" to PublicPage("Configurações","Preferências locais e experiência do aplicativo.",listOf(
   "Tema" to "O tema pode ser alternado entre a identidade tecnológica escura e a versão clara corporativa.",
   "Sessão" to "A sessão permanece localmente até o usuário sair.",
   "Dados" to "Os dados desta edição são locais e não são sincronizados com servidor."
 )),
 "blog" to PublicPage("Blog","Conteúdos sobre tecnologia e gestão industrial.",listOf(
   "Manutenção preventiva" to "Planeje inspeções e intervenções antes de falhas causarem parada.",
   "Indicadores" to "Disponibilidade, performance e qualidade ajudam a acompanhar a operação.",
   "Digitalização" to "Conecte processos em uma estrutura única."
 ))
)

@Composable
fun HomeScreen(onLogin:()->Unit,onPage:(String)->Unit,onChat:()->Unit,onThemeToggle:()->Unit) {
 TechScaffold("GeTech",actions={
   TextButton(onClick=onThemeToggle){Text("☀︎ / ☾",color=MaterialTheme.colorScheme.onSurface)}
   Text("SITE C",color=MaterialTheme.colorScheme.primary,modifier=Modifier.padding(end=10.dp))
 }){pad->
  LazyColumn(Modifier.padding(pad).padding(horizontal=18.dp,vertical=22.dp),verticalArrangement=Arrangement.spacedBy(18.dp)){
   item{TechCard{
     StatusBadge("●  Servidor Principal: Operacional")
     Spacer(Modifier.height(8.dp))
     Text("Plataforma Integrada de Gestão de Ativos",style=MaterialTheme.typography.headlineLarge,color=MaterialTheme.colorScheme.onSurface)
     Text("Monitore a eficiência global de seus equipamentos, controle ordens de serviço, gerencie a cadeia de suprimentos e reduza o tempo de inatividade em um único ecossistema digital inteligente.",color=MaterialTheme.colorScheme.onSurfaceVariant)
     Spacer(Modifier.height(16.dp))
     Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)){
       PrimaryButton("Entrar no Painel",Modifier.weight(1f),onLogin)
       SecondaryButton("Ver funcionalidades",Modifier.weight(1f)){onPage("funcionalidades")}
     }
   }}
   item{SectionTitle("Módulos Estruturais do Sistema")}
   items(listOf(
    "⚙️" to ("Controle de Manutenção" to "Ordens de serviço preventivas e corretivas estruturadas."),
    "📦" to ("Estoque de Peças" to "Rastreabilidade física e níveis críticos de insumos de reposição."),
    "📊" to ("Métricas de OEE" to "Eficiência, performance e qualidade."),
    "🔒" to ("Auditoria & Logs" to "Histórico digital de eventos operacionais.")
   )){(icon,data)->TechCard(title=data.first,icon=icon){Text(data.second,color=MaterialTheme.colorScheme.onSurfaceVariant)}}
   item{SectionTitle("Impacto Operacional Global")}
   item{Row(horizontalArrangement=Arrangement.spacedBy(10.dp)){
     StatCard("+150","Plantas Industriais Atendidas",Modifier.weight(1f))
     StatCard("99.8%","Disponibilidade de Dados",Modifier.weight(1f))
   }}
   item{TechCard{Text("24/7",fontSize=36.sp,fontWeight=FontWeight.ExtraBold,color=MaterialTheme.colorScheme.primary);Text("Monitoramento Ativo e Alertas",color=MaterialTheme.colorScheme.onSurfaceVariant)}}
   item{SectionTitle("Acesso rápido");Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){SecondaryButton("Chatbot",Modifier.weight(1f),onChat);SecondaryButton("FAQ",Modifier.weight(1f)){onPage("faq")}}}
   item{SectionTitle("Acesso institucional")}
   item{Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){SecondaryButton("Planos",Modifier.weight(1f)){onPage("planos")};SecondaryButton("Sobre",Modifier.weight(1f)){onPage("sobre")}}}
   item{Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){SecondaryButton("Contato",Modifier.weight(1f)){onPage("contato")};SecondaryButton("Ajuda",Modifier.weight(1f)){onPage("ajuda")}}}
  }
 }
}

@Composable
fun PublicPageScreen(page:PublicPage,onBack:()->Unit){
 TechScaffold(page.title,onBack=onBack){pad->
  LazyColumn(Modifier.padding(pad).padding(18.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
   item{Text(page.intro,style=MaterialTheme.typography.titleLarge,color=MaterialTheme.colorScheme.primary)}
   items(page.sections){(title,description)->
    var expanded by remember{mutableStateOf(false)}
    TechCard(modifier=Modifier.animateContentSize(),title=title){
      Text(description,color=MaterialTheme.colorScheme.onSurfaceVariant)
      if(page.title=="Planos de Gestão GeTech") StatusBadge(if(title.startsWith("Pro")) "MAIS POPULAR" else "PLANO")
      TextButton(onClick={expanded=!expanded}){Text(if(expanded)"Ocultar detalhes" else "Ver detalhes")}
      if(expanded) Text(
       when(title){
        "Cloud Computing"->"Estrutura escalável para centralizar informações operacionais."
        "Apps Mobile"->"Interfaces intuitivas e adaptadas ao uso em campo."
        "IoT Solutions"->"Sensores e monitoramento remoto podem alimentar o ecossistema."
        "Manutenção preventiva"->"Planejamento reduz paradas e aumenta a vida útil dos ativos."
        else->"Informação apresentada pelo projeto GeTech e adaptada para a experiência nativa."
       },color=MaterialTheme.colorScheme.onSurface)
    }
   }
  }
 }
}
