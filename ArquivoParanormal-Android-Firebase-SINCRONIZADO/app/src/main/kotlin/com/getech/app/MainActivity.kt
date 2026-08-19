package com.getech.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.getech.app.data.LocalRepository
import com.getech.app.screens.*
import com.getech.app.ui.GeTechTheme

class MainActivity:ComponentActivity(){
    override fun onCreate(savedInstanceState:Bundle?){
        super.onCreate(savedInstanceState)
        val repo=LocalRepository(applicationContext)
        setContent{GeTechTheme{GeTechApp(repo)}}
    }
}

@Composable
fun GeTechApp(repo:LocalRepository){
    val nav=rememberNavController()
    var user by remember{mutableStateOf(repo.session())}
    val start=if(user==null)"home" else if(user!!.role=="gestor")"gestor" else "cliente"
    NavHost(navController=nav,startDestination=start){
        composable("home"){HomeScreen(
            onLogin={nav.navigate("login")},
            onPage={nav.navigate("public/$it")},
            onChat={nav.navigate(if(user==null)"login" else "chat")}
        )}
        composable("login"){LoginScreen(repo,{user=repo.session();nav.navigate(if(user!!.role=="gestor")"gestor" else "cliente"){popUpTo("home")}}, {nav.navigate("register")})}
        composable("register"){RegisterScreen(repo,{user=repo.session();nav.navigate("cliente"){popUpTo("home")}}, {nav.popBackStack()})}
        composable("cliente"){if(user?.role!="cliente"){nav.navigate("login")}else ClientDashboard(repo,{nav.navigate("chat")},{nav.navigate("ar")},{nav.navigate("public/$it")},{repo.logout();user=null;nav.navigate("home"){popUpTo("home")}},{nav.navigate("home")})}
        composable("gestor"){if(user?.role!="gestor"){nav.navigate("login")}else ManagerDashboard(repo,{nav.navigate("module/$it")},{repo.logout();user=null;nav.navigate("home"){popUpTo("home")}},{nav.navigate("home")})}
        composable("chat"){ChatbotScreen(repo){nav.popBackStack()}}
        composable("ar"){ARScreen{nav.popBackStack()}}
        composable("public/{key}",arguments=listOf(navArgument("key"){type=NavType.StringType})){backStack->
            val key=backStack.arguments?.getString("key") ?: "sobre"
            PublicPageScreen(publicPages[key] ?: publicPages["sobre"]!!){nav.popBackStack()}
        }
        composable("module/{key}",arguments=listOf(navArgument("key"){type=NavType.StringType})){backStack->
            val key=backStack.arguments?.getString("key") ?: "geral"
            ManagerModuleScreen(managerModules.firstOrNull{it.route==key} ?: managerModules.first(),repo){nav.popBackStack()}
        }
    }
}
