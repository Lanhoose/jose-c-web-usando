@file:OptIn(ExperimentalMaterial3Api::class)

package com.getech.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.navigation.NavType
import androidx.navigation.navArgument

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repo = GeTechRepository(this)
        setContent { GeTechApp(repo) }
    }
}

private object Routes {
    const val HOME="home"; const val LOGIN="login"; const val CADASTRO="cadastro"; const val RECUPERAR="recuperar"
    const val CLIENTE="cliente"; const val PORTAL="portal"; const val ERP="erp"; const val ERP_HOME="erp_home"
    const val ESTOQUE="estoque"; const val MANUT="manutencao"; const val MAQUINAS="maquinas"; const val RH="rh"
    const val PEDIDOS="pedidos"; const val QUALIDADE="qualidade"; const val SUPRIMENTOS="suprimentos"; const val PRODUCAO="producao"
    const val LOGS="logs"; const val SISTEMA="sistema"; const val ERP_CONFIG="erp_config"
    const val CONTATO="contato"; const val MATERIAIS="materiais"; const val CHATBOT="chatbot"; const val SOBRE="sobre"
    const val FUNC="funcionalidades"; const val PLANOS="planos"; const val DEPOIMENTOS="depoimentos"; const val FAQ="faq"
    const val AJUDA="ajuda"; const val PRIVACIDADE="privacidade"; const val INTEGRACOES="integracoes"; const val AR="ar"
    const val BLOG="blog"; const val MENSAGENS="mensagens"; const val ORCAMENTOS="orcamentos"; const val CONFIG="config"
}

@Composable
private fun GeTechApp(repo: GeTechRepository) {
    var dark by remember { mutableStateOf(false) }
    var session by remember { mutableStateOf(repo.session()) }
    val start = if (session?.perfil == Perfil.GESTOR) Routes.PORTAL else if (session?.perfil == Perfil.CLIENTE) Routes.CLIENTE else Routes.HOME
    val nav = rememberNavController()

    fun logout() {
        repo.logout()
        session = null
        while(nav.popBackStack()) { }; nav.navigate(Routes.HOME)
    }

    GeTechTheme(dark) {
        NavHost(navController = nav, startDestination = start) {
            composable(Routes.HOME) { HomeScreen(nav) }
            composable(Routes.LOGIN) {
                LoginScreen(nav,repo) { s -> session=s; nav.navigate(if(s.perfil==Perfil.GESTOR) Routes.PORTAL else Routes.CLIENTE){popUpTo(Routes.LOGIN){inclusive=true}} }
            }
            composable(Routes.CADASTRO) { CadastroScreen(nav,repo) { nav.popBackStack() } }
            composable(Routes.RECUPERAR) { RecuperarScreen(nav,repo) { nav.popBackStack() } }
            composable(Routes.CONTATO) { InfoScreen(nav,"GeTech Soluções Industriais","Fale com a equipe técnica industrial e solicite atendimento.") }
            composable(Routes.MATERIAIS) { MaterialsScreen(nav) }
            composable(Routes.CHATBOT) { ChatbotScreen(repo, nav) }
            composable(Routes.SOBRE) { InfoScreen(nav,"Sobre a GeTech","A tecnologia por trás do controle de ativos industriais: missão, visão e governança do ERP GeTech.") }
            composable(Routes.FUNC) { InfoScreen(nav,"Funcionalidades","Cloud, inteligência artificial, cibersegurança, apps mobile, big data e IoT aplicados à indústria.") }
            composable(Routes.PLANOS) { InfoScreen(nav,"Planos de Gestão","Estratégias de gestão de ativos e operação industrial da GeTech.") }
            composable(Routes.DEPOIMENTOS) { TestimonialsScreen(repo,nav) }
            composable(Routes.FAQ) { FaqScreen(nav) }
            composable(Routes.AJUDA) { InfoScreen(nav,"Central de Ajuda","Acesso e conta, pagamentos, pedidos e suporte direto: encontre respostas rápidas da GeTech.") }
            composable(Routes.PRIVACIDADE) { PrivacyScreen(nav) }
            composable(Routes.INTEGRACOES) { InfoScreen(nav,"Integrações do ERP","Conecte o ERP GeTech a sensores IoT, CLPs, ERPs financeiros e ferramentas de gestão via API.") }
            composable(Routes.AR) { InfoScreen(nav,"Visualização 3D e Realidade Aumentada","Recursos de apoio ao trabalho em campo. Nesta versão nativa, a interface é preparada para integração futura.") }
            composable(Routes.BLOG) { BlogScreen(repo,nav) }
            composable(Routes.MENSAGENS) { MessagesScreen(repo,nav) }
            composable(Routes.ORCAMENTOS) { GenericCrudScreen(repo,nav,"orcamento","Orçamentos","Solicitações e itens de orçamento.") }
            composable(Routes.CONFIG) { AccountSettingsScreen(repo,nav,dark){dark=it} }

            composable(Routes.CLIENTE) {
                GuardScreen(session, Perfil.CLIENTE, nav) {
                    ClientScreen(repo,nav,session!!,::logout)
                }
            }
            composable(Routes.PORTAL) {
                GuardScreen(session, Perfil.GESTOR, nav) { PortalScreen(nav,session!!,::logout) }
            }
            composable(Routes.ERP) { GuardScreen(session,Perfil.GESTOR,nav){ ErpScreen(nav,session!!,::logout) } }
            composable(Routes.ERP_HOME) { GuardScreen(session,Perfil.GESTOR,nav){ ErpScreen(nav,session!!,::logout) } }
            composable(Routes.ESTOQUE) { GuardScreen(session,Perfil.GESTOR,nav){ ModuleScreen(repo,nav,"estoque","Gestão de Inventário","Controle de entrada e saída de materiais industriais.") } }
            composable(Routes.MANUT) { GuardScreen(session,Perfil.GESTOR,nav){ ModuleScreen(repo,nav,"manutencao","Manutenção Ativa","Ordens de manutenção de máquinas e equipamentos.") } }
            composable(Routes.MAQUINAS) { GuardScreen(session,Perfil.GESTOR,nav){ ModuleScreen(repo,nav,"maquinas","Gestão de Máquinas","Inventário de equipamentos e dados técnicos.") } }
            composable(Routes.RH) { GuardScreen(session,Perfil.GESTOR,nav){ ModuleScreen(repo,nav,"colaboradores","RH & Ponto Digital","Funcionários, dados e últimos registros de ponto.") } }
            composable(Routes.PEDIDOS) { GuardScreen(session,Perfil.GESTOR,nav){ OrdersScreen(repo,nav) } }
            composable(Routes.QUALIDADE) { GuardScreen(session,Perfil.GESTOR,nav){ ModuleScreen(repo,nav,"qualidade","Controle de Qualidade","Inspeções, lotes, resultados e acompanhamento.") } }
            composable(Routes.SUPRIMENTOS) { GuardScreen(session,Perfil.GESTOR,nav){ ModuleScreen(repo,nav,"suprimentos","Suprimentos","Requisições, fornecedores, quantidades e status.") } }
            composable(Routes.PRODUCAO) { GuardScreen(session,Perfil.GESTOR,nav){ ModuleScreen(repo,nav,"producao","Linha de Produção","Apontamentos, metas, turnos e produção.") } }
            composable(Routes.LOGS) { GuardScreen(session,Perfil.GESTOR,nav){ LogsScreen(repo,nav) } }
            composable(Routes.SISTEMA) { GuardScreen(session,Perfil.GESTOR,nav){ SistemaScreen(repo,nav) } }
            composable(Routes.ERP_CONFIG) { GuardScreen(session,Perfil.GESTOR,nav){ ErpConfigScreen(repo,nav,dark){dark=it} } }
        }
    }
}

