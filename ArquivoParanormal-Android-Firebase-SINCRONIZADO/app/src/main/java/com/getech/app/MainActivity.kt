@file:OptIn(ExperimentalMaterial3Api::class)

package com.getech.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.withStyle
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
    const val LOGS="logs"; const val SISTEMA="sistema"; const val ERP_CONFIG="erp_config"; const val ERP_GERAL="erp_geral"
    const val CONTATO="contato"; const val MATERIAIS="materiais"; const val CHATBOT="chatbot"; const val SOBRE="sobre"
    const val FUNC="funcionalidades"; const val PLANOS="planos"; const val DEPOIMENTOS="depoimentos"; const val FAQ="faq"
    const val AJUDA="ajuda"; const val PRIVACIDADE="privacidade"; const val INTEGRACOES="integracoes"; const val AR="ar"
    const val BLOG="blog"; const val MENSAGENS="mensagens"; const val ORCAMENTOS="orcamentos"; const val CONFIG="config"
}

@Composable
private fun GeTechApp(repo: GeTechRepository) {
    var dark by remember { mutableStateOf(false) }
    var fontScale by remember { mutableFloatStateOf(1f) }
    var altoContraste by remember { mutableStateOf(false) }
    var session by remember { mutableStateOf(repo.session()) }
    val start = if (session?.perfil == Perfil.GESTOR) Routes.PORTAL else if (session?.perfil == Perfil.CLIENTE) Routes.CLIENTE else Routes.HOME
    val nav = rememberNavController()

    fun logout() {
        repo.logout()
        session = null
        while(nav.popBackStack()) { }; nav.navigate(Routes.HOME)
    }

    val controls = AppUiControls(
        dark = dark,
        onToggleDark = { dark = !dark },
        fontScale = fontScale,
        onFontScaleChange = { fontScale = it.coerceIn(0.85f, 1.5f) },
        altoContraste = altoContraste,
        onToggleContraste = { altoContraste = !altoContraste },
        onResetAcessibilidade = { fontScale = 1f; altoContraste = false }
    )

    GeTechTheme(dark, altoContraste) {
        CompositionLocalProvider(
            LocalAppUiControls provides controls,
            LocalDensity provides Density(LocalDensity.current.density, LocalDensity.current.fontScale * fontScale)
        ) {
            Box(Modifier.fillMaxSize()) {
                NavHost(navController = nav, startDestination = start) {
                    composable(Routes.HOME) { HomeScreen(nav) }
                    composable(Routes.LOGIN) {
                        LoginScreen(nav,repo) { s -> session=s; nav.navigate(if(s.perfil==Perfil.GESTOR) Routes.PORTAL else Routes.CLIENTE){popUpTo(Routes.LOGIN){inclusive=true}} }
                    }
                    composable(Routes.CADASTRO) { CadastroScreen(nav,repo) { nav.popBackStack() } }
                    composable(Routes.RECUPERAR) { RecuperarScreen(nav,repo) { nav.popBackStack() } }
                    composable(Routes.CONTATO) { ContatoScreen(repo,nav) }
                    composable(Routes.MATERIAIS) { MaterialsScreen(nav) }
                    composable(Routes.CHATBOT) { ChatbotScreen(repo, nav) }
                    composable(Routes.SOBRE) { SobreScreen(nav) }
                    composable(Routes.FUNC) { FuncionalidadesScreen(nav) }
                    composable(Routes.PLANOS) { PlanosScreen(nav) }
                    composable(Routes.DEPOIMENTOS) { TestimonialsScreen(repo,nav) }
                    composable(Routes.FAQ) { FaqScreen(nav) }
                    composable(Routes.AJUDA) { AjudaScreen(nav) }
                    composable(Routes.PRIVACIDADE) { PrivacyScreen(nav) }
                    composable(Routes.INTEGRACOES) { IntegracoesScreen(nav) }
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
                    composable(Routes.ERP_GERAL) { GuardScreen(session,Perfil.GESTOR,nav){ ErpGeralScreen(nav) } }
                    composable(Routes.ESTOQUE) { GuardScreen(session,Perfil.GESTOR,nav){ EstoqueScreen(repo,nav) } }
                    composable(Routes.MANUT) { GuardScreen(session,Perfil.GESTOR,nav){ ManutencaoScreen(repo,nav) } }
                    composable(Routes.MAQUINAS) { GuardScreen(session,Perfil.GESTOR,nav){ MaquinasScreen(repo,nav) } }
                    composable(Routes.RH) { GuardScreen(session,Perfil.GESTOR,nav){ RhScreen(repo,nav) } }
                    composable(Routes.PEDIDOS) { GuardScreen(session,Perfil.GESTOR,nav){ OrdersScreen(repo,nav) } }
                    composable(Routes.QUALIDADE) { GuardScreen(session,Perfil.GESTOR,nav){ QualidadeScreen(repo,nav) } }
                    composable(Routes.SUPRIMENTOS) { GuardScreen(session,Perfil.GESTOR,nav){ SuprimentosScreen(repo,nav) } }
                    composable(Routes.PRODUCAO) { GuardScreen(session,Perfil.GESTOR,nav){ ProducaoScreen(repo,nav) } }
                    composable(Routes.LOGS) { GuardScreen(session,Perfil.GESTOR,nav){ LogsScreen(repo,nav) } }
                    composable(Routes.SISTEMA) { GuardScreen(session,Perfil.GESTOR,nav){ SistemaScreen(repo,nav) } }
                    composable(Routes.ERP_CONFIG) { GuardScreen(session,Perfil.GESTOR,nav){ ErpConfigScreen(repo,nav,dark){dark=it} } }
                }
                AcessibilidadeButton(Modifier.align(Alignment.BottomStart))
            }
        }
    }
}

/**
 * Botão flutuante de acessibilidade, fixo em todas as telas (canto inferior
 * esquerdo), equivalente ao Acessibilidade.tsx do protótipo Lovable:
 * tamanho de fonte (+/-), alto contraste e restaurar padrão.
 * O VLibras (widget de Libras do governo, carregado via script no site) não
 * tem equivalente nativo direto e não foi replicado nesta versão Android.
 */
