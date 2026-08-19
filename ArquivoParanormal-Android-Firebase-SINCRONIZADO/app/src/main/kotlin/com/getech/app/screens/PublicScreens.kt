package com.getech.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.getech.app.ui.*

data class PublicPage(val title:String,val intro:String,val sections:List<Pair<String,String>>)

val publicPages = mapOf(
    "funcionalidades" to PublicPage("Funcionalidades","Uma plataforma integrada para gestão de ativos industriais.",listOf(
        "Controle de Manutenção" to "Ordens preventivas e corretivas estruturadas, com histórico e acompanhamento.",
        "Estoque de Peças" to "Rastreabilidade física e níveis críticos de insumos de reposição.",
        "Métricas de OEE" to "Acompanhamento de eficiência, performance e qualidade.",
        "Auditoria & Logs" to "Histórico digital dos eventos operacionais."
    )),
    "planos" to PublicPage("Planos","Escolha a estrutura adequada para a sua operação.",listOf(
        "Essencial" to "Recursos fundamentais para organização da manutenção e estoque.",
        "Profissional" to "Gestão ampliada, ordens, indicadores e acompanhamento operacional.",
        "Enterprise" to "Estrutura para operações maiores, com módulos e governança."
    )),
    "depoimentos" to PublicPage("Depoimentos","Experiências de quem utiliza uma gestão industrial centralizada.",listOf(
        "Operação" to "Mais visibilidade sobre ativos e tarefas críticas.",
        "Manutenção" to "Histórico organizado e acompanhamento das preventivas.",
        "Gestão" to "Indicadores em um único ambiente."
    )),
    "integrações" to PublicPage("Integrações","Conecte processos e informações da operação.",listOf(
        "Dados operacionais" to "Centralização das informações de ativos e ordens.",
        "Indicadores" to "Estrutura preparada para métricas e relatórios.",
        "Sistemas" to "Arquitetura modular para futuras integrações."
    )),
    "faq" to PublicPage("FAQ","Perguntas frequentes sobre a plataforma.",listOf(
        "O que é a GeTech?" to "Uma plataforma de gestão industrial com manutenção, estoque, ordens, qualidade e outros módulos.",
        "Os dados ficam onde?" to "Nesta versão do aplicativo, os dados de autenticação e chamados ficam localmente no dispositivo.",
        "Há hierarquia de acesso?" to "Sim. Gestor possui o painel administrativo e Cliente possui a área de atendimento."
    )),
    "blog" to PublicPage("Blog","Conteúdos e informações sobre gestão industrial.",listOf(
        "Manutenção preventiva" to "Organize inspeções e intervenções antes que falhas provoquem parada.",
        "Indicadores" to "Use indicadores para acompanhar disponibilidade, performance e qualidade.",
        "Digitalização" to "Conecte processos em uma estrutura única."
    )),
    "sobre" to PublicPage("Sobre a GeTech","Plataforma integrada de gestão de ativos e processos industriais.",listOf(
        "Missão" to "Aproximar tecnologia e operação para reduzir perdas e aumentar a visibilidade.",
        "Escopo" to "Manutenção, estoque, produção, RH, pedidos, qualidade, suprimentos e auditoria.",
        "Arquitetura" to "Aplicativo modular com navegação nativa e dados locais nesta edição."
    )),
    "ajuda" to PublicPage("Ajuda","Encontre orientações para utilizar os recursos do aplicativo.",listOf(
        "Acesso" to "Entre com uma conta ou crie uma nova conta de Cliente.",
        "Gestor" to "Use o painel administrativo para acessar os módulos internos.",
        "Cliente" to "Use atendimento e chatbot para registrar solicitações."
    )),
    "privacidade" to PublicPage("Política de Privacidade","Nesta versão offline/local, os dados criados no aplicativo são armazenados no próprio dispositivo.",listOf(
        "Armazenamento" to "Contas, sessão e chamados ficam no armazenamento local do aplicativo.",
        "Controle" to "O usuário pode sair da conta e limpar os dados locais pelas configurações.",
        "Firebase" to "Firebase não é utilizado nesta versão."
    )),
    "contato" to PublicPage("Contato","Entre em contato com a GeTech ou abra um atendimento pelo chatbot.",listOf(
        "Atendimento" to "Use o módulo Chatbot para registrar um chamado.",
        "Informações" to "Forneça nome, e-mail e descrição do problema para a equipe técnica."
    )),
    "configuracoes" to PublicPage("Configurações","Preferências do aplicativo e sessão local.",listOf(
        "Tema" to "A interface nativa utiliza o tema escuro tecnológico da GeTech.",
        "Sessão" to "A sessão permanece no dispositivo até você sair.",
        "Dados locais" to "Os dados podem ser apagados pelo painel de configurações."
    ))
)