@Composable
private fun AppBar(nav: NavHostController, title:String, session:Sessao?=null, onLogout:(()->Unit)?=null) {
    TopAppBar(
        title={Text(title,fontWeight=FontWeight.Bold)},
        navigationIcon={ IconButton(onClick={ if(!nav.popBackStack()) nav.navigate(Routes.HOME) }) { Icon(Icons.Default.ArrowBack,"Voltar") } },
        actions={
            if(session!=null) IconButton(onClick={onLogout?.invoke()}){Icon(Icons.Default.Logout,"Sair")}
        }
    )
}

@Composable
private fun Page(nav:NavHostController,title:String,session:Sessao?=null,onLogout:(()->Unit)?=null,content:@Composable ColumnScope.()->Unit){
    Scaffold(topBar={AppBar(nav,title,session,onLogout)}){pad->
        Column(Modifier.fillMaxSize().padding(pad).verticalScroll(rememberScrollState()).padding(18.dp),content=content)
    }
}

@Composable
private fun HomeScreen(nav:NavHostController){
    Scaffold(topBar={
        TopAppBar(title={Row(verticalAlignment=Alignment.CenterVertically){
            Image(painterResource(com.getech.app.R.drawable.getech_logo),null,Modifier.size(38.dp))
            Spacer(Modifier.width(8.dp)); Text("GeTech",fontWeight=FontWeight.Bold)
        }},actions={IconButton(onClick={nav.navigate(Routes.LOGIN)}){Icon(Icons.Default.Person,"Login")}})
    }){p->
        Column(Modifier.fillMaxSize().padding(p).verticalScroll(rememberScrollState())){
            Box(Modifier.fillMaxWidth().height(290.dp).background(Brush.verticalGradient(listOf(Color(0xFF202B4A),Color(0xFF4668E7)))).padding(24.dp)){
                Column(Modifier.align(Alignment.CenterStart)){
                    Text("Manutenção de Máquinas Pesadas",style=MaterialTheme.typography.headlineMedium,fontWeight=FontWeight.Bold,color=Color.White)
                    Spacer(Modifier.height(12.dp))
                    Text("Tecnologia, controle de ativos e operação industrial em uma experiência GeTech nativa para Android.",color=Color.White.copy(.88f))
                    Spacer(Modifier.height(20.dp))
                    Button(onClick={nav.navigate(Routes.CHATBOT)}){Icon(Icons.Default.Chat,null);Spacer(Modifier.width(8.dp));Text("Falar com o Assistente")}
                }
            }
            Column(Modifier.padding(18.dp)){
                Text("Sobre a GeTech",style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text("Atuação industrial com foco em confiabilidade, manutenção e gestão de ativos.")
                Spacer(Modifier.height(14.dp))
                Row(horizontalArrangement=Arrangement.spacedBy(12.dp)){
                    SmallFeature("Atuação","Gestão integrada de ativos e operações.",Modifier.weight(1f))
                    SmallFeature("Preventiva","Planejamento e acompanhamento de manutenção.",Modifier.weight(1f))
                }
                Spacer(Modifier.height(22.dp))
                Text("Nossas Especialidades",style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.Bold)
                Spacer(Modifier.height(10.dp))
                listOf("Manutenção industrial","Gestão de máquinas","Estoque e suprimentos","Qualidade","Produção","Tecnologia e dados").forEach{FeatureCard(it)}
                Spacer(Modifier.height(20.dp))
                Text("Acesso rápido",style=MaterialTheme.typography.titleLarge,fontWeight=FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                QuickButton("Materiais e Manuais"){nav.navigate(Routes.MATERIAIS)}
                QuickButton("Contato"){nav.navigate(Routes.CONTATO)}
                QuickButton("Sobre a Plataforma"){nav.navigate(Routes.SOBRE)}
                QuickButton("Entrar no sistema"){nav.navigate(Routes.LOGIN)}
                Spacer(Modifier.height(20.dp))
                Text("Assistente Virtual GeTech",style=MaterialTheme.typography.titleLarge,fontWeight=FontWeight.Bold)
                Text("Converse com o assistente e registre um chamado quando a funcionalidade estiver disponível localmente.")
                Spacer(Modifier.height(10.dp))
                OutlinedButton(onClick={nav.navigate(Routes.CHATBOT)}){Icon(Icons.Default.Chat,null);Spacer(Modifier.width(8.dp));Text("Abrir chatbot")}
            }
        }
    }
}

@Composable private fun SmallFeature(title:String,text:String,modifier:Modifier=Modifier){
    Card(modifier){Column(Modifier.padding(14.dp)){Text(title,fontWeight=FontWeight.Bold,color=MaterialTheme.colorScheme.primary);Text(text,style=MaterialTheme.typography.bodySmall)}}
}
@Composable private fun FeatureCard(title:String){Card(Modifier.fillMaxWidth().padding(vertical=4.dp)){Row(Modifier.padding(14.dp),verticalAlignment=Alignment.CenterVertically){Icon(Icons.Default.CheckCircle,null,tint=MaterialTheme.colorScheme.primary);Spacer(Modifier.width(10.dp));Text(title,fontWeight=FontWeight.SemiBold)}}}
@Composable private fun QuickButton(text:String,onClick:()->Unit){OutlinedButton(onClick=onClick,modifier=Modifier.fillMaxWidth().padding(vertical=4.dp)){Text(text)}}


@Composable
private fun LoginScreen(nav:NavHostController,repo:GeTechRepository,onSuccess:(Sessao)->Unit){
    var email by remember{mutableStateOf("")}
    var senha by remember{mutableStateOf("")}
    var erro by remember{mutableStateOf("")}
    Page(nav,"Entrar") {
        Image(painterResource(R.drawable.getech_logo),null,Modifier.size(86.dp).align(Alignment.CenterHorizontally))
        Spacer(Modifier.height(10.dp))
        Text("Acesse sua conta GeTech",style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.Bold)
        Text("Acesse sua conta GeTech como cliente ou gestor.",color=MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(20.dp))
        OutlinedTextField(email,{email=it},label={Text("E-mail")},modifier=Modifier.fillMaxWidth(),singleLine=true)
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(senha,{senha=it},label={Text("Senha")},visualTransformation=PasswordVisualTransformation(),modifier=Modifier.fillMaxWidth(),singleLine=true)
        if(erro.isNotBlank()){Spacer(Modifier.height(8.dp));Text(erro,color=MaterialTheme.colorScheme.error)}
        Spacer(Modifier.height(14.dp))
        Button(onClick={repo.login(email,senha)?.let{onSuccess(it)} ?: run{erro="E-mail ou senha inválidos."}},modifier=Modifier.fillMaxWidth()){Text("Entrar")}
        TextButton(onClick={nav.navigate(Routes.RECUPERAR)},modifier=Modifier.align(Alignment.CenterHorizontally)){Text("Esqueci minha senha")}
        OutlinedButton(onClick={nav.navigate(Routes.CADASTRO)},modifier=Modifier.fillMaxWidth()){Text("Criar cadastro")}
        Spacer(Modifier.height(18.dp))
        Card(Modifier.fillMaxWidth()){Column(Modifier.padding(14.dp)){Text("Contas de demonstração",fontWeight=FontWeight.Bold);Text("Gestor: gestor@getech.com / getech123");Text("Cliente: cliente@getech.com / getech123")}}
    }
}

@Composable
private fun CadastroScreen(nav:NavHostController,repo:GeTechRepository,onDone:()->Unit){
    var nome by remember{mutableStateOf("")};var email by remember{mutableStateOf("")};var senha by remember{mutableStateOf("")}
    var perfil by remember{mutableStateOf(Perfil.CLIENTE)};var msg by remember{mutableStateOf("")}
    Page(nav,"Cadastro"){
        Text("Criar conta",style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.Bold)
        Spacer(Modifier.height(14.dp))
        OutlinedTextField(nome,{nome=it},label={Text("Nome")},modifier=Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp));OutlinedTextField(email,{email=it},label={Text("E-mail")},modifier=Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp));OutlinedTextField(senha,{senha=it},label={Text("Senha")},visualTransformation=PasswordVisualTransformation(),modifier=Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp));Text("Perfil")
        Row{RadioButton(perfil==Perfil.CLIENTE,{perfil=Perfil.CLIENTE});Text("Cliente",Modifier.padding(top=12.dp));Spacer(Modifier.width(12.dp));RadioButton(perfil==Perfil.GESTOR,{perfil=Perfil.GESTOR});Text("Gestor",Modifier.padding(top=12.dp))}
        if(msg.isNotBlank()){Text(msg,color=MaterialTheme.colorScheme.primary)}
        Button(onClick={if(nome.isBlank()||email.isBlank()||senha.isBlank())msg="Preencha todos os campos." else if(repo.register(nome,email,senha,perfil)){msg="Cadastro realizado.";onDone()}else msg="Este e-mail já está cadastrado."},modifier=Modifier.fillMaxWidth()){Text("Cadastrar")}
    }
}