@Composable
private fun AcessibilidadeButton(modifier: Modifier = Modifier) {
    val controls = LocalAppUiControls.current
    var aberto by remember { mutableStateOf(false) }
    Column(modifier.padding(16.dp), horizontalAlignment = Alignment.Start) {
        if (aberto) {
            Card(Modifier.width(220.dp), elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)) {
                Column(Modifier.padding(12.dp)) {
                    Text("Acessibilidade", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    Text("Tamanho do texto: ${(controls.fontScale * 100).toInt()}%", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedIconButton(onClick = { controls.onFontScaleChange(controls.fontScale - 0.1f) }) { Icon(Icons.Default.Remove, "Diminuir texto") }
                        OutlinedIconButton(onClick = { controls.onFontScaleChange(controls.fontScale + 0.1f) }) { Icon(Icons.Default.Add, "Aumentar texto") }
                        FilledIconToggleButton(checked = controls.altoContraste, onCheckedChange = { controls.onToggleContraste() }) { Icon(Icons.Default.Contrast, "Alternar alto contraste") }
                    }
                    Spacer(Modifier.height(6.dp))
                    TextButton(onClick = { controls.onResetAcessibilidade() }, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Default.RestartAlt, null, Modifier.size(16.dp)); Spacer(Modifier.width(6.dp)); Text("Restaurar padrão", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }
        FloatingActionButton(onClick = { aberto = !aberto }) { Icon(Icons.Default.Accessibility, "Opções de acessibilidade") }
    }
}

/** Ícone sol/lua para alternar o tema, disponível em toda tela (equivalente ao toggle do AppHeader.tsx). */
@Composable
private fun ThemeToggleIcon(tint: Color) {
    val controls = LocalAppUiControls.current
    IconButton(onClick = { controls.onToggleDark() }) {
        Icon(if (controls.dark) Icons.Default.LightMode else Icons.Default.DarkMode, "Alternar tema", tint = tint)
    }
}

@Composable
private fun AppBar(nav: NavHostController, title:String, session:Sessao?=null, onLogout:(()->Unit)?=null) {
    // Header sticky com cor própria do design system Lovable (--header), distinta
    // do "primary" no modo escuro, com sombra shadow-card.
    val header = MaterialTheme.geTechColors.header
    val headerFg = MaterialTheme.geTechColors.headerForeground
    Surface(shadowElevation = 4.dp, color = header) {
        TopAppBar(
            title={Text(title,fontWeight=FontWeight.Bold,color=headerFg)},
            navigationIcon={ IconButton(onClick={ if(!nav.popBackStack()) nav.navigate(Routes.HOME) }) { Icon(Icons.Default.ArrowBack,"Voltar",tint=headerFg) } },
            actions={
                ThemeToggleIcon(tint = headerFg)
                if(session!=null) IconButton(onClick={onLogout?.invoke()}){Icon(Icons.Default.Logout,"Sair",tint=MaterialTheme.colorScheme.error)}
            },
            colors=TopAppBarDefaults.topAppBarColors(
                containerColor=Color.Transparent,
                titleContentColor=headerFg,
                navigationIconContentColor=headerFg,
                actionIconContentColor=headerFg
            )
        )
    }
}

@Composable private fun AppFooter(){
    // Rodapé: bg-header, texto centralizado, igual ao Footer.tsx do protótipo Lovable
    Box(Modifier.fillMaxWidth().background(MaterialTheme.geTechColors.header).padding(vertical=20.dp),contentAlignment=Alignment.Center){
        Text("© 2026 GeTech Soluções Industriais",color=MaterialTheme.geTechColors.headerForeground,style=MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun Page(nav:NavHostController,title:String,session:Sessao?=null,onLogout:(()->Unit)?=null,content:@Composable ColumnScope.()->Unit){
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar={AppBar(nav,title,session,onLogout)},
        bottomBar={AppFooter()}
    ){pad->
        Column(Modifier.fillMaxSize().padding(pad).verticalScroll(rememberScrollState()).padding(horizontal=18.dp,vertical=22.dp),content=content)
    }
}

/** Card padrão do design system Lovable: raio de 8dp e shadow-card (elevação 3dp). */
@Composable
private fun GeCard(modifier:Modifier=Modifier,content:@Composable ColumnScope.()->Unit){
    Card(
        modifier=modifier,
        colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface),
        elevation=CardDefaults.cardElevation(defaultElevation=3.dp),
        content=content
    )
}

@Composable
private fun HomeScreen(nav:NavHostController){
    val header = MaterialTheme.geTechColors.header
    val headerFg = MaterialTheme.geTechColors.headerForeground
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar={
            Surface(shadowElevation = 4.dp, color = header) {
                TopAppBar(title={Row(verticalAlignment=Alignment.CenterVertically){
                    Image(painterResource(com.getech.app.R.drawable.getech_logo),null,Modifier.size(38.dp).clip(androidx.compose.foundation.shape.CircleShape))
                    Spacer(Modifier.width(8.dp))
                    Text(buildAnnotatedString{withStyle(SpanStyle(fontWeight=FontWeight.Bold)){append("GE")};append("TECH")},color=headerFg)
                }},actions={
                    ThemeToggleIcon(tint = headerFg)
                    IconButton(onClick={nav.navigate(Routes.LOGIN)}){Icon(Icons.Default.Person,"Login",tint=headerFg)}
                },
                colors=TopAppBarDefaults.topAppBarColors(
                    containerColor=Color.Transparent,
                    titleContentColor=headerFg,
                    navigationIconContentColor=headerFg,
                    actionIconContentColor=headerFg
                ))
            }
        },
        bottomBar={AppFooter()}
    ){p->
        Column(Modifier.fillMaxSize().padding(p).verticalScroll(rememberScrollState())){
            // Hero: gradient-hero do Lovable (escuro translúcido -> primary), mesmo tom em ambos os temas
            Box(Modifier.fillMaxWidth().height(290.dp).background(Brush.verticalGradient(listOf(Color(0xFF232626).copy(alpha=.78f), MaterialTheme.colorScheme.primary))).padding(24.dp)){
                Column(Modifier.align(Alignment.Center),horizontalAlignment=Alignment.CenterHorizontally){
                    Text("Manutenção de Máquinas Pesadas",style=MaterialTheme.typography.headlineMedium,fontWeight=FontWeight.Bold,color=Color.White,textAlign=androidx.compose.ui.text.style.TextAlign.Center)
                    Spacer(Modifier.height(12.dp))
                    Text("Especialistas em diagnóstico, reparo e prevenção industrial.",color=Color.White.copy(.88f),textAlign=androidx.compose.ui.text.style.TextAlign.Center)
                    Spacer(Modifier.height(20.dp))
                    Button(onClick={nav.navigate(Routes.CHATBOT)}){Icon(Icons.Default.Chat,null);Spacer(Modifier.width(8.dp));Text("Conversar com nosso chatbot")}
                }
            }
            Column(Modifier.padding(18.dp)){
                Card(Modifier.fillMaxWidth()){
                    Column(Modifier.padding(18.dp)){
                        Text("Sobre a GeTech",style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.Bold)
                        Spacer(Modifier.height(14.dp))
                        Row(horizontalArrangement=Arrangement.spacedBy(12.dp)){
                            MutedFeatureBox("Atuação","Setores automotivo, alimentício e metalúrgico com tecnologia de ponta.",Modifier.weight(1f))
                            MutedFeatureBox("Preventiva","Redução de custos emergenciais e aumento da vida útil dos equipamentos.",Modifier.weight(1f))
                        }
                    }
                }
                Spacer(Modifier.height(28.dp))
                Text("Nossas Especialidades",style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.Bold,modifier=Modifier.fillMaxWidth(),textAlign=androidx.compose.ui.text.style.TextAlign.Center)
                Spacer(Modifier.height(14.dp))
                listOf(
                    "Hidráulica" to "Reparo em cilindros, bombas e válvulas de alta pressão.",
                    "Elétrica Industrial" to "Manutenção em painéis, inversores de frequência e motores.",
                    "Mecânica Geral" to "Ajuste de rolamentos, engrenagens e eixos rotativos."
                ).forEach{(t,d)->SpecialtyCard(t,d)}
                Spacer(Modifier.height(28.dp))
                Text("Assistente Virtual GeTech",style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.Bold,modifier=Modifier.fillMaxWidth(),textAlign=androidx.compose.ui.text.style.TextAlign.Center)
                Spacer(Modifier.height(10.dp))
                Card(Modifier.fillMaxWidth()){
                    Column(Modifier.padding(16.dp)){
                        Text("Converse com o assistente e registre um chamado quando a funcionalidade estiver disponível localmente.")
                        Spacer(Modifier.height(10.dp))
                        Button(onClick={nav.navigate(Routes.CHATBOT)},modifier=Modifier.fillMaxWidth()){Icon(Icons.Default.Chat,null);Spacer(Modifier.width(8.dp));Text("Abrir chatbot")}
                    }
                }
                Spacer(Modifier.height(28.dp))
                HorizontalDivider()
                Spacer(Modifier.height(18.dp))
                Text("Acesso rápido",style=MaterialTheme.typography.titleMedium,fontWeight=FontWeight.Bold,color=MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(8.dp))
                QuickButton("Materiais e Manuais"){nav.navigate(Routes.MATERIAIS)}
                QuickButton("Contato"){nav.navigate(Routes.CONTATO)}
                QuickButton("Sobre a Plataforma"){nav.navigate(Routes.SOBRE)}
                QuickButton("Entrar no sistema"){nav.navigate(Routes.LOGIN)}
            }
        }
    }
}

@Composable private fun MutedFeatureBox(title:String,text:String,modifier:Modifier=Modifier){
    Box(modifier.background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha=.5f),RoundedCornerShape(8.dp)).padding(14.dp)){
        Column{
            Text(title,fontWeight=FontWeight.Bold,color=MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(4.dp))
            Text(text,style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable private fun SpecialtyCard(title:String,text:String){
    Card(Modifier.fillMaxWidth().padding(vertical=4.dp)){
        Box(Modifier.fillMaxWidth().height(4.dp).background(MaterialTheme.colorScheme.tertiary))
        Column(Modifier.padding(14.dp)){
            Text(title,fontWeight=FontWeight.Bold,style=MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text(text,style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable private fun FeatureCard(title:String){Card(Modifier.fillMaxWidth().padding(vertical=4.dp)){Row(Modifier.padding(14.dp),verticalAlignment=Alignment.CenterVertically){Icon(Icons.Default.CheckCircle,null,tint=MaterialTheme.colorScheme.primary);Spacer(Modifier.width(10.dp));Text(title,fontWeight=FontWeight.SemiBold)}}}
@Composable private fun QuickButton(text:String,onClick:()->Unit){OutlinedButton(onClick=onClick,modifier=Modifier.fillMaxWidth().padding(vertical=4.dp)){Text(text)}}


/** Campo de formulário no padrão Lovable: rótulo em cima (Label), h-9, borda border-input. */
@Composable private fun FormField(label:String,value:String,onChange:(String)->Unit,isPassword:Boolean=false,keyboard:androidx.compose.ui.text.input.KeyboardType=androidx.compose.ui.text.input.KeyboardType.Text){
    Column(Modifier.fillMaxWidth()){
        Text(label,style=MaterialTheme.typography.labelMedium,fontWeight=FontWeight.SemiBold,color=MaterialTheme.colorScheme.onSurface)
        Spacer(Modifier.height(4.dp))
        var visible by remember{mutableStateOf(false)}
        OutlinedTextField(
            value=value,onValueChange=onChange,modifier=Modifier.fillMaxWidth(),singleLine=true,
            visualTransformation = if(isPassword && !visible) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
            trailingIcon = if(isPassword) {{
                IconButton(onClick={visible=!visible}){Icon(if(visible) Icons.Default.VisibilityOff else Icons.Default.Visibility,null)}
            }} else null,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = if(isPassword) androidx.compose.ui.text.input.KeyboardType.Password else keyboard),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedBorderColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

/** Card de formulário: max-w-md, rounded-lg, border-border, bg-card, shadow-card — igual ao Lovable. */
@Composable private fun AuthCard(content:@Composable ColumnScope.()->Unit){
    Box(Modifier.fillMaxWidth(),contentAlignment=Alignment.TopCenter){
        Card(
            modifier=Modifier.widthIn(max=420.dp).fillMaxWidth(),
            border=BorderStroke(1.dp,MaterialTheme.colorScheme.outline),
            colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface),
            elevation=CardDefaults.cardElevation(defaultElevation=3.dp)
        ){ Column(Modifier.padding(24.dp),verticalArrangement=Arrangement.spacedBy(16.dp),content=content) }
    }
}

@Composable private fun InlineToast(msg:String,isError:Boolean){
    if(msg.isBlank()) return
    Text(msg,color=if(isError) MaterialTheme.colorScheme.error else MaterialTheme.geTechColors.success,style=MaterialTheme.typography.bodySmall,fontWeight=FontWeight.SemiBold)
}

@Composable
private fun LoginScreen(nav:NavHostController,repo:GeTechRepository,onSuccess:(Sessao)->Unit){
    var email by remember{mutableStateOf("")}
    var senha by remember{mutableStateOf("")}
    var erro by remember{mutableStateOf("")}
    var enviando by remember{mutableStateOf(false)}
    Page(nav,"Entrar") {
        Text("Entrar",style=MaterialTheme.typography.headlineMedium,fontWeight=FontWeight.Bold)
        Text("Use o e-mail e a senha cadastrados.",color=MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(18.dp))
        AuthCard{
            FormField("E-mail",email,{email=it},keyboard=androidx.compose.ui.text.input.KeyboardType.Email)
            FormField("Senha",senha,{senha=it},isPassword=true)
            InlineToast(erro,true)
            Button(onClick={
                enviando=true
                repo.login(email,senha)?.let{onSuccess(it)} ?: run{erro="E-mail ou senha incorretos.";enviando=false}
            },modifier=Modifier.fillMaxWidth()){Text(if(enviando) "Entrando..." else "Entrar")}
            Text(buildAnnotatedString{append("Não tem conta? ");withStyle(SpanStyle(color=MaterialTheme.colorScheme.primary,fontWeight=FontWeight.Bold)){append("Cadastre-se")}},
                modifier=Modifier.fillMaxWidth().clickable{nav.navigate(Routes.CADASTRO)},textAlign=androidx.compose.ui.text.style.TextAlign.Center,style=MaterialTheme.typography.bodySmall)
            Text("Esqueci minha senha",color=MaterialTheme.colorScheme.primary,fontWeight=FontWeight.Bold,
                modifier=Modifier.fillMaxWidth().clickable{nav.navigate(Routes.RECUPERAR)},textAlign=androidx.compose.ui.text.style.TextAlign.Center,style=MaterialTheme.typography.bodySmall)
            Column(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha=.4f),RoundedCornerShape(6.dp)).padding(12.dp)){
                Text("Contas de demonstração",fontWeight=FontWeight.Bold,color=MaterialTheme.colorScheme.onSurface,style=MaterialTheme.typography.labelSmall)
                Text("Gestor — gestor@getech.com / getech123",Modifier.clickable{email="gestor@getech.com";senha="getech123"}.padding(top=4.dp),style=MaterialTheme.typography.labelSmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Cliente — cliente@getech.com / getech123",Modifier.clickable{email="cliente@getech.com";senha="getech123"}.padding(top=2.dp),style=MaterialTheme.typography.labelSmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun CadastroScreen(nav:NavHostController,repo:GeTechRepository,onDone:()->Unit){
    var nome by remember{mutableStateOf("")};var email by remember{mutableStateOf("")};var senha by remember{mutableStateOf("")}
    var perfil by remember{mutableStateOf(Perfil.CLIENTE)};var msg by remember{mutableStateOf("")};var erro by remember{mutableStateOf(false)}
    Page(nav,"Cadastro"){
        Text("Criar conta",style=MaterialTheme.typography.headlineMedium,fontWeight=FontWeight.Bold)
        Text("Perfis disponíveis: cliente e gestor.",color=MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(18.dp))
        AuthCard{
            FormField("Nome completo",nome,{nome=it})
            FormField("E-mail",email,{email=it},keyboard=androidx.compose.ui.text.input.KeyboardType.Email)
            FormField("Senha",senha,{senha=it},isPassword=true)
            Column{
                Text("Perfil",style=MaterialTheme.typography.labelMedium,fontWeight=FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment=Alignment.CenterVertically){
                    RadioButton(perfil==Perfil.CLIENTE,{perfil=Perfil.CLIENTE});Text("Cliente",Modifier.clickable{perfil=Perfil.CLIENTE})
                    Spacer(Modifier.width(16.dp))
                    RadioButton(perfil==Perfil.GESTOR,{perfil=Perfil.GESTOR});Text("Gestor",Modifier.clickable{perfil=Perfil.GESTOR})
                }
            }
            InlineToast(msg,erro)
            Button(onClick={
                if(nome.isBlank()||email.isBlank()||senha.isBlank()){msg="Preencha todos os campos.";erro=true}
                else if(repo.register(nome,email,senha,perfil)){msg="Cadastro concluído! Faça login para continuar.";erro=false;onDone()}
                else {msg="Já existe uma conta com este e-mail.";erro=true}
            },modifier=Modifier.fillMaxWidth()){Text("Cadastrar")}
            Text(buildAnnotatedString{append("Já tem conta? ");withStyle(SpanStyle(color=MaterialTheme.colorScheme.primary,fontWeight=FontWeight.Bold)){append("Entrar")}},
                modifier=Modifier.fillMaxWidth().clickable{nav.navigate(Routes.LOGIN)},textAlign=androidx.compose.ui.text.style.TextAlign.Center,style=MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun RecuperarScreen(nav:NavHostController,repo:GeTechRepository,onDone:()->Unit){
    var email by remember{mutableStateOf("")};var senha by remember{mutableStateOf("")};var confirmacao by remember{mutableStateOf("")};var msg by remember{mutableStateOf("")};var erro by remember{mutableStateOf(false)}
    Page(nav,"Recuperar senha"){
        Text("Recuperar senha",style=MaterialTheme.typography.headlineMedium,fontWeight=FontWeight.Bold)
        Text("Esta versão opera localmente: confirme o e-mail cadastrado e defina uma nova senha.",color=MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(18.dp))
        AuthCard{
            FormField("E-mail cadastrado",email,{email=it},keyboard=androidx.compose.ui.text.input.KeyboardType.Email)
            FormField("Nova senha",senha,{senha=it},isPassword=true)
            FormField("Confirmar nova senha",confirmacao,{confirmacao=it},isPassword=true)
            InlineToast(msg,erro)
            Button(onClick={
                when{
                    senha.length<4 -> {msg="A nova senha precisa ter pelo menos 4 caracteres.";erro=true}
                    senha!=confirmacao -> {msg="As senhas não coincidem.";erro=true}
                    !repo.resetPassword(email,senha) -> {msg="Nenhuma conta encontrada com este e-mail.";erro=true}
                    else -> {msg="Senha redefinida. Faça login com a nova senha.";erro=false;onDone()}
                }
            },modifier=Modifier.fillMaxWidth()){Text("Redefinir senha")}
            Text("Voltar para o login",color=MaterialTheme.colorScheme.primary,fontWeight=FontWeight.Bold,
                modifier=Modifier.fillMaxWidth().clickable{nav.navigate(Routes.LOGIN)},textAlign=androidx.compose.ui.text.style.TextAlign.Center,style=MaterialTheme.typography.bodySmall)
        }
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
private fun SobreScreen(nav:NavHostController){
    Page(nav,"Sobre a Plataforma"){
        Text("Sobre a Plataforma",style=MaterialTheme.typography.headlineMedium,fontWeight=FontWeight.Bold)
        Text("A tecnologia por trás do controle de ativos industriais.",color=MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(16.dp))
        Box(Modifier.fillMaxWidth().background(Brush.linearGradient(listOf(MaterialTheme.geTechColors.sidebar,MaterialTheme.geTechColors.panelGradientEnd)),RoundedCornerShape(8.dp)).padding(20.dp)){
            Text("O ERP Industrial da GeTech é uma plataforma de software especializada na gestão, monitoramento e digitalização do ecossistema de manutenção industrial. Centralizamos dados operacionais complexos em uma interface intuitiva, do agendamento automatizado de manutenções preventivas e corretivas ao rastreamento em tempo real do histórico digital de cada máquina. Com módulos inteligentes e análise de dados, otimizamos processos e eliminamos gargalos de comunicação, maximizando a produtividade das plantas fabris.",color=Color.White.copy(alpha=.92f),style=MaterialTheme.typography.bodySmall)
        }
        Spacer(Modifier.height(24.dp))
        Text("Objetivos Tecnológicos e Governança do ERP",style=MaterialTheme.typography.titleLarge,fontWeight=FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        listOf(
            "Missão do Software" to "Garantir a integridade, a segurança e a alta disponibilidade dos dados críticos dos equipamentos industriais de nossos clientes, oferecendo uma infraestrutura digital confiável para ordens de serviço, relatórios de auditoria e tomadas de decisão rápidas.",
            "Visão de Futuro" to "Consolidar-se como o ERP líder de mercado no segmento de manutenção de ativos, integrando soluções de ponta como análise preditiva e inteligência de dados aplicada para eliminar completamente o tempo de máquina parada involuntário na indústria.",
            "Valores Digitais" to "Transparência total na governança de dados, segurança da informação intransigente, arquitetura de software escalável, inovação contínua na experiência do usuário e foco absoluto na produtividade operacional."
        ).forEach{(t,d)-> GeCard(Modifier.fillMaxWidth().padding(vertical=5.dp)){ Column(Modifier.padding(16.dp)){ Text(t,fontWeight=FontWeight.Bold); Spacer(Modifier.height(6.dp)); Text(d,style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant) } } }
    }
}

@Composable
private fun FuncionalidadesScreen(nav:NavHostController){
    Page(nav,"Nossas Soluções"){
        Text("Nossas Soluções",style=MaterialTheme.typography.headlineMedium,fontWeight=FontWeight.Bold)
        Text("Inovação e performance para o seu ecossistema digital.",color=MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(16.dp))
        val solucoes = listOf(
            Triple("☁️","Cloud Computing","Escalabilidade e armazenamento seguro na nuvem.") to "Nossa infraestrutura em nuvem oferece disponibilidade de 99,9%, garantindo que seus dados estejam sempre acessíveis.",
            Triple("🤖","Inteligência Artificial","Algoritmos avançados para análise e automação de tarefas.") to "Algoritmos avançados para análise de dados e automação de tarefas repetitivas em larga escala.",
            Triple("🛡️","Cibersegurança","Proteção total contra ataques e integridade de dados.") to "Proteção contra ataques DDoS e Ransomware, com garantia de integridade dos dados corporativos.",
            Triple("📱","Apps Mobile","Interfaces intuitivas para iOS e Android.") to "Desenvolvemos interfaces intuitivas e backends robustos para sua aplicação decolar nas lojas.",
            Triple("📊","Big Data","Análise estratégica de grandes volumes de informação.") to "Processamento de dados em tempo real para gerar insights valiosos e cruciais para o seu negócio.",
            Triple("🔗","IoT Solutions","Conectividade entre dispositivos e sensores.") to "Conectamos sua indústria com tecnologia de sensores e monitoramento remoto eficiente."
        )
        solucoes.forEach{(head,detalhe)->
            val(icone,titulo,resumo)=head
            var aberto by remember{mutableStateOf(false)}
            GeCard(Modifier.fillMaxWidth().padding(vertical=5.dp)){
                Column(Modifier.padding(16.dp)){
                    Text(icone,fontSize=androidx.compose.ui.unit.TextUnit(28f,androidx.compose.ui.unit.TextUnitType.Sp))
                    Spacer(Modifier.height(6.dp));Text(titulo,fontWeight=FontWeight.Bold)
                    Spacer(Modifier.height(2.dp));Text(resumo,style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(6.dp))
                    Text(if(aberto)"Ocultar detalhes" else "Ver detalhes",color=MaterialTheme.colorScheme.primary,fontWeight=FontWeight.Bold,style=MaterialTheme.typography.bodySmall,modifier=Modifier.clickable{aberto=!aberto})
                    if(aberto){Spacer(Modifier.height(6.dp));Text(detalhe,style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)}
                }
            }
        }
    }
}

@Composable
private fun PlanosScreen(nav:NavHostController){
    var selecionado by remember{mutableStateOf("")}
    Page(nav,"Planos de Gestão"){
        Text("Planos de Gestão",style=MaterialTheme.typography.headlineMedium,fontWeight=FontWeight.Bold)
        Text("Escolha a inteligência ideal para a vida útil do seu maquinário.",color=MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(16.dp))
        if(selecionado.isNotBlank()){InlineToast("Plano selecionado: $selecionado — nossa equipe comercial entrará em contato.",false);Spacer(Modifier.height(10.dp))}
        data class Plano(val nome:String,val preco:String,val periodo:String,val destaque:Boolean,val acao:String,val itens:List<String>)
        listOf(
            Plano("Essencial","R$ 499","/mês",false,"Começar agora",listOf("Manutenção corretiva agendada","Relatórios mensais em PDF","Suporte em até 24h","Gestão de até 5 máquinas")),
            Plano("Pro Performance","R$ 1.299","/mês",true,"Assinar Pro",listOf("Manutenção preditiva com IoT","Dashboard em tempo real","Suporte prioritário em 4h","Gestão de até 20 máquinas","Análise de vibração inclusa")),
            Plano("Enterprise","Sob consulta","",false,"Falar com consultor",listOf("Gestão de parque industrial ilimitado","Consultoria técnica dedicada","Integração total via API","Treinamento de equipe in loco"))
        ).forEach{p->
            Card(
                Modifier.fillMaxWidth().padding(vertical=6.dp),
                border=BorderStroke(if(p.destaque)2.dp else 1.dp,if(p.destaque)MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline),
                elevation=CardDefaults.cardElevation(defaultElevation=if(p.destaque)6.dp else 3.dp)
            ){
                Column(Modifier.padding(18.dp)){
                    if(p.destaque){Surface(color=MaterialTheme.colorScheme.primary,shape=RoundedCornerShape(50)){Text("Mais popular",Modifier.padding(horizontal=10.dp,vertical=3.dp),color=Color.White,style=MaterialTheme.typography.labelSmall,fontWeight=FontWeight.Bold)};Spacer(Modifier.height(8.dp))}
                    Text(p.nome,style=MaterialTheme.typography.titleLarge,fontWeight=FontWeight.Bold)
                    Row(verticalAlignment=Alignment.Bottom){
                        Text(p.preco,style=MaterialTheme.typography.headlineMedium,fontWeight=FontWeight.Bold,color=MaterialTheme.colorScheme.primary)
                        Text(p.periodo,style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(Modifier.height(10.dp))
                    p.itens.forEach{Text("✓ $it",style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant,modifier=Modifier.padding(vertical=2.dp))}
                    Spacer(Modifier.height(12.dp))
                    if(p.destaque) Button(onClick={selecionado=p.nome},modifier=Modifier.fillMaxWidth()){Text(p.acao)}
                    else OutlinedButton(onClick={selecionado=p.nome},modifier=Modifier.fillMaxWidth()){Text(p.acao)}
                }
            }
        }
    }
}

@Composable
private fun ContatoScreen(repo:GeTechRepository,nav:NavHostController){
    var nome by remember{mutableStateOf("")};var email by remember{mutableStateOf("")};var mensagem by remember{mutableStateOf("")};var enviado by remember{mutableStateOf(false)}
    Page(nav,"Contato"){
        Text("Contato",style=MaterialTheme.typography.headlineMedium,fontWeight=FontWeight.Bold)
        Text("Envie sua solicitação para o suporte técnico.",color=MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(16.dp))
        GeCard(Modifier.fillMaxWidth()){
            Column(Modifier.padding(20.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
                FormField("Nome",nome,{nome=it})
                FormField("E-mail",email,{email=it},keyboard=androidx.compose.ui.text.input.KeyboardType.Email)
                Column{
                    Text("Mensagem",style=MaterialTheme.typography.labelMedium,fontWeight=FontWeight.SemiBold)
                    Spacer(Modifier.height(4.dp))
                    OutlinedTextField(mensagem,{mensagem=it},modifier=Modifier.fillMaxWidth().height(120.dp))
                }
                if(enviado) InlineToast("Mensagem enviada! Retornaremos em breve.",false)
                Button(onClick={
                    repo.add("chamados",mapOf("nome" to nome,"email" to email,"problema" to mensagem,"origem" to "Formulário de contato"))
                    nome="";email="";mensagem="";enviado=true
                },modifier=Modifier.fillMaxWidth()){Text("Enviar mensagem")}
            }
        }
        Spacer(Modifier.height(16.dp))
        GeCard(Modifier.fillMaxWidth()){
            Column(Modifier.padding(20.dp),verticalArrangement=Arrangement.spacedBy(6.dp)){
                Text("GeTech Soluções Industriais",style=MaterialTheme.typography.titleMedium,fontWeight=FontWeight.Bold)
                Text("Atendimento técnico 24/7 para plantas industriais.",style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
                Text("E-mail: contato@getech.com.br",style=MaterialTheme.typography.bodySmall)
                Text("Telefone: (11) 4000-1234",style=MaterialTheme.typography.bodySmall)
                Text("Setores: automotivo, alimentício e metalúrgico.",style=MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun AjudaScreen(nav:NavHostController){
    var busca by remember{mutableStateOf("")}
    data class Cat(val icone:String,val titulo:String,val texto:String,val to:String)
    val categorias = listOf(
        Cat("🔑","Acesso e Conta","Gerencie seu perfil e a segurança da conta.",Routes.CONFIG),
        Cat("💳","Pagamentos","Planos, faturas e formas de contratação.",Routes.PLANOS),
        Cat("📦","Pedidos e Materiais","Status de solicitações e manuais das máquinas.",Routes.MATERIAIS),
        Cat("🎧","Suporte Direto","Fale com nossa equipe técnica industrial.",Routes.CONTATO)
    )
    val filtradas = categorias.filter{"${it.titulo} ${it.texto}".lowercase().contains(busca.lowercase())}
    Page(nav,"Central de Ajuda GeTech"){
        Text("Central de Ajuda GeTech",style=MaterialTheme.typography.headlineMedium,fontWeight=FontWeight.Bold)
        Text("Como podemos facilitar sua experiência hoje?",color=MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(14.dp))
        OutlinedTextField(busca,{busca=it},placeholder={Text("Buscar por assunto...")},modifier=Modifier.fillMaxWidth(),singleLine=true)
        Spacer(Modifier.height(16.dp))
        if(filtradas.isEmpty()) Text("Nenhuma categoria encontrada.",color=MaterialTheme.colorScheme.onSurfaceVariant)
        filtradas.forEach{c->
            GeCard(Modifier.fillMaxWidth().padding(vertical=5.dp).clickable{nav.navigate(c.to)}){
                Column(Modifier.padding(16.dp)){
                    Text(c.icone,fontSize=androidx.compose.ui.unit.TextUnit(28f,androidx.compose.ui.unit.TextUnitType.Sp))
                    Spacer(Modifier.height(6.dp));Text(c.titulo,fontWeight=FontWeight.Bold)
                    Spacer(Modifier.height(2.dp));Text(c.texto,style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun IntegracoesScreen(nav:NavHostController){
    Page(nav,"Nossas Integrações"){
        Text("Nossas Integrações",style=MaterialTheme.typography.headlineMedium,fontWeight=FontWeight.Bold)
        Text("Conectando a GeTech com as melhores soluções de mercado através da integração ERP.",color=MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(16.dp))
        listOf(
            "MQTT / OPC UA" to "Leitura direta de sensores e CLPs do chão de fábrica.",
            "API REST GeTech" to "Endpoints para ordens de serviço, estoque e ativos.",
            "Power BI" to "Exportação de indicadores de OEE e disponibilidade.",
            "ERPs financeiros" to "Conciliação de pedidos de compra e notas de peças.",
            "WhatsApp Business" to "Notificação de ordens críticas para a equipe de campo.",
            "Google Workspace" to "Login corporativo e relatórios enviados por e-mail."
        ).forEach{(t,d)-> GeCard(Modifier.fillMaxWidth().padding(vertical=5.dp)){ Column(Modifier.padding(16.dp)){ Text(t,fontWeight=FontWeight.Bold); Spacer(Modifier.height(4.dp)); Text(d,style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant) } } }
    }
}

@Composable
private fun MaterialsScreen(nav:NavHostController){
    val ctx = androidx.compose.ui.platform.LocalContext.current
    Page(nav,"Manuais das Máquinas"){
        Text("Manuais das Máquinas",style=MaterialTheme.typography.headlineMedium,fontWeight=FontWeight.Bold)
        Text("Documentação técnica disponível para download.",color=MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(16.dp))
        listOf(
            "Máquinas agrícolas" to "https://www.bibliotecaagptea.org.br/agricultura/mecanizacao/livros/APOSTILAS%20DE%20MAQUINAS%20AGRICOLAS%20UNESP.pdf",
            "Prensas Hidráulicas" to "https://www.marcon.ind.br/wp-content/uploads/2024/07/15614-PRENSA-MPH-10-MPH-10S-MPH-15-MPH-15S-MPH-15C-MPH-30.pdf",
            "Máquina de solda transformadora" to "https://www.somar.com.br/wp-content/uploads/2020/06/025.0905-0-Manual-Maquina-de-Solda-Transformador-Somar-MTS-250-Compact-rev2-04.18-Trilingue.pdf",
            "Quinadoras" to "https://www.minag.com.br/downloads/maquina-grande.pdf"
        ).forEach{(nome,url)->
            GeCard(Modifier.fillMaxWidth().padding(vertical=5.dp)){
                Row(Modifier.padding(16.dp).fillMaxWidth(),verticalAlignment=Alignment.CenterVertically){
                    Text(nome,Modifier.weight(1f),fontWeight=FontWeight.Medium)
                    Button(onClick={
                        try{ ctx.startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))) }catch(_:Exception){}
                    }){Icon(Icons.Default.Download,null,Modifier.size(16.dp));Spacer(Modifier.width(6.dp));Text("Baixar Manual")}
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        GeCard(Modifier.fillMaxWidth()){
            Column(Modifier.padding(18.dp)){
                Text("Informações Adicionais",fontWeight=FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                Text("Os documentos estão disponíveis em formato PDF ou DOCX. Para visualizar, certifique-se de ter um leitor de PDF ou software de edição de texto instalado.",style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
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
        Text("Olá, ${s.nome}",style=MaterialTheme.typography.headlineMedium,fontWeight=FontWeight.Bold)
        Text("Área do cliente: acompanhe seus chamados e materiais técnicos.",color=MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement=Arrangement.spacedBy(10.dp)){
            GeCard(Modifier.weight(1f).clickable{nav.navigate(Routes.MATERIAIS)}){Column(Modifier.padding(16.dp)){Text("Materiais e manuais",fontWeight=FontWeight.Bold);Spacer(Modifier.height(4.dp));Text("Baixe a documentação das máquinas.",style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)}}
            GeCard(Modifier.weight(1f).clickable{nav.navigate(Routes.CONTATO)}){Column(Modifier.padding(16.dp)){Text("Abrir novo chamado",fontWeight=FontWeight.Bold);Spacer(Modifier.height(4.dp));Text("Fale com o suporte técnico industrial.",style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)}}
        }
        Spacer(Modifier.height(24.dp));Text("Meus chamados",style=MaterialTheme.typography.titleLarge,fontWeight=FontWeight.Bold)
        val chamados=repo.records("chamados").filter{it.campos["email"].equals(s.email,true)}
        if(chamados.isEmpty())Text("Você ainda não abriu chamados.",Modifier.padding(vertical=10.dp),style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
        chamados.forEach{r->GeCard(Modifier.fillMaxWidth().padding(vertical=5.dp)){Column(Modifier.padding(14.dp)){Text(r.campos["origem"]?:"Chamado",style=MaterialTheme.typography.labelSmall,color=MaterialTheme.colorScheme.onSurfaceVariant);Spacer(Modifier.height(4.dp));Text(r.campos["problema"]?:"",style=MaterialTheme.typography.bodySmall)}}}
        Spacer(Modifier.height(18.dp))
        Box(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha=.5f),RoundedCornerShape(6.dp)).padding(14.dp)){
            Text("Orçamentos, mensagens e o ERP são restritos ao perfil gestor.",style=MaterialTheme.typography.labelSmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun PortalScreen(nav:NavHostController,s:Sessao,logout:()->Unit){
    Page(nav,"Painel Central",s,logout){
        Box(Modifier.fillMaxWidth().background(Brush.linearGradient(listOf(MaterialTheme.geTechColors.sidebar,MaterialTheme.geTechColors.panelGradientEnd)),RoundedCornerShape(8.dp)).padding(22.dp)){
            Column{
                Text("SERVIDOR PRINCIPAL: OPERACIONAL",color=Color.White.copy(alpha=.7f),style=MaterialTheme.typography.labelSmall,fontWeight=FontWeight.Bold)
                Spacer(Modifier.height(8.dp));Text("Plataforma Integrada de Gestão de Ativos",color=Color.White,style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.Bold)
                Spacer(Modifier.height(8.dp));Text("Monitore a eficiência global de seus equipamentos, controle ordens de serviço, gerencie a cadeia de suprimentos e reduza o tempo de inatividade em um único ecossistema digital.",color=Color.White.copy(alpha=.85f),style=MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement=Arrangement.spacedBy(10.dp)){
                    Button(onClick={nav.navigate(Routes.ERP_HOME)},colors=ButtonDefaults.buttonColors(containerColor=Color.White,contentColor=MaterialTheme.colorScheme.primary)){Text("Entrar no sistema")}
                    OutlinedButton(onClick={nav.navigate(Routes.ERP)},colors=ButtonDefaults.outlinedButtonColors(contentColor=Color.White),border=BorderStroke(1.dp,Color.White)){Text("Conhecer o ERP")}
                }
            }
        }
        Spacer(Modifier.height(24.dp));Text("Módulos Estruturais do Sistema",style=MaterialTheme.typography.titleLarge,fontWeight=FontWeight.Bold)
        Spacer(Modifier.height(10.dp))
        listOf(
            "⚙️" to ("Controle de Manutenção" to "Ordens preventivas e corretivas estruturadas."),
            "📦" to ("Estoque de Peças" to "Rastreabilidade física e níveis críticos de insumos."),
            "📊" to ("Métricas de OEE" to "Eficiência, performance e qualidade em tempo real."),
            "🔒" to ("Auditoria & Logs" to "Histórico digital imutável de todas as ações.")
        ).forEach{(icone,rest)-> val(t,d)=rest
            GeCard(Modifier.fillMaxWidth().padding(vertical=5.dp)){ Column(Modifier.padding(16.dp)){ Text(icone,fontSize=androidx.compose.ui.unit.TextUnit(24f,androidx.compose.ui.unit.TextUnitType.Sp)); Spacer(Modifier.height(6.dp)); Text(t,fontWeight=FontWeight.Bold); Spacer(Modifier.height(2.dp)); Text(d,style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant) } }
        }
        Spacer(Modifier.height(20.dp));Text("Impacto Operacional Global",style=MaterialTheme.typography.titleLarge,fontWeight=FontWeight.Bold)
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement=Arrangement.spacedBy(8.dp),modifier=Modifier.fillMaxWidth()){
            StatCard("+150","Plantas Industriais Atendidas",Modifier.weight(1f))
            StatCard("99.8%","Disponibilidade de Dados",Modifier.weight(1f))
            StatCard("24/7","Monitoramento Ativo e Alertas",Modifier.weight(1f))
        }
    }
}
@Composable private fun StatCard(value:String,label:String,modifier:Modifier=Modifier){GeCard(modifier){Column(Modifier.padding(12.dp),horizontalAlignment=Alignment.CenterHorizontally){Text(value,style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.Bold,color=MaterialTheme.colorScheme.primary);Spacer(Modifier.height(4.dp));Text(label,style=MaterialTheme.typography.labelSmall,textAlign=androidx.compose.ui.text.style.TextAlign.Center,color=MaterialTheme.colorScheme.onSurfaceVariant)}}}

private data class ModuleLink(val icone:String,val title:String,val texto:String,val route:String)
@Composable
private fun ErpScreen(nav:NavHostController,s:Sessao,logout:()->Unit){
    val links=listOf(
        ModuleLink("🌐","Visão Geral","Documentação técnica e escopo do projeto.",Routes.ERP_GERAL),
        ModuleLink("📦","Gestão de Inventário","Entrada e saída de materiais.",Routes.ESTOQUE),
        ModuleLink("⚙️","Manutenção Ativa","Máquinas e ordens de serviço.",Routes.MANUT),
        ModuleLink("🏗️","Gestão de Máquinas","Modelo, série e última manutenção.",Routes.MAQUINAS),
        ModuleLink("🕐","RH & Ponto Digital","Colaboradores e registro de jornada.",Routes.RH),
        ModuleLink("📋","Ordens de Serviço","Gestão de pedidos de produção.",Routes.PEDIDOS),
        ModuleLink("✅","Controle de Qualidade","Inspeções e conformidade de lotes.",Routes.QUALIDADE),
        ModuleLink("🛒","Suprimentos","Requisições de compra e fornecedores.",Routes.SUPRIMENTOS),
        ModuleLink("🏭","Linha de Produção","Metas por turno e eficiência (OEE).",Routes.PRODUCAO),
        ModuleLink("📝","Logs de Operação","Auditoria de eventos do sistema.",Routes.LOGS),
        ModuleLink("🖥️","Sistema","Simulação técnica industrial.",Routes.SISTEMA),
        ModuleLink("🔧","Configurações","Tema, sessão e dados locais.",Routes.ERP_CONFIG)
    )
    Page(nav,"Painel ERP",s,logout){
        Text("Bem-vindo, ${s.nome}!",style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(8.dp))
        Text("Módulos do sistema",style=MaterialTheme.typography.headlineMedium,fontWeight=FontWeight.Bold)
        Spacer(Modifier.height(14.dp))
        links.forEach{m->GeCard(Modifier.fillMaxWidth().padding(vertical=5.dp).clickable{nav.navigate(m.route)}){Column(Modifier.padding(16.dp)){Text(m.icone,fontSize=androidx.compose.ui.unit.TextUnit(24f,androidx.compose.ui.unit.TextUnitType.Sp));Spacer(Modifier.height(6.dp));Text(m.title,fontWeight=FontWeight.Bold);Spacer(Modifier.height(2.dp));Text(m.texto,style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)}}}
    }
}

private data class InfoBloco(val titulo:String,val texto:String)
private val ERP_GERAL_BLOCOS = listOf(
    InfoBloco("Objetivo do sistema","Centralizar inventário, manutenção, pessoas e ordens de serviço da operação industrial em um único painel, reduzindo planilhas paralelas e retrabalho."),
    InfoBloco("Perfis de acesso","Clientes acompanham solicitações e materiais pelo portal público. Gestores têm acesso ao ERP completo, orçamentos, mensagens e auditoria."),
    InfoBloco("Persistência dos dados","Esta versão opera com armazenamento local no dispositivo, permitindo demonstração completa dos fluxos sem servidor. Cada operação gera registro no módulo de logs."),
    InfoBloco("Próximos passos","Integração com banco de dados na nuvem, autenticação com senha criptografada e emissão de relatórios em PDF.")
)
private val ERP_GERAL_INDICADORES = listOf("11" to "Módulos ativos","2" to "Perfis suportados","500 eventos" to "Auditoria")

@Composable
private fun ErpGeralScreen(nav:NavHostController){
    Page(nav,"Visão Geral"){
        Text("Visão Geral",style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.Bold)
        Text("Documentação técnica e escopo do sistema de gestão GeTech.",color=MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(14.dp))
        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(10.dp)){
            ERP_GERAL_INDICADORES.forEach{(valor,rotulo)-> StatCard(valor,rotulo,Modifier.weight(1f)) }
        }
        Spacer(Modifier.height(16.dp))
        ERP_GERAL_BLOCOS.forEach{b->
            GeCard(Modifier.fillMaxWidth().padding(vertical=5.dp)){
                Column(Modifier.padding(16.dp)){
                    Text(b.titulo,fontWeight=FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Text(b.texto,style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun StatusChip(text:String,color:Color,onClick:()->Unit){
    Text(text,color=color,fontWeight=FontWeight.Bold,style=MaterialTheme.typography.labelSmall,
        modifier=Modifier.clip(RoundedCornerShape(999.dp)).background(color.copy(alpha=.12f)).clickable(onClick=onClick).padding(horizontal=10.dp,vertical=5.dp))
}

@Composable
private fun EstoqueScreen(repo:GeTechRepository,nav:NavHostController){
    var refresh by remember{mutableIntStateOf(0)}
    var nome by remember{mutableStateOf("")};var codigo by remember{mutableStateOf("")}
    var qtd by remember{mutableStateOf("0")};var minimo by remember{mutableStateOf("5")};var local by remember{mutableStateOf("")}
    val itens=remember(refresh){repo.records("estoque")}
    val critico=itens.count{(it.campos["qtd"]?.toIntOrNull()?:0) <= (it.campos["minimo"]?.toIntOrNull()?:0)}
    Page(nav,"Gestão de Inventário"){
        Text("Gestão de Inventário",style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.Bold)
        Text("Cadastre materiais e registre entradas e saídas do almoxarifado.",color=MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(14.dp))
        GeCard(Modifier.fillMaxWidth()){
            Column(Modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(8.dp)){
                Text("Cadastrar material",fontWeight=FontWeight.Bold)
                FormField("Material",nome,{nome=it})
                FormField("Código",codigo,{codigo=it})
                Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){
                    OutlinedTextField(qtd,{qtd=it.filter{c->c.isDigit()}},label={Text("Qtd")},modifier=Modifier.weight(1f),keyboardOptions=androidx.compose.foundation.text.KeyboardOptions(keyboardType=androidx.compose.ui.text.input.KeyboardType.Number))
                    OutlinedTextField(minimo,{minimo=it.filter{c->c.isDigit()}},label={Text("Mín.")},modifier=Modifier.weight(1f),keyboardOptions=androidx.compose.foundation.text.KeyboardOptions(keyboardType=androidx.compose.ui.text.input.KeyboardType.Number))
                }
                FormField("Localização",local,{local=it})
                Button(onClick={
                    if(nome.isNotBlank()&&codigo.isNotBlank()){
                        repo.add("estoque",mapOf("nome" to nome,"codigo" to codigo,"qtd" to qtd,"minimo" to minimo,"local" to local))
                        nome="";codigo="";qtd="0";minimo="5";local="";refresh++
                    }
                },modifier=Modifier.fillMaxWidth()){Text("Cadastrar")}
            }
        }
        Spacer(Modifier.height(16.dp))
        Text("${itens.size} itens cadastrados · $critico abaixo do estoque mínimo",style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(8.dp))
        if(itens.isEmpty())Text("Nenhum item cadastrado.",Modifier.padding(vertical=20.dp))
        itens.forEach{i->
            val q=i.campos["qtd"]?.toIntOrNull()?:0; val min=i.campos["minimo"]?.toIntOrNull()?:0
            GeCard(Modifier.fillMaxWidth().padding(vertical=5.dp)){
                Column(Modifier.padding(14.dp)){
                    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){
                        Text(i.campos["nome"]?:"",fontWeight=FontWeight.Bold,modifier=Modifier.weight(1f))
                        if(q<=min) StatusChip("Repor",MaterialTheme.colorScheme.error){} else Text("Normal",style=MaterialTheme.typography.labelSmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text("${i.campos["codigo"]} · ${i.campos["local"]?.ifBlank{"—"}}",style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(10.dp)){
                        OutlinedButton(onClick={repo.update("estoque",i.id,mapOf("qtd" to maxOf(0,q-1).toString()));refresh++}){Text("−")}
                        Text("$q",fontWeight=FontWeight.Bold)
                        OutlinedButton(onClick={repo.update("estoque",i.id,mapOf("qtd" to (q+1).toString()));refresh++}){Text("+")}
                        Spacer(Modifier.weight(1f))
                        TextButton(onClick={repo.remove("estoque",i.id);refresh++}){Text("Excluir")}
                    }
                }
            }
        }
    }
}

private val MANUT_PROXIMO = mapOf("Aberta" to "Em execução","Em execução" to "Concluída","Concluída" to "Aberta")
@Composable
private fun ManutencaoScreen(repo:GeTechRepository,nav:NavHostController){
    var refresh by remember{mutableIntStateOf(0)}
    var maquina by remember{mutableStateOf("")};var setor by remember{mutableStateOf("")}
    var tipo by remember{mutableStateOf("Preventiva")};var responsavel by remember{mutableStateOf("")}
    val itens=remember(refresh){repo.records("manutencao")}
    Page(nav,"Manutenção Ativa"){
        Text("Manutenção Ativa",style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.Bold)
        Text("Abra ordens de serviço e acompanhe o andamento por máquina.",color=MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(14.dp))
        GeCard(Modifier.fillMaxWidth()){
            Column(Modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(8.dp)){
                Text("Abrir ordem de serviço",fontWeight=FontWeight.Bold)
                FormField("Máquina / equipamento",maquina,{maquina=it})
                FormField("Setor",setor,{setor=it})
                Text("Tipo",style=MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement=Arrangement.spacedBy(6.dp)){
                    listOf("Preventiva","Corretiva","Preditiva").forEach{t-> FilterChip(selected=tipo==t,onClick={tipo=t},label={Text(t)}) }
                }
                FormField("Responsável",responsavel,{responsavel=it})
                Button(onClick={
                    if(maquina.isNotBlank()){
                        repo.add("manutencao",mapOf("maquina" to maquina,"setor" to setor,"tipo" to tipo,"responsavel" to responsavel,"status" to "Aberta"))
                        maquina="";setor="";tipo="Preventiva";responsavel="";refresh++
                    }
                },modifier=Modifier.fillMaxWidth()){Text("Abrir OS")}
            }
        }
        Spacer(Modifier.height(16.dp))
        if(itens.isEmpty())Text("Nenhuma ordem registrada.",Modifier.padding(vertical=20.dp))
        itens.forEach{o->
            val status=o.campos["status"]?:"Aberta"
            val cor=when(status){"Aberta"->MaterialTheme.colorScheme.error;"Concluída"->MaterialTheme.colorScheme.primary;else->MaterialTheme.colorScheme.tertiary}
            GeCard(Modifier.fillMaxWidth().padding(vertical=5.dp)){
                Column(Modifier.padding(14.dp)){
                    Text(o.campos["maquina"]?:"",fontWeight=FontWeight.Bold)
                    Text("${o.campos["setor"]?.ifBlank{"—"}} · ${o.campos["tipo"]} · ${o.campos["responsavel"]?.ifBlank{"—"}}",style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment=Alignment.CenterVertically){
                        StatusChip(status,cor){repo.update("manutencao",o.id,mapOf("status" to (MANUT_PROXIMO[status]?:"Aberta")));refresh++}
                        Spacer(Modifier.weight(1f))
                        TextButton(onClick={repo.remove("manutencao",o.id);refresh++}){Text("Excluir")}
                    }
                }
            }
        }
    }
}

@Composable
private fun MaquinasScreen(repo:GeTechRepository,nav:NavHostController){
    var refresh by remember{mutableIntStateOf(0)}
    var nome by remember{mutableStateOf("")};var modelo by remember{mutableStateOf("")}
    var serie by remember{mutableStateOf("")};var ultima by remember{mutableStateOf("")}
    var filtro by remember{mutableStateOf("")}
    val itens=remember(refresh){repo.records("maquinas")}
    val filtrados=itens.filter{filtro.isBlank()||listOf(it.campos["nome"],it.campos["modelo"],it.campos["serie"]).any{v->v?.contains(filtro,true)==true}}
    Page(nav,"Gestão de Máquinas"){
        Text("Gestão de Máquinas",style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.Bold)
        Text("Cadastro e consulta do inventário de equipamentos industriais.",color=MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(14.dp))
        GeCard(Modifier.fillMaxWidth()){
            Column(Modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(8.dp)){
                Text("Cadastrar equipamento",fontWeight=FontWeight.Bold)
                FormField("Nome do equipamento",nome,{nome=it})
                FormField("Modelo",modelo,{modelo=it})
                FormField("Número de série",serie,{serie=it})
                FormField("Data da última manutenção",ultima,{ultima=it})
                Button(onClick={
                    if(nome.isNotBlank()&&modelo.isNotBlank()){
                        repo.add("maquinas",mapOf("nome" to nome,"modelo" to modelo,"serie" to serie,"ultimaManutencao" to ultima))
                        nome="";modelo="";serie="";ultima="";refresh++
                    }
                },modifier=Modifier.fillMaxWidth()){Text("Cadastrar equipamento")}
            }
        }
        Spacer(Modifier.height(16.dp))
        Text("Consultar inventário",fontWeight=FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(filtro,{filtro=it},placeholder={Text("Filtrar por nome, modelo ou série")},modifier=Modifier.fillMaxWidth(),singleLine=true,leadingIcon={Icon(Icons.Default.Search,null)})
        Spacer(Modifier.height(10.dp))
        if(filtrados.isEmpty())Text("Nenhum equipamento encontrado.",Modifier.padding(vertical=20.dp))
        filtrados.forEach{m->
            GeCard(Modifier.fillMaxWidth().padding(vertical=5.dp)){
                Column(Modifier.padding(14.dp)){
                    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){
                        Text(m.campos["nome"]?:"",fontWeight=FontWeight.Bold,modifier=Modifier.weight(1f))
                        TextButton(onClick={repo.remove("maquinas",m.id);refresh++}){Text("Excluir")}
                    }
                    Text("Modelo: ${m.campos["modelo"]} · Série: ${m.campos["serie"]}",style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Última manutenção: ${m.campos["ultimaManutencao"]?.ifBlank{"—"}}",style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun RhScreen(repo:GeTechRepository,nav:NavHostController){
    var refresh by remember{mutableIntStateOf(0)}
    var nome by remember{mutableStateOf("")};var cargo by remember{mutableStateOf("")};var setor by remember{mutableStateOf("")}
    val equipe=remember(refresh){repo.records("colaboradores")}
    val pontos=remember(refresh){repo.records("pontos")}
    Page(nav,"RH & Ponto Digital"){
        Text("RH & Ponto Digital",style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.Bold)
        Text("Cadastre a equipe e registre entradas e saídas da jornada.",color=MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(14.dp))
        GeCard(Modifier.fillMaxWidth()){
            Column(Modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(8.dp)){
                Text("Cadastrar colaborador",fontWeight=FontWeight.Bold)
                FormField("Nome do colaborador",nome,{nome=it})
                FormField("Cargo",cargo,{cargo=it})
                FormField("Setor",setor,{setor=it})
                Button(onClick={
                    if(nome.isNotBlank()){
                        repo.add("colaboradores",mapOf("nome" to nome,"cargo" to cargo,"setor" to setor,"entrada" to ""))
                        nome="";cargo="";setor="";refresh++
                    }
                },modifier=Modifier.fillMaxWidth()){Text("Cadastrar")}
            }
        }
        Spacer(Modifier.height(16.dp))
        if(equipe.isEmpty())Text("Nenhum colaborador cadastrado.",Modifier.padding(vertical=20.dp))
        equipe.forEach{c->
            val emTurno=!c.campos["entrada"].isNullOrBlank()
            GeCard(Modifier.fillMaxWidth().padding(vertical=5.dp)){
                Column(Modifier.padding(14.dp)){
                    Text(c.campos["nome"]?:"",fontWeight=FontWeight.Bold)
                    Text("${c.campos["cargo"]?.ifBlank{"—"}} · ${c.campos["setor"]?.ifBlank{"—"}}",style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(4.dp))
                    Text(if(emTurno)"Em turno desde ${c.campos["entrada"]}" else "Fora de turno",style=MaterialTheme.typography.labelSmall,color=if(emTurno)MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,fontWeight=FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement=Arrangement.spacedBy(10.dp)){
                        OutlinedButton(onClick={
                            val agora=java.text.SimpleDateFormat("HH:mm").format(java.util.Date())
                            repo.update("colaboradores",c.id,mapOf("entrada" to if(emTurno) "" else agora))
                            repo.add("pontos",mapOf("colaborador" to (c.campos["nome"]?:""),"tipo" to if(emTurno) "Saída" else "Entrada"))
                            refresh++
                        }){Text(if(emTurno)"Registrar saída" else "Registrar entrada")}
                        TextButton(onClick={repo.remove("colaboradores",c.id);refresh++}){Text("Excluir")}
                    }
                }
            }
        }
        Spacer(Modifier.height(20.dp))
        Text("Últimos registros de ponto",fontWeight=FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        if(pontos.isEmpty())Text("Nenhum ponto registrado ainda.",style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
        pontos.take(10).forEach{p->
            Row(Modifier.fillMaxWidth().padding(vertical=6.dp),horizontalArrangement=Arrangement.SpaceBetween){
                Text(p.campos["colaborador"]?:"",fontWeight=FontWeight.Bold)
                Text(p.campos["tipo"]?:"",style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
            }
            HorizontalDivider(color=MaterialTheme.colorScheme.outline.copy(alpha=.3f))
        }
    }
}

private val QUALIDADE_PROXIMO = mapOf("Em análise" to "Aprovado","Aprovado" to "Reprovado","Reprovado" to "Em análise")
@Composable
private fun QualidadeScreen(repo:GeTechRepository,nav:NavHostController){
    var refresh by remember{mutableIntStateOf(0)}
    var lote by remember{mutableStateOf("")};var item by remember{mutableStateOf("")};var inspetor by remember{mutableStateOf("")}
    val itens=remember(refresh){repo.records("qualidade")}
    val aprovados=itens.count{it.campos["resultado"]=="Aprovado"}
    val reprovados=itens.count{it.campos["resultado"]=="Reprovado"}
    val taxa=if(itens.isNotEmpty()) (aprovados*100/itens.size) else 0
    Page(nav,"Controle de Qualidade"){
        Text("Controle de Qualidade",style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.Bold)
        Text("Inspeções, conformidade de lotes e índice de aprovação.",color=MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(14.dp))
        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(10.dp)){
            StatCard("${itens.size}","Inspeções",Modifier.weight(1f))
            StatCard("$taxa%","Aprovação",Modifier.weight(1f))
            StatCard("$reprovados","Reprovados",Modifier.weight(1f))
        }
        Spacer(Modifier.height(16.dp))
        GeCard(Modifier.fillMaxWidth()){
            Column(Modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(8.dp)){
                Text("Nova inspeção",fontWeight=FontWeight.Bold)
                FormField("Lote",lote,{lote=it})
                FormField("Item inspecionado",item,{item=it})
                FormField("Inspetor",inspetor,{inspetor=it})
                Button(onClick={
                    if(lote.isNotBlank()&&item.isNotBlank()){
                        repo.add("qualidade",mapOf("lote" to lote,"item" to item,"inspetor" to inspetor,"resultado" to "Em análise"))
                        lote="";item="";inspetor="";refresh++
                    }
                },modifier=Modifier.fillMaxWidth()){Text("Registrar inspeção")}
            }
        }
        Spacer(Modifier.height(16.dp))
        if(itens.isEmpty())Text("Nenhuma inspeção registrada.",Modifier.padding(vertical=20.dp))
        itens.forEach{i->
            val resultado=i.campos["resultado"]?:"Em análise"
            val cor=when(resultado){"Aprovado"->MaterialTheme.colorScheme.primary;"Reprovado"->MaterialTheme.colorScheme.error;else->MaterialTheme.colorScheme.tertiary}
            GeCard(Modifier.fillMaxWidth().padding(vertical=5.dp)){
                Column(Modifier.padding(14.dp)){
                    Text(i.campos["lote"]?:"",fontWeight=FontWeight.Bold)
                    Text("${i.campos["item"]} · ${i.campos["inspetor"]}",style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment=Alignment.CenterVertically){
                        StatusChip(resultado,cor){repo.update("qualidade",i.id,mapOf("resultado" to (QUALIDADE_PROXIMO[resultado]?:"Em análise")));refresh++}
                        Spacer(Modifier.weight(1f))
                        TextButton(onClick={repo.remove("qualidade",i.id);refresh++}){Text("Excluir")}
                    }
                }
            }
        }
    }
}

private val SUPRIMENTOS_FLUXO = listOf("Solicitado","Cotação","Aprovado","Recebido")
@Composable
private fun SuprimentosScreen(repo:GeTechRepository,nav:NavHostController){
    var refresh by remember{mutableIntStateOf(0)}
    var item by remember{mutableStateOf("")};var fornecedor by remember{mutableStateOf("")};var quantidade by remember{mutableStateOf("")}
    val itens=remember(refresh){repo.records("suprimentos")}
    Page(nav,"Suprimentos"){
        Text("Suprimentos",style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.Bold)
        Text("Requisições de compra e rastreio de fornecedores da operação.",color=MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(14.dp))
        GeCard(Modifier.fillMaxWidth()){
            Column(Modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(8.dp)){
                Text("Nova requisição",fontWeight=FontWeight.Bold)
                FormField("Item / peça",item,{item=it})
                FormField("Fornecedor",fornecedor,{fornecedor=it})
                FormField("Quantidade",quantidade,{quantidade=it})
                Button(onClick={
                    if(item.isNotBlank()&&fornecedor.isNotBlank()){
                        repo.add("suprimentos",mapOf("item" to item,"fornecedor" to fornecedor,"quantidade" to quantidade,"status" to "Solicitado"))
                        item="";fornecedor="";quantidade="";refresh++
                    }
                },modifier=Modifier.fillMaxWidth()){Text("Solicitar compra")}
            }
        }
        Spacer(Modifier.height(18.dp))
        SUPRIMENTOS_FLUXO.forEach{etapa->
            val lista=itens.filter{it.campos["status"]==etapa}
            Text("$etapa (${lista.size})",fontWeight=FontWeight.Bold,style=MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(6.dp))
            if(lista.isEmpty())Text("Sem requisições.",style=MaterialTheme.typography.labelSmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
            lista.forEach{r->
                GeCard(Modifier.fillMaxWidth().padding(vertical=4.dp)){
                    Column(Modifier.padding(12.dp)){
                        Text(r.campos["item"]?:"",fontWeight=FontWeight.Bold)
                        Text("${r.campos["fornecedor"]} · ${r.campos["quantidade"]}",style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(6.dp))
                        Row(horizontalArrangement=Arrangement.spacedBy(10.dp)){
                            TextButton(onClick={
                                val idx=SUPRIMENTOS_FLUXO.indexOf(etapa)
                                val prox=SUPRIMENTOS_FLUXO[(idx+1)%SUPRIMENTOS_FLUXO.size]
                                repo.update("suprimentos",r.id,mapOf("status" to prox));refresh++
                            }){Text("Avançar")}
                            TextButton(onClick={repo.remove("suprimentos",r.id);refresh++}){Text("Excluir")}
                        }
                    }
                }
            }
            Spacer(Modifier.height(14.dp))
        }
    }
}

@Composable
private fun ProducaoScreen(repo:GeTechRepository,nav:NavHostController){
    var refresh by remember{mutableIntStateOf(0)}
    var linha by remember{mutableStateOf("")};var turno by remember{mutableStateOf("1º turno")}
    var meta by remember{mutableStateOf("")};var produzido by remember{mutableStateOf("")}
    val itens=remember(refresh){repo.records("producao")}
    val metaTotal=itens.sumOf{it.campos["meta"]?.toIntOrNull()?:0}
    val produzidoTotal=itens.sumOf{it.campos["produzido"]?.toIntOrNull()?:0}
    val oee=if(metaTotal>0)(produzidoTotal*100/metaTotal) else 0
    Page(nav,"Linha de Produção"){
        Text("Linha de Produção",style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.Bold)
        Text("Metas por turno, volume produzido e eficiência global.",color=MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(14.dp))
        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(10.dp)){
            StatCard("${itens.size}","Linhas monitoradas",Modifier.weight(1f))
            StatCard("$produzidoTotal","Peças produzidas",Modifier.weight(1f))
            StatCard("$oee%","Eficiência (OEE)",Modifier.weight(1f))
        }
        Spacer(Modifier.height(16.dp))
        GeCard(Modifier.fillMaxWidth()){
            Column(Modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(8.dp)){
                Text("Registrar apontamento",fontWeight=FontWeight.Bold)
                FormField("Linha / célula",linha,{linha=it})
                FormField("Turno",turno,{turno=it})
                FormField("Meta",meta,{meta=it.filter{c->c.isDigit()}})
                FormField("Produzido",produzido,{produzido=it.filter{c->c.isDigit()}})
                Button(onClick={
                    if(linha.isNotBlank()){
                        repo.add("producao",mapOf("linha" to linha,"turno" to turno,"meta" to meta.ifBlank{"0"},"produzido" to produzido.ifBlank{"0"}))
                        linha="";turno="1º turno";meta="";produzido="";refresh++
                    }
                },modifier=Modifier.fillMaxWidth()){Text("Salvar apontamento")}
            }
        }
        Spacer(Modifier.height(16.dp))
        if(itens.isEmpty())Text("Nenhuma linha registrada.",Modifier.padding(vertical=20.dp))
        itens.forEach{l->
            val m=l.campos["meta"]?.toIntOrNull()?:0; val p=l.campos["produzido"]?.toIntOrNull()?:0
            val pct=if(m>0)(p*100/m) else 0
            GeCard(Modifier.fillMaxWidth().padding(vertical=5.dp)){
                Column(Modifier.padding(14.dp)){
                    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){
                        Text(l.campos["linha"]?:"",fontWeight=FontWeight.Bold,modifier=Modifier.weight(1f))
                        StatusChip("$pct%",if(pct>=100)MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary){}
                    }
                    Text("${l.campos["turno"]} · meta ${m}",style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(10.dp)){
                        OutlinedButton(onClick={repo.update("producao",l.id,mapOf("produzido" to (p+1).toString()));refresh++}){Text("$p +1")}
                        Spacer(Modifier.weight(1f))
                        TextButton(onClick={repo.remove("producao",l.id);refresh++}){Text("Excluir")}
                    }
                }
            }
        }
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

/** Mapeia valores de status conhecidos do design system Lovable para a cor semântica correspondente. */
@Composable
private fun statusBadgeColor(value:String):Color? = when(value){
    "Operacional","Aprovado","A Fazer","Concluída","Normal","Recebido" -> MaterialTheme.colorScheme.primary
    "Em Manutenção","Em execução","Em análise","Em Andamento","Cotação" -> MaterialTheme.colorScheme.tertiary
    "Parada","Reprovado","Aberta","Urgente","URGENTE","Repor" -> MaterialTheme.colorScheme.error
    else -> null
}

@Composable
private fun RecordCard(r:Registro,onDelete:()->Unit){
    Card(Modifier.fillMaxWidth().padding(vertical=5.dp)){Column(Modifier.padding(14.dp)){
        r.campos.entries.take(6).forEachIndexed{index,(k,v)->
            val cor=statusBadgeColor(v)
            Row(Modifier.fillMaxWidth().padding(vertical=2.dp),verticalAlignment=Alignment.CenterVertically){
                Text(if(index==0)k.replaceFirstChar{it.uppercase()} else "$k: ",fontWeight=if(index==0)FontWeight.Bold else FontWeight.Normal,modifier=Modifier.widthIn(max=120.dp))
                if(cor!=null) Surface(color=cor.copy(alpha=.12f),shape=RoundedCornerShape(50)){Text(v,color=cor,fontWeight=FontWeight.Bold,style=MaterialTheme.typography.labelSmall,modifier=Modifier.padding(horizontal=8.dp,vertical=2.dp))}
                else Text(v,Modifier.weight(1f))
            }
        }
        Spacer(Modifier.height(5.dp));TextButton(onClick=onDelete){Text("Excluir")}
    }}
}

@Composable
private fun AddRecordDialog(key:String,title:String,repo:GeTechRepository,onDone:()->Unit){
    var text by remember{mutableStateOf("")}
    AlertDialog(onDismissRequest=onDone,title={Text("Novo registro")},text={Column{Text("Digite os dados principais separados por | para manter a entrada local.");Spacer(Modifier.height(8.dp));OutlinedTextField(text,{text=it},modifier=Modifier.fillMaxWidth(),placeholder={Text("nome | código | quantidade")})}},confirmButton={Button(onClick={if(text.isNotBlank()){repo.add(key,mapOf("registro" to text));onDone()}}){Text("Cadastrar")}},dismissButton={TextButton(onClick=onDone){Text("Cancelar")}})
}

private val PEDIDOS_STATUS = listOf("A Fazer","Em Andamento","Qualidade","Finalizado")
private val PEDIDOS_PERFIS = linkedMapOf(
    "PCP" to "Acesso: PCP (cadastro e edição completa)",
    "Produção" to "Acesso: Produção (avanço de etapas)",
    "Gestão" to "Acesso: Gestão (visão total e relatórios)",
    "Entregador" to "Acesso: Entregador (somente entregas)"
)

@Composable
private fun pedidoStatusColor(status:String):Color = when(status){
    "A Fazer"->MaterialTheme.colorScheme.primary
    "Em Andamento"->MaterialTheme.colorScheme.tertiary
    "Qualidade"->MaterialTheme.colorScheme.error
    else->MaterialTheme.colorScheme.onSurfaceVariant
}

@Composable
private fun OrdersScreen(repo:GeTechRepository,nav:NavHostController){
    var refresh by remember{mutableIntStateOf(0)}
    var tab by remember{mutableIntStateOf(0)}
    var perfil by remember{mutableStateOf("PCP")}
    var pesquisa by remember{mutableStateOf("")}
    val itens=remember(refresh){repo.records("pedidos")}
    val podeCadastrar = perfil=="PCP" || perfil=="Gestão"
    val termo=pesquisa.trim().lowercase()
    val filtrados=itens.filter{termo.isBlank()||"${it.campos["op"]} ${it.campos["cliente"]} ${it.campos["produto"]}".lowercase().contains(termo)}
    val afazer=itens.count{it.campos["status"]=="A Fazer"}
    val producaoCount=itens.count{it.campos["status"]=="Em Andamento"}
    val pronto=itens.count{it.campos["status"]=="Finalizado"}

    fun avancar(p:Registro){
        val atual=p.campos["status"]?:"A Fazer"
        val idx=PEDIDOS_STATUS.indexOf(atual)
        val prox=PEDIDOS_STATUS[(idx+1)%PEDIDOS_STATUS.size]
        repo.update("pedidos",p.id,mapOf("status" to prox));refresh++
    }

    Page(nav,"Pedidos"){
        Text("Ordens de Serviço",style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.Bold)
        Text("Controle completo de ordens industriais: kanban, recebimento, rastreio e frete.",color=MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(12.dp))
        GeCard(Modifier.fillMaxWidth()){
            Column(Modifier.padding(14.dp)){
                Text("Perfil de acesso",fontWeight=FontWeight.Bold,style=MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement=Arrangement.spacedBy(6.dp)){
                    PEDIDOS_PERFIS.keys.forEach{p-> FilterChip(selected=perfil==p,onClick={perfil=p},label={Text(p,style=MaterialTheme.typography.labelSmall)}) }
                }
                Spacer(Modifier.height(6.dp))
                Text(PEDIDOS_PERFIS[perfil]?:"",style=MaterialTheme.typography.labelSmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(pesquisa,{pesquisa=it},modifier=Modifier.fillMaxWidth(),placeholder={Text("Pesquisar por OP, cliente ou produto...")},singleLine=true,leadingIcon={Icon(Icons.Default.Search,null)})
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement=Arrangement.spacedBy(14.dp)){
            Text("A Fazer: $afazer",style=MaterialTheme.typography.labelSmall,fontWeight=FontWeight.Bold,color=MaterialTheme.colorScheme.primary)
            Text("Produção: $producaoCount",style=MaterialTheme.typography.labelSmall,fontWeight=FontWeight.Bold,color=MaterialTheme.colorScheme.tertiary)
            Text("Pronto: $pronto",style=MaterialTheme.typography.labelSmall,fontWeight=FontWeight.Bold,color=MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(Modifier.height(12.dp))
        val tabs=listOf("Kanban","Recebimento","Rastreio","Frete","Tabela")
        ScrollableTabRow(selectedTabIndex=tab,edgePadding=0.dp){tabs.forEachIndexed{i,t->Tab(selected=tab==i,onClick={tab=i},text={Text(t)})}}
        Spacer(Modifier.height(14.dp))
        when(tab){
            0-> PedidosKanban(filtrados,perfil,onAvancar={avancar(it)})
            1-> PedidosCadastro(repo,podeCadastrar,perfil){refresh++}
            2-> PedidosRastreio(itens)
            3-> PedidosFrete()
            4-> PedidosTabela(filtrados,itens.size,podeCadastrar,onAvancar={avancar(it)},onDelete={repo.remove("pedidos",it.id);refresh++})
        }
    }
}

@Composable
private fun PedidosKanban(itens:List<Registro>,perfil:String,onAvancar:(Registro)->Unit){
    Column{
        PEDIDOS_STATUS.forEach{coluna->
            val lista=itens.filter{it.campos["status"]==coluna}
            Text(coluna.uppercase(),style=MaterialTheme.typography.labelSmall,fontWeight=FontWeight.Bold,color=MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(6.dp))
            if(lista.isEmpty())Text("Vazio",style=MaterialTheme.typography.labelSmall,color=MaterialTheme.colorScheme.onSurfaceVariant,modifier=Modifier.padding(vertical=8.dp))
            lista.forEach{p->
                Card(Modifier.fillMaxWidth().padding(vertical=4.dp).clickable(enabled=perfil!="Entregador"){onAvancar(p)}){
                    Column(Modifier.padding(12.dp)){
                        Text(p.campos["op"]?.ifBlank{"OP —"}?:"OP —",fontWeight=FontWeight.Bold)
                        Text(p.campos["cliente"]?:"",style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(p.campos["produto"]?:"",style=MaterialTheme.typography.bodySmall)
                        if(p.campos["prioridade"]=="Urgente"){
                            Spacer(Modifier.height(4.dp))
                            StatusChip("URGENTE",MaterialTheme.colorScheme.error){}
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun PedidosCadastro(repo:GeTechRepository,podeCadastrar:Boolean,perfil:String,onSaved:()->Unit){
    if(!podeCadastrar){
        GeCard(Modifier.fillMaxWidth()){Column(Modifier.padding(24.dp)){Text("O perfil $perfil não tem permissão para cadastrar ordens.",style=MaterialTheme.typography.bodyMedium,color=MaterialTheme.colorScheme.onSurfaceVariant,textAlign=androidx.compose.ui.text.style.TextAlign.Center,modifier=Modifier.fillMaxWidth())}}
        return
    }
    var op by remember{mutableStateOf("")};var cliente by remember{mutableStateOf("")};var produto by remember{mutableStateOf("")}
    var qtd by remember{mutableStateOf("1")};var responsavel by remember{mutableStateOf("")}
    var cep by remember{mutableStateOf("")};var rua by remember{mutableStateOf("")};var numero by remember{mutableStateOf("")}
    var bairro by remember{mutableStateOf("")};var cidade by remember{mutableStateOf("")}
    var status by remember{mutableStateOf("A Fazer")};var prioridade by remember{mutableStateOf("Normal")}
    var prazo by remember{mutableStateOf("")};var descricao by remember{mutableStateOf("")}

    GeCard(Modifier.fillMaxWidth()){
        Column(Modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(8.dp)){
            Text("Registrar nova ordem",fontWeight=FontWeight.Bold)
            FormField("Nº OP *",op,{op=it})
            FormField("Cliente *",cliente,{cliente=it})
            FormField("Produto *",produto,{produto=it})
            FormField("Quantidade *",qtd,{qtd=it.filter{c->c.isDigit()}})
            FormField("Responsável",responsavel,{responsavel=it})
            FormField("CEP",cep,{cep=it})
            FormField("Rua",rua,{rua=it})
            FormField("Nº Casa",numero,{numero=it})
            FormField("Bairro",bairro,{bairro=it})
            FormField("Cidade",cidade,{cidade=it})
            Text("Status",style=MaterialTheme.typography.labelMedium)
            Row(horizontalArrangement=Arrangement.spacedBy(6.dp)){PEDIDOS_STATUS.forEach{s->FilterChip(selected=status==s,onClick={status=s},label={Text(s,style=MaterialTheme.typography.labelSmall)})}}
            Text("Prioridade",style=MaterialTheme.typography.labelMedium)
            Row(horizontalArrangement=Arrangement.spacedBy(6.dp)){listOf("Normal","Urgente").forEach{p->FilterChip(selected=prioridade==p,onClick={prioridade=p},label={Text(p)})}}
            FormField("Prazo (dd/mm/aaaa)",prazo,{prazo=it})
            OutlinedTextField(descricao,{descricao=it},label={Text("Descrição / Observações")},modifier=Modifier.fillMaxWidth().height(100.dp))
            Button(onClick={
                if(op.isNotBlank()&&cliente.isNotBlank()&&produto.isNotBlank()){
                    repo.add("pedidos",mapOf("op" to op,"cliente" to cliente,"produto" to produto,"qtd" to qtd.ifBlank{"1"},"responsavel" to responsavel,"cep" to cep,"rua" to rua,"numeroCasa" to numero,"bairro" to bairro,"cidade" to cidade,"status" to status,"prioridade" to prioridade,"prazo" to prazo,"descricao" to descricao))
                    op="";cliente="";produto="";qtd="1";responsavel="";cep="";rua="";numero="";bairro="";cidade="";status="A Fazer";prioridade="Normal";prazo="";descricao=""
                    onSaved()
                }
            },modifier=Modifier.fillMaxWidth()){Text("Registrar Ordem")}
        }
    }
}

@Composable
private fun PedidosRastreio(itens:List<Registro>){
    var rastreio by remember{mutableStateOf("")}
    var achado by remember{mutableStateOf<Registro?>(null)}
    var buscou by remember{mutableStateOf(false)}
    GeCard(Modifier.fillMaxWidth()){
        Column(Modifier.padding(16.dp)){
            Text("Área de Rastreio",fontWeight=FontWeight.Bold,style=MaterialTheme.typography.titleMedium)
            Text("Consulte rapidamente uma ordem pelo nº OP ou nome do cliente.",style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(rastreio,{rastreio=it},modifier=Modifier.fillMaxWidth(),placeholder={Text("Digite o nº OP ou nome do cliente")},singleLine=true)
            Spacer(Modifier.height(8.dp))
            Button(onClick={
                val alvo=rastreio.trim().lowercase()
                achado=itens.find{(it.campos["op"]?.lowercase()==alvo)||(it.campos["cliente"]?.lowercase()?.contains(alvo)==true)}
                buscou=true
            },modifier=Modifier.fillMaxWidth()){Text("Rastrear")}
            if(buscou && achado==null){
                Spacer(Modifier.height(12.dp))
                Text("Nenhuma ordem encontrada.",color=MaterialTheme.colorScheme.error,style=MaterialTheme.typography.bodySmall)
            }
            achado?.let{p->
                Spacer(Modifier.height(12.dp))
                Card(Modifier.fillMaxWidth(),border=BorderStroke(1.dp,MaterialTheme.colorScheme.outline)){
                    Column(Modifier.padding(14.dp)){
                        Text("${p.campos["op"]} — ${p.campos["cliente"]}",fontWeight=FontWeight.Bold)
                        Text("${p.campos["produto"]} · ${p.campos["qtd"]} un.",style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment=Alignment.CenterVertically){
                            Text("Status atual: ",style=MaterialTheme.typography.bodySmall)
                            val st=p.campos["status"]?:"A Fazer"
                            StatusChip(st,pedidoStatusColor(st)){}
                        }
                        Spacer(Modifier.height(8.dp))
                        Text("Entrega: ${p.campos["rua"]?:""} ${p.campos["numeroCasa"]?:""}, ${p.campos["bairro"]?:""} — ${p.campos["cidade"]?:""}",style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun PedidosFrete(){
    var peso by remember{mutableStateOf("")};var distancia by remember{mutableStateOf("")};var tipo by remember{mutableStateOf(1.0)}
    var valor by remember{mutableStateOf<Double?>(null)}
    GeCard(Modifier.fillMaxWidth()){
        Column(Modifier.padding(16.dp)){
            Text("Cálculo de Frete",fontWeight=FontWeight.Bold,style=MaterialTheme.typography.titleMedium)
            Text("Simule valores de transporte industrial.",style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(10.dp))
            FormField("Peso da carga (kg)",peso,{peso=it.filter{c->c.isDigit()||c=='.'}})
            FormField("Distância (km)",distancia,{distancia=it.filter{c->c.isDigit()||c=='.'}})
            Text("Tipo de entrega",style=MaterialTheme.typography.labelMedium)
            Row(horizontalArrangement=Arrangement.spacedBy(6.dp)){
                listOf("Normal" to 1.0,"Expressa" to 1.4,"Urgente" to 1.8).forEach{(label,mult)->
                    FilterChip(selected=tipo==mult,onClick={tipo=mult},label={Text(label)})
                }
            }
            Spacer(Modifier.height(10.dp))
            Button(onClick={
                val p=peso.toDoubleOrNull()?:0.0; val km=distancia.toDoubleOrNull()?:0.0
                valor=(p*0.75+km*2.4+35)*tipo
            },modifier=Modifier.fillMaxWidth()){Text("Calcular Frete")}
            valor?.let{
                Spacer(Modifier.height(14.dp))
                Text("R$ %.2f".format(it),style=MaterialTheme.typography.headlineMedium,fontWeight=FontWeight.Bold,color=MaterialTheme.colorScheme.primary,modifier=Modifier.fillMaxWidth(),textAlign=androidx.compose.ui.text.style.TextAlign.Center)
            }
        }
    }
}

@Composable
private fun PedidosTabela(filtrados:List<Registro>,total:Int,podeCadastrar:Boolean,onAvancar:(Registro)->Unit,onDelete:(Registro)->Unit){
    Text("Tabela de Pedidos",fontWeight=FontWeight.Bold,style=MaterialTheme.typography.titleMedium)
    Spacer(Modifier.height(10.dp))
    if(filtrados.isEmpty())Text("Nenhum pedido encontrado.",Modifier.padding(vertical=20.dp))
    filtrados.forEach{p->
        val st=p.campos["status"]?:"A Fazer"
        GeCard(Modifier.fillMaxWidth().padding(vertical=5.dp)){
            Column(Modifier.padding(14.dp)){
                Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){
                    Text(p.campos["op"]?.ifBlank{"—"}?:"—",fontWeight=FontWeight.Bold,modifier=Modifier.weight(1f))
                    Text(p.campos["prioridade"]?:"",style=MaterialTheme.typography.labelSmall,color=if(p.campos["prioridade"]=="Urgente") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text("${p.campos["cliente"]} · ${p.campos["produto"]} · ${p.campos["qtd"]} un.",style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Bairro: ${p.campos["bairro"]?.ifBlank{"—"}} · Prazo: ${p.campos["prazo"]?.ifBlank{"—"}}",style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment=Alignment.CenterVertically){
                    StatusChip(st,pedidoStatusColor(st)){onAvancar(p)}
                    Spacer(Modifier.weight(1f))
                    TextButton(onClick={onDelete(p)},enabled=podeCadastrar){Text("Excluir")}
                }
            }
        }
    }
    Spacer(Modifier.height(8.dp))
    Text("${filtrados.size} de $total ordens exibidas",style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
}
@Composable private fun SectionCard(title:String,items:List<Registro>){Card(Modifier.fillMaxWidth().padding(vertical=5.dp)){Column(Modifier.padding(12.dp)){Text(title,fontWeight=FontWeight.Bold,color=MaterialTheme.colorScheme.primary);items.take(5).forEach{Text("${it.campos["op"]}: ${it.campos["produto"]}",Modifier.padding(vertical=3.dp))}}}}

@Composable
private fun LogsScreen(repo:GeTechRepository,nav:NavHostController){
    Page(nav,"Logs de Operação"){
        Text("Logs e auditoria",style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.Bold)
        Spacer(Modifier.height(10.dp))
        repo.logs().forEach{line->
            val p=line.split("|",limit=3)
            val nivel=p.getOrNull(2)?.trim()?:"INFO"
            val cor=when(nivel){"CRITICO"->MaterialTheme.colorScheme.error;"AVISO"->MaterialTheme.colorScheme.tertiary;else->MaterialTheme.colorScheme.primary}
            Card(Modifier.fillMaxWidth().padding(vertical=4.dp)){
                Column(Modifier.padding(12.dp)){
                    Text(p.getOrNull(0)?:"",fontWeight=FontWeight.Bold)
                    Text(p.getOrNull(1)?:"")
                    Spacer(Modifier.height(4.dp))
                    Surface(color=cor.copy(alpha=.12f),shape=RoundedCornerShape(50)){Text(nivel,color=cor,fontWeight=FontWeight.Bold,style=MaterialTheme.typography.labelSmall,modifier=Modifier.padding(horizontal=8.dp,vertical=2.dp))}
                }
            }
        }
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

private data class Depoimento(val nome:String,val cargo:String,val nota:Int,val texto:String)
private val DEPOIMENTOS_FIXOS = listOf(
    Depoimento("Ricardo Souza","Gerente de Operações — Metalúrgica Norte",5,"Reduzimos o tempo de parada de máquina em 22% logo no primeiro semestre. A precisão dos relatórios é impressionante."),
    Depoimento("Ana Paula","Diretora de Logística — Indústria Alimentícia",5,"O suporte técnico é ágil e o sistema é intuitivo. O controle de inventário finalmente está batendo com o físico."),
    Depoimento("Marcos Vinícius","Engenheiro Chefe — TechParts Brasil",5,"A integração com o chão de fábrica via sensores mudou nossa visão da produção em tempo real.")
)

@Composable
private fun TestimonialsScreen(repo:GeTechRepository,nav:NavHostController){
    var aberto by remember{mutableStateOf(false)}
    var nome by remember{mutableStateOf("")};var cargo by remember{mutableStateOf("")};var nota by remember{mutableIntStateOf(5)};var texto by remember{mutableStateOf("")}
    val extras = remember{mutableStateListOf<Depoimento>()}
    Page(nav,"O que nossos clientes dizem"){
        Text("O que nossos clientes dizem",style=MaterialTheme.typography.headlineMedium,fontWeight=FontWeight.Bold)
        Text("Resultados reais de quem vive a transformação industrial diariamente.",color=MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(14.dp))
        Button(onClick={aberto=!aberto}){Text(if(aberto)"Fechar formulário" else "Adicionar depoimento")}
        if(aberto){
            Spacer(Modifier.height(12.dp))
            GeCard(Modifier.fillMaxWidth()){
                Column(Modifier.padding(18.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){
                    Text("Compartilhe sua experiência",fontWeight=FontWeight.Bold)
                    FormField("Seu nome",nome,{nome=it})
                    FormField("Cargo e empresa",cargo,{cargo=it})
                    Text("Avaliação: $nota ${if(nota==1)"estrela" else "estrelas"}",style=MaterialTheme.typography.labelMedium)
                    Slider(value=nota.toFloat(),onValueChange={nota=it.toInt()},valueRange=1f..5f,steps=3)
                    OutlinedTextField(texto,{texto=it},placeholder={Text("Conte como a GeTech ajudou sua operação")},modifier=Modifier.fillMaxWidth().height(100.dp))
                    Button(onClick={
                        if(nome.isNotBlank()&&texto.isNotBlank()){extras.add(0,Depoimento(nome,cargo,nota,texto));nome="";cargo="";nota=5;texto="";aberto=false}
                    },modifier=Modifier.fillMaxWidth()){Text("Salvar depoimento")}
                }
            }
        }
        Spacer(Modifier.height(18.dp))
        (extras+DEPOIMENTOS_FIXOS).forEach{d->
            GeCard(Modifier.fillMaxWidth().padding(vertical=5.dp)){
                Column(Modifier.padding(16.dp)){
                    Text("★".repeat(d.nota)+"☆".repeat(5-d.nota),color=MaterialTheme.colorScheme.tertiary)
                    Spacer(Modifier.height(8.dp));Text("\u201C${d.texto}\u201D",style=MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(10.dp));Text(d.nome,fontWeight=FontWeight.Bold,style=MaterialTheme.typography.bodySmall)
                    Text(d.cargo,style=MaterialTheme.typography.labelSmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun FaqScreen(nav:NavHostController){
    Page(nav,"Perguntas Frequentes"){
        Text("Perguntas Frequentes",style=MaterialTheme.typography.headlineMedium,fontWeight=FontWeight.Bold)
        Text("Tudo sobre o ERP Industrial da GeTech.",color=MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(16.dp))
        val qs=listOf(
            "Como funciona a integração com o chão de fábrica?" to "Nossa plataforma utiliza protocolos industriais (como MQTT e OPC UA) para ler dados diretamente de sensores e CLPs, atualizando o inventário e a produção em tempo real sem intervenção manual.",
            "O sistema funciona sem internet?" to "Sim, o ERP possui um módulo Edge que permite a operação offline, sincronizando os dados automaticamente com a nuvem assim que a conexão for restabelecida.",
            "Qual o tempo médio de implementação?" to "Para indústrias de médio porte, a implementação completa leva entre 4 e 8 semanas, incluindo o treinamento da equipe e a migração de dados históricos.",
            "O ERP GeTech é compatível com normas de auditoria?" to "Sim, o sistema é nativamente compatível com as normas ISO 9001 e IATF 16949, gerando relatórios de rastreabilidade total de lotes e histórico de manutenções."
        )
        GeCard(Modifier.fillMaxWidth()){
            Column(Modifier.padding(6.dp)){
                qs.forEachIndexed{i,(q,a)->
                    var open by remember{mutableStateOf(false)}
                    Column(Modifier.fillMaxWidth().clickable{open=!open}.padding(12.dp)){
                        Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically){
                            Text(q,Modifier.weight(1f),fontWeight=FontWeight.SemiBold)
                            Icon(if(open) Icons.Default.ExpandLess else Icons.Default.ExpandMore,null,tint=MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        if(open){Spacer(Modifier.height(8.dp));Text(a,style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)}
                    }
                    if(i<qs.lastIndex) HorizontalDivider(color=MaterialTheme.colorScheme.outline.copy(alpha=.4f))
                }
            }
        }
    }
}

@Composable
private fun PrivacyScreen(nav:NavHostController){
    var aceito by remember{mutableStateOf(false)}
    var dataAceite by remember{mutableStateOf<String?>(null)}
    Page(nav,"Política de Privacidade"){
        Text("Política de Privacidade",style=MaterialTheme.typography.headlineMedium,fontWeight=FontWeight.Bold)
        Text("Transparência total sobre o tratamento dos seus dados, conforme a Lei nº 13.709/2018 (LGPD).",color=MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(16.dp))
        listOf(
            "1. Dados que coletamos" to "Coletamos dados de cadastro (nome, e-mail e empresa), dados operacionais lançados no ERP (ordens de serviço, estoque, ativos e logs de auditoria) e dados técnicos de uso da plataforma necessários para segurança e suporte.",
            "2. Finalidade do tratamento" to "Os dados são utilizados exclusivamente para operar os módulos de manutenção, estoque e relatórios, prestar suporte técnico, cumprir obrigações legais e gerar indicadores de desempenho da sua operação industrial.",
            "3. Armazenamento e segurança" to "Nesta versão do aplicativo, os registros ficam armazenados localmente no dispositivo do usuário. Nenhum dado é enviado a terceiros sem sua autorização, exceto as mensagens enviadas ao assistente virtual, processadas apenas para gerar a resposta técnica.",
            "4. Seus direitos como titular (LGPD)" to "Você pode a qualquer momento confirmar a existência de tratamento, acessar, corrigir, portar ou eliminar seus dados, além de revogar o consentimento. Os módulos do ERP permitem exportar e apagar registros diretamente pelo painel.",
            "5. Retenção e eliminação" to "Os logs de auditoria mantêm os 500 eventos mais recentes. Ao limpar uma coleção no ERP, os dados correspondentes são eliminados de forma definitiva do dispositivo.",
            "6. Encarregado de dados (DPO)" to "Dúvidas ou solicitações relacionadas à LGPD podem ser enviadas pela página de contato, com resposta em até 15 dias úteis."
        ).forEach{(t,d)-> GeCard(Modifier.fillMaxWidth().padding(vertical=5.dp)){ Column(Modifier.padding(16.dp)){ Text(t,fontWeight=FontWeight.Bold); Spacer(Modifier.height(6.dp)); Text(d,style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant) } } }
        Spacer(Modifier.height(10.dp))
        Card(Modifier.fillMaxWidth(),border=BorderStroke(1.dp,MaterialTheme.colorScheme.primary.copy(alpha=.4f)),colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.primary.copy(alpha=.06f))){
            Column(Modifier.padding(18.dp)){
                Text("Consentimento",fontWeight=FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                if(aceito && dataAceite!=null){
                    Text("Consentimento registrado em $dataAceite.",style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(10.dp))
                    OutlinedButton(onClick={aceito=false;dataAceite=null}){Text("Revogar consentimento")}
                } else {
                    Text("Li e concordo com o tratamento dos meus dados nos termos descritos acima.",style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(10.dp))
                    Button(onClick={aceito=true;dataAceite=java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(java.util.Date())}){Text("Aceitar e continuar")}
                }
            }
        }
    }
}

private data class Post(val autor:String,val categoria:String,val titulo:String,val texto:String)
private val CATEGORIAS_BLOG = listOf("❓ Dúvida","📚 Tutorial","📝 Artigo","🚀 Notícia")
private val POSTS_FIXOS = listOf(
    Post("Equipe GeTech","📚 Tutorial","Como identificar desgaste em cilindros hidráulicos","Vazamento externo, queda de força e ruído de cavitação são os três sinais iniciais. Meça a velocidade de avanço sem carga e compare com a ficha do fabricante antes de abrir o cilindro."),
    Post("Engenharia de Manutenção","📝 Artigo","Preventiva x preditiva: onde investir primeiro","Comece pela preventiva nos ativos críticos e evolua para análise de vibração nos equipamentos com maior custo de parada. O ganho médio observado é de 18% em disponibilidade.")
)

@Composable
private fun BlogScreen(repo:GeTechRepository,nav:NavHostController){
    var aberto by remember{mutableStateOf(false)}
    var titulo by remember{mutableStateOf("")};var categoria by remember{mutableStateOf(CATEGORIAS_BLOG[0])};var texto by remember{mutableStateOf("")}
    val extras = remember{mutableStateListOf<Post>()}
    val autorAtual = repo.session()?.nome ?: "Visitante"
    Page(nav,"Comunidade Tech GeTech"){
        Text("Comunidade Tech GeTech",style=MaterialTheme.typography.headlineMedium,fontWeight=FontWeight.Bold)
        Text("Explore posts ou compartilhe seu conhecimento com a nossa fábrica.",color=MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(14.dp))
        Button(onClick={aberto=!aberto}){Text(if(aberto)"Fechar" else "✨ Criar publicação")}
        if(aberto){
            Spacer(Modifier.height(12.dp))
            GeCard(Modifier.fillMaxWidth()){
                Column(Modifier.padding(18.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){
                    Text("Criar nova publicação",fontWeight=FontWeight.Bold)
                    FormField("Título",titulo,{titulo=it})
                    Text("Categoria",style=MaterialTheme.typography.labelMedium)
                    Row(horizontalArrangement=Arrangement.spacedBy(6.dp)){
                        CATEGORIAS_BLOG.forEach{c-> FilterChip(selected=categoria==c,onClick={categoria=c},label={Text(c,style=MaterialTheme.typography.labelSmall)}) }
                    }
                    OutlinedTextField(texto,{texto=it},placeholder={Text("Escreva sua publicação")},modifier=Modifier.fillMaxWidth().height(100.dp))
                    Button(onClick={
                        if(titulo.isNotBlank()&&texto.isNotBlank()){extras.add(0,Post(autorAtual,categoria,titulo,texto));repo.log("BLOG","Publicação criada: $titulo");titulo="";texto="";categoria=CATEGORIAS_BLOG[0];aberto=false}
                    },modifier=Modifier.fillMaxWidth()){Text("Publicar no feed")}
                }
            }
        }
        Spacer(Modifier.height(20.dp))
        Text("🚀 Feed da comunidade",style=MaterialTheme.typography.titleLarge,fontWeight=FontWeight.Bold)
        Spacer(Modifier.height(10.dp))
        (extras+POSTS_FIXOS).forEach{p->
            GeCard(Modifier.fillMaxWidth().padding(vertical=5.dp)){
                Column(Modifier.padding(16.dp)){
                    Text(p.categoria,color=MaterialTheme.colorScheme.primary,fontWeight=FontWeight.Bold,style=MaterialTheme.typography.labelSmall)
                    Spacer(Modifier.height(4.dp));Text(p.titulo,fontWeight=FontWeight.Bold)
                    Spacer(Modifier.height(4.dp));Text(p.texto,style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(8.dp));Text("por ${p.autor}",style=MaterialTheme.typography.labelSmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
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