@Composable
fun HomeScreen(onLogin:()->Unit,onPage:(String)->Unit,onChat:()->Unit) {
    TechScaffold("GeTech", actions={Text("ERP INDUSTRIAL",color=Cyan,modifier=Modifier.padding(end=14.dp))}){pad->
        LazyColumn(Modifier.padding(pad).padding(18.dp),verticalArrangement=Arrangement.spacedBy(18.dp)){
            item {
                TechCard {
                    Text("Painel Central - ERP Industrial",style=MaterialTheme.typography.headlineLarge,color=TextPrimary)
                    Text("Servidor Principal: Operacional",color=Green,fontWeight=androidx.compose.ui.text.font.FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text("Plataforma Integrada de Gestão de Ativos",style=MaterialTheme.typography.titleLarge,color=Cyan)
                    Text("Monitore a eficiência global de seus equipamentos, controle ordens de serviço, gerencie a cadeia de suprimentos e reduza o tempo de inatividade em um único ecossistema digital inteligente.",color=TextSecondary)
                    Spacer(Modifier.height(14.dp))
                    Row(horizontalArrangement=Arrangement.spacedBy(10.dp)){PrimaryButton("Entrar no Painel",onClick=onLogin);SecondaryButton("Conhecer o ERP",onClick={onPage("funcionalidades")})}
                }
            }
            item { SectionTitle("Módulos Estruturais do Sistema") }
            items(listOf(
                "⚙️" to ("Controle de Manutenção" to "Ordens de serviço preventivas e corretivas estruturadas."),
                "📦" to ("Estoque de Peças" to "Rastreabilidade física e níveis críticos de insumos."),
                "📊" to ("Métricas de OEE" to "Eficiência, performance e qualidade."),
                "🔒" to ("Auditoria & Logs" to "Histórico digital dos eventos.")
            )) { (icon,data)-> TechCard(title=data.first,icon=icon){Text(data.second,color=TextSecondary)} }
            item {
                SectionTitle("Impacto Operacional Global")
                Row(horizontalArrangement=Arrangement.spacedBy(10.dp)){StatCard("+150","Plantas Industriais Atendidas");StatCard("99.8%","Disponibilidade de Dados")}
                Spacer(Modifier.height(10.dp));TechCard{Text("24/7",color=Cyan,fontSize=30.sp,fontWeight=androidx.compose.ui.text.font.FontWeight.ExtraBold);Text("Monitoramento Ativo e Alertas",color=TextSecondary)}
            }
            item { SectionTitle("Acesso rápido");Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){SecondaryButton("Chatbot",Modifier.weight(1f),onChat);SecondaryButton("FAQ",Modifier.weight(1f),onClick={onPage("faq")})} }
            item {
                SectionTitle("Navegação pública")
                Column(verticalArrangement=Arrangement.spacedBy(8.dp)) {
                    listOf(
                        "funcionalidades" to "Funcionalidades",
                        "planos" to "Planos",
                        "depoimentos" to "Depoimentos",
                        "integrações" to "Integrações",
                        "blog" to "Blog",
                        "sobre" to "Sobre",
                        "ajuda" to "Ajuda",
                        "contato" to "Contato",
                        "privacidade" to "Privacidade",
                        "configuracoes" to "Configurações"
                    ).chunked(2).forEach { row ->
                        Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                            row.forEach { (route,label) -> SecondaryButton(label,Modifier.weight(1f)){onPage(route)} }
                            if(row.size==1) Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PublicPageScreen(page:PublicPage,onBack:()->Unit){
    TechScaffold(page.title,onBack=onBack){pad->LazyColumn(Modifier.padding(pad).padding(18.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
        item{Text(page.intro,style=MaterialTheme.typography.titleLarge,color=Cyan)}
        items(page.sections){(t,d)->TechCard(title=t){Text(d,color=TextSecondary)}}
    }}
}