@Composable
private fun RecuperarScreen(nav:NavHostController,repo:GeTechRepository,onDone:()->Unit){;var email by remember{mutableStateOf("")};var senha by remember{mutableStateOf("")};var msg by remember{mutableStateOf("")}
    Page(nav,"Recuperar senha"){
        Text("Redefinição local de senha",style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.Bold)
        Text("A recuperação desta versão funciona localmente, sem backend.")
        Spacer(Modifier.height(14.dp))
        OutlinedTextField(email,{email=it},label={Text("E-mail")},modifier=Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp));OutlinedTextField(senha,{senha=it},label={Text("Nova senha")},visualTransformation=PasswordVisualTransformation(),modifier=Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        Button(onClick={if(repo.resetPassword(email,senha)){msg="Senha redefinida.";onDone()}else msg="Usuário não encontrado."},modifier=Modifier.fillMaxWidth()){Text("Redefinir senha")}
        if(msg.isNotBlank())Text(msg)
    }
}

@Composable
private fun GuardScreen(session:Sessao?,required:Perfil,nav:NavHostController,content:@Composable () -> Unit){
    if(session==null){ LaunchedEffect(Unit){nav.navigate(Routes.LOGIN){launchSingleTop=true}} }
    else if(session.perfil!=required){ LaunchedEffect(Unit){nav.navigate(if(session.perfil==Perfil.GESTOR)Routes.PORTAL else Routes.CLIENTE){launchSingleTop=true}} }
    else content()
}

@Composable
private fun InfoScreen(nav:NavHostController,title:String,description:String){
    Page(nav,title){
        Text(title,style=MaterialTheme.typography.headlineMedium,fontWeight=FontWeight.Bold)
        Spacer(Modifier.height(8.dp));Text(description)
        Spacer(Modifier.height(18.dp))
        listOf(
            "Arquitetura preparada para smartphone",
            "Identidade visual GeTech preservada",
            "Dados locais para demonstração",
            "Navegação Android nativa",
            "Integração com backend pode ser adicionada futuramente"
        ).forEach{FeatureCard(it)}
    }
}

@Composable
private fun MaterialsScreen(nav:NavHostController){
    Page(nav,"Materiais e Manuais"){
        Text("Materiais e manuais",style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.Bold)
        Text("Baixe a documentação das máquinas.")
        Spacer(Modifier.height(12.dp))
        listOf("Manual técnico — Torno CNC Romi GL 240","Manual — Prensa Hidráulica 150t","Guia de manutenção preventiva","Procedimento de inspeção dimensional").forEach{
            Card(Modifier.fillMaxWidth().padding(vertical=4.dp)){Row(Modifier.padding(14.dp),verticalAlignment=Alignment.CenterVertically){Icon(Icons.Default.Description,null);Spacer(Modifier.width(10.dp));Text(it,Modifier.weight(1f));Icon(Icons.Default.Download,"Disponível localmente")}}
        }
        Spacer(Modifier.height(16.dp));Text("Informações Adicionais",fontWeight=FontWeight.Bold)
        Text("Os arquivos disponibilizados pelo projeto Web devem ser incorporados como assets locais quando fornecidos. Esta conversão não depende de URLs externas.")
    }
}

@Composable
private fun ChatbotScreen(repo:GeTechRepository,nav:NavHostController){
    var input by remember{mutableStateOf("")}
    val messages=remember{mutableStateListOf(ChatMessage(false,"Olá! Sou o Assistente Virtual GeTech. Como posso ajudar?"))}
    Scaffold(topBar={AppBar(nav,"Assistente Virtual GeTech")},bottomBar={
        Row(Modifier.fillMaxWidth().padding(8.dp),verticalAlignment=Alignment.CenterVertically){
            OutlinedTextField(input,{input=it},modifier=Modifier.weight(1f),placeholder={Text("Digite sua pergunta...")},singleLine=true)
            IconButton(onClick={if(input.isNotBlank()){messages.add(ChatMessage(true,input));repo.log("CHAT","Pergunta enviada ao Assistente Virtual GeTech");messages.add(ChatMessage(false,"Recebi sua mensagem. A integração de IA/backend externo não foi inventada nesta conversão; posso registrar um chamado local quando suportado."));input=""}}){Icon(Icons.Default.Send,"Enviar")}
        }
    }){p->LazyColumn(Modifier.fillMaxSize().padding(p).padding(12.dp),verticalArrangement=Arrangement.spacedBy(8.dp)){
        items(messages){m->Row(Modifier.fillMaxWidth(),horizontalArrangement=if(m.fromUser)Arrangement.End else Arrangement.Start){Surface(shape=RoundedCornerShape(18.dp),color=if(m.fromUser)MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant){Text(m.text,Modifier.padding(12.dp),color=if(m.fromUser)MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant)}}}
    }}
}

@Composable
private fun ClientScreen(repo:GeTechRepository,nav:NavHostController,s:Sessao,logout:()->Unit){
    Page(nav,"Minha Área",s,logout){
        Text("Olá, ${s.nome}",style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.Bold)
        Text("Acompanhe seus chamados abertos com a GeTech.")
        Spacer(Modifier.height(16.dp))
        QuickButton("Materiais e manuais"){nav.navigate(Routes.MATERIAIS)}
        QuickButton("Abrir novo chamado"){nav.navigate(Routes.CHATBOT)}
        QuickButton("Configurações da conta"){nav.navigate(Routes.CONFIG)}
        Spacer(Modifier.height(18.dp));Text("Meus chamados",style=MaterialTheme.typography.titleLarge,fontWeight=FontWeight.Bold)
        val chamados=repo.records("chamados").filter{it.campos["email"].equals(s.email,true)}
        if(chamados.isEmpty())Text("Você ainda não abriu chamados.",Modifier.padding(vertical=14.dp))
        chamados.forEach{r->Card(Modifier.fillMaxWidth().padding(vertical=5.dp)){Column(Modifier.padding(14.dp)){Text(r.campos["origem"]?:"Chamado",fontWeight=FontWeight.Bold);Text(r.campos["problema"]?:"");Text(r.campos["email"]?:"",style=MaterialTheme.typography.bodySmall)}}}
        Card(Modifier.fillMaxWidth().padding(top=14.dp)){Text("Acesso restrito: clientes não acessam o Portal do gestor nem o ERP.",Modifier.padding(14.dp),style=MaterialTheme.typography.bodySmall)}
    }
}

@Composable
private fun PortalScreen(nav:NavHostController,s:Sessao,logout:()->Unit){
    Page(nav,"Painel Central",s,logout){
        Text("Plataforma Integrada de Gestão de Ativos",style=MaterialTheme.typography.headlineMedium,fontWeight=FontWeight.Bold)
        Text("Dashboard intermediário para manutenção, estoque, métricas de OEE e auditoria.")
        Spacer(Modifier.height(16.dp))
        Button(onClick={nav.navigate(Routes.ERP_HOME)},modifier=Modifier.fillMaxWidth()){Icon(Icons.Default.Apps,null);Spacer(Modifier.width(8.dp));Text("Entrar no Sistema")}
        OutlinedButton(onClick={nav.navigate(Routes.CONFIG)},modifier=Modifier.fillMaxWidth()){Text("Configurações da conta")}
        Spacer(Modifier.height(18.dp));Text("Módulos Estruturais do Sistema",style=MaterialTheme.typography.titleLarge,fontWeight=FontWeight.Bold)
        val modules=listOf("Manutenção","Estoque","Máquinas","RH & Ponto","Pedidos","Qualidade","Suprimentos","Produção","Logs","Sistema")
        modules.forEach{FeatureCard(it)}
        Spacer(Modifier.height(14.dp));Text("Impacto Operacional Global",style=MaterialTheme.typography.titleLarge,fontWeight=FontWeight.Bold)
        Row(horizontalArrangement=Arrangement.spacedBy(8.dp),modifier=Modifier.fillMaxWidth()){
            StatCard("Ativos","4",Modifier.weight(1f));StatCard("Ordens","4",Modifier.weight(1f));StatCard("Qualidade","5",Modifier.weight(1f))
        }
    }
}
@Composable private fun StatCard(label:String,value:String,modifier:Modifier=Modifier){Card(modifier){Column(Modifier.padding(12.dp)){Text(value,style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.Bold,color=MaterialTheme.colorScheme.primary);Text(label,style=MaterialTheme.typography.bodySmall)}}}

private data class ModuleLink(val title:String,val route:String,val icon:ImageVector)
@Composable
private fun ErpScreen(nav:NavHostController,s:Sessao,logout:()->Unit){
    val links=listOf(
        ModuleLink("Visão Geral",Routes.ERP_HOME,Icons.Default.Dashboard),
        ModuleLink("Geral",Routes.ERP_HOME,Icons.Default.Info),
        ModuleLink("Estoque",Routes.ESTOQUE,Icons.Default.Inventory),
        ModuleLink("Manutenção",Routes.MANUT,Icons.Default.Build),
        ModuleLink("Máquinas",Routes.MAQUINAS,Icons.Default.PrecisionManufacturing),
        ModuleLink("RH & Ponto",Routes.RH,Icons.Default.Badge),
        ModuleLink("Pedidos",Routes.PEDIDOS,Icons.Default.ShoppingCart),
        ModuleLink("Qualidade",Routes.QUALIDADE,Icons.Default.Verified),
        ModuleLink("Suprimentos",Routes.SUPRIMENTOS,Icons.Default.LocalShipping),
        ModuleLink("Produção",Routes.PRODUCAO,Icons.Default.Factory),
        ModuleLink("Logs",Routes.LOGS,Icons.Default.History),
        ModuleLink("Sistema",Routes.SISTEMA,Icons.Default.SettingsApplications),
        ModuleLink("Configurações",Routes.ERP_CONFIG,Icons.Default.Settings)
    )
    Page(nav,"ERP Industrial",s,logout){
        Text("Módulos do sistema",style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.Bold)
        Spacer(Modifier.height(10.dp))
        links.forEach{m->Card(Modifier.fillMaxWidth().padding(vertical=4.dp).clickable{nav.navigate(m.route)}){Row(Modifier.padding(14.dp),verticalAlignment=Alignment.CenterVertically){Icon(m.icon,null,tint=MaterialTheme.colorScheme.primary);Spacer(Modifier.width(12.dp));Column{Text(m.title,fontWeight=FontWeight.Bold);Text("Abrir módulo",style=MaterialTheme.typography.bodySmall)}}}}
    }
}

@Composable
private fun ModuleScreen(repo:GeTechRepository,nav:NavHostController,key:String,title:String,description:String){
    var refresh by remember{mutableIntStateOf(0)};var query by remember{mutableStateOf("")};var showAdd by remember{mutableStateOf(false)}
    val data=remember(refresh){repo.records(key)}
    Page(nav,title){
        Text(title,style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.Bold)
        Text(description)
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement=Arrangement.spacedBy(8.dp),verticalAlignment=Alignment.CenterVertically){
            OutlinedTextField(query,{query=it},modifier=Modifier.weight(1f),placeholder={Text("Pesquisar...")},singleLine=true,leadingIcon={Icon(Icons.Default.Search,null)})
            FilledTonalIconButton(onClick={showAdd=true}){Icon(Icons.Default.Add,"Adicionar")}
        }
        Spacer(Modifier.height(10.dp))
        val filtered=data.filter{query.isBlank()||it.campos.values.any{v->v.contains(query,true)}}
        if(filtered.isEmpty())Text("Nenhum registro encontrado.",Modifier.padding(20.dp))
        filtered.forEach{r->RecordCard(r,onDelete={repo.remove(key,r.id);refresh++})}
        if(showAdd){AddRecordDialog(key,title,repo,{showAdd=false;refresh++})}
    }
}

@Composable
private fun RecordCard(r:Registro,onDelete:()->Unit){
    Card(Modifier.fillMaxWidth().padding(vertical=5.dp)){Column(Modifier.padding(14.dp)){
        r.campos.entries.take(6).forEachIndexed{index,(k,v)->Row(Modifier.fillMaxWidth().padding(vertical=2.dp)){Text(if(index==0)k.replaceFirstChar{it.uppercase()} else "$k: ",fontWeight=if(index==0)FontWeight.Bold else FontWeight.Normal,modifier=Modifier.widthIn(max=120.dp));Text(v,Modifier.weight(1f))}}
        Spacer(Modifier.height(5.dp));TextButton(onClick=onDelete){Text("Excluir")}
    }}
}

@Composable
private fun AddRecordDialog(key:String,title:String,repo:GeTechRepository,onDone:()->Unit){
    var text by remember{mutableStateOf("")}
    AlertDialog(onDismissRequest=onDone,title={Text("Novo registro")},text={Column{Text("Digite os dados principais separados por | para manter a entrada local.");Spacer(Modifier.height(8.dp));OutlinedTextField(text,{text=it},modifier=Modifier.fillMaxWidth(),placeholder={Text("nome | código | quantidade")})}},confirmButton={Button(onClick={if(text.isNotBlank()){repo.add(key,mapOf("registro" to text));onDone()}}){Text("Cadastrar")}},dismissButton={TextButton(onClick=onDone){Text("Cancelar")}})
}

@Composable
private fun OrdersScreen(repo:GeTechRepository,nav:NavHostController){
    var tab by remember{mutableIntStateOf(0)}
    Page(nav,"Pedidos"){
        Text("Pedidos",style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.Bold)
        Text("Kanban, recebimento, rastreio e frete das ordens de produção.")
        Spacer(Modifier.height(10.dp))
        TabRow(selectedTabIndex=tab){listOf("Kanban","Recebimento","Rastreio","Frete","Tabela").forEachIndexed{i,t->Tab(selected=tab==i,onClick={tab=i},text={Text(t)})}}
        Spacer(Modifier.height(12.dp))
        when(tab){
            0->listOf("A Fazer","Em Andamento","Qualidade","Finalizado").forEach{status->SectionCard(status,repo.records("pedidos").filter{it.campos["status"]==status})}
            1->Text("Área de recebimento preparada para registros locais.")
            2->Text("Área de rastreio preparada para acompanhamento das ordens.")
            3->Text("Cálculo de Frete: dados de frete podem ser integrados posteriormente.")
            4->repo.records("pedidos").forEach{RecordCard(it,{repo.remove("pedidos",it.id)})}
        }
    }
}
@Composable private fun SectionCard(title:String,items:List<Registro>){Card(Modifier.fillMaxWidth().padding(vertical=5.dp)){Column(Modifier.padding(12.dp)){Text(title,fontWeight=FontWeight.Bold,color=MaterialTheme.colorScheme.primary);items.take(5).forEach{Text("${it.campos["op"]}: ${it.campos["produto"]}",Modifier.padding(vertical=3.dp))}}}}

@Composable
private fun LogsScreen(repo:GeTechRepository,nav:NavHostController){
    Page(nav,"Logs de Operação"){
        Text("Logs e auditoria",style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.Bold)
        Spacer(Modifier.height(10.dp))
        repo.logs().forEach{line->val p=line.split("|",limit=3);Card(Modifier.fillMaxWidth().padding(vertical=4.dp)){Column(Modifier.padding(12.dp)){Text(p.getOrNull(0)?:"",fontWeight=FontWeight.Bold);Text(p.getOrNull(1)?:"");Text(p.getOrNull(2)?:"INFO",style=MaterialTheme.typography.labelSmall)}}}
    }
}

@Composable
private fun SistemaScreen(repo:GeTechRepository,nav:NavHostController){
    var tab by remember{mutableIntStateOf(0)}
    Page(nav,"Sistema de Inventário Unificado"){
        Text("Sistema de Inventário Unificado da GeTech",style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.Bold)
        Spacer(Modifier.height(8.dp));TabRow(selectedTabIndex=tab){listOf("Status Ativos","Ordens de Serviço","Cadastros","Histórico Global").forEachIndexed{i,t->Tab(tab==i,{tab=i},text={Text(t)})}}
        Spacer(Modifier.height(12.dp))
        when(tab){
            0->repo.records("siu-ativos").forEach{RecordCard(it,{})}
            1->Card(Modifier.fillMaxWidth()){Column(Modifier.padding(14.dp)){Text("Abertura de Ordem de Serviço (O.S.)",fontWeight=FontWeight.Bold);Text("Registre e ative uma O.S. localmente quando necessário.")}}
            2->{Text("Técnicos");repo.records("siu-tecnicos").forEach{RecordCard(it,{})}}
            3->repo.records("siu-historico").forEach{RecordCard(it,{})}
        }
    }
}

@Composable
private fun ErpConfigScreen(repo:GeTechRepository,nav:NavHostController,dark:Boolean,onDark:(Boolean)->Unit){
    Page(nav,"Configurações do ERP"){
        Text("Configurações",style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.Bold)
        Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically){Text("Tema escuro",Modifier.weight(1f));Switch(dark,onDark)}
        Spacer(Modifier.height(10.dp));Text("Dados locais",style=MaterialTheme.typography.titleMedium,fontWeight=FontWeight.Bold)
        Text("O ERP opera inicialmente com persistência local. A arquitetura separa UI, repositório e fonte de dados para permitir backend futuro.")
        Spacer(Modifier.height(12.dp));Button(onClick={repo.log("CONFIG","Configurações do ERP acessadas")}){Text("Registrar acesso nos logs")}
    }
}

@Composable
private fun AccountSettingsScreen(repo:GeTechRepository,nav:NavHostController,dark:Boolean,onDark:(Boolean)->Unit){
    Page(nav,"Configurações da Conta"){
        Text("👤 Perfil do Usuário",style=MaterialTheme.typography.titleLarge,fontWeight=FontWeight.Bold)
        val s=repo.session();Text(s?.nome?:"Visitante");Text(s?.email?:"Sem sessão",style=MaterialTheme.typography.bodySmall)
        Spacer(Modifier.height(18.dp));Text("🔒 Segurança e Senha",style=MaterialTheme.typography.titleLarge,fontWeight=FontWeight.Bold)
        Text("A redefinição local de senha está disponível na tela de recuperação.")
        Spacer(Modifier.height(18.dp));Text("❓ Suporte e Ajuda",style=MaterialTheme.typography.titleLarge,fontWeight=FontWeight.Bold)
        QuickButton("Central de Ajuda"){nav.navigate(Routes.AJUDA)}
        QuickButton("Privacidade e LGPD"){nav.navigate(Routes.PRIVACIDADE)}
        Spacer(Modifier.height(18.dp));Text("Aparência",style=MaterialTheme.typography.titleLarge,fontWeight=FontWeight.Bold)
        Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically){Text("Tema escuro",Modifier.weight(1f));Switch(dark,onDark)}
    }
}

@Composable
private fun TestimonialsScreen(repo:GeTechRepository,nav:NavHostController){
    Page(nav,"Depoimentos de Clientes"){
        Text("Depoimentos de Clientes",style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.Bold)
        Text("Compartilhe sua experiência")
        Spacer(Modifier.height(12.dp))
        repo.records("depoimentos").forEach{RecordCard(it,{})}
        OutlinedButton(onClick={repo.add("depoimentos",mapOf("nome" to "Usuário","depoimento" to "Experiência registrada localmente"))},modifier=Modifier.fillMaxWidth()){Text("Adicionar depoimento")}
    }
}

@Composable
private fun FaqScreen(nav:NavHostController){
    Page(nav,"Perguntas Frequentes"){
        val qs=listOf(
            "Como acesso o sistema?" to "Use uma conta demo ou crie um cadastro local.",
            "O cliente acessa o ERP?" to "Não. O controle de navegação bloqueia perfis de cliente.",
            "O aplicativo usa WebView?" to "Não. A interface é Kotlin + Jetpack Compose.",
            "Os dados ficam onde?" to "Localmente no dispositivo nesta primeira implementação.",
            "Existe backend?" to "Não foi inventado backend. A arquitetura está preparada para uma futura fonte remota."
        )
        qs.forEach{(q,a)->var open by remember{mutableStateOf(false)};Card(Modifier.fillMaxWidth().padding(vertical=4.dp).clickable{open=!open}){Column(Modifier.padding(14.dp)){Text(q,fontWeight=FontWeight.Bold);if(open)Text(a,Modifier.padding(top=8.dp))}}}
    }
}

@Composable
private fun PrivacyScreen(nav:NavHostController){
    Page(nav,"Política de Privacidade e LGPD"){
        Text("Política de Privacidade",style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.Bold)
        Text("Esta versão Android mantém dados de demonstração e sessão no armazenamento local do dispositivo.")
        Spacer(Modifier.height(12.dp));Text("Consentimento",fontWeight=FontWeight.Bold)
        Text("O usuário deve revisar as informações e políticas aplicáveis antes de utilizar dados reais.")
    }
}

@Composable
private fun BlogScreen(repo:GeTechRepository,nav:NavHostController){
    Page(nav,"Comunidade Tech | Blog GeTech"){
        Text("🚀 Feed da comunidade",style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.Bold)
        Text("Conteúdo técnico e atualizações da plataforma.")
        Spacer(Modifier.height(12.dp))
        listOf("Confiabilidade e manutenção preventiva","Indicadores de ativos industriais","Transformação digital no chão de fábrica").forEach{Card(Modifier.fillMaxWidth().padding(vertical=4.dp)){Text(it,Modifier.padding(14.dp),fontWeight=FontWeight.SemiBold)}}
        Spacer(Modifier.height(14.dp));Button(onClick={repo.log("BLOG","Publicação criada localmente")}){Text("Criar nova publicação")}
    }
}

@Composable
private fun MessagesScreen(repo:GeTechRepository,nav:NavHostController){
    Page(nav,"Mensagens"){
        Text("Mensagens",style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.Bold)
        repo.records("chamados").forEach{r->RecordCard(r,{})}
        if(repo.records("chamados").isEmpty())Text("Nenhuma mensagem.")
    }
}

@Composable
private fun GenericCrudScreen(repo:GeTechRepository,nav:NavHostController,key:String,title:String,desc:String){
    ModuleScreen(repo,nav,key,title,desc)
}
