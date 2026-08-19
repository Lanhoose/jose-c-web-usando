
package com.getech.app.ui

import androidx.compose.runtime.*
import com.getech.app.data.LocalRepository
import com.getech.app.screens.*

private sealed class Route {
    data object Home:Route()
    data object Login:Route()
    data object Register:Route()
    data object Client:Route()
    data object PublicPortal:Route()
    data object Manager:Route()
    data object Profile:Route()
    data class HomePage(val key:String):Route()
    data object Chatbot:Route()
    data object AR:Route()
    data class Public(val key:String):Route()
    data class ManagerModule(val key:String):Route()
}

private fun initialRoute(repo: LocalRepository): Route = when (repo.session()?.role?.lowercase()) {
    "gestor" -> Route.PublicPortal
    "cliente" -> Route.Client
    else -> Route.Home
}

@Composable
fun GeTechApp(
    repo:LocalRepository,
    darkTheme:Boolean = true,
    onThemeChange:(Boolean)->Unit = {}
) {
    var route by remember { mutableStateOf<Route>(initialRoute(repo)) }

    fun routeAfterLogin(): Route {
        val role = repo.session()?.role?.lowercase()
        return if (role == "gestor") {
            Route.PublicPortal
        } else if (role == "cliente") {
            Route.Client
        } else {
            Route.Home
        }
    }

    fun goAfterLogin() {
        route = routeAfterLogin()
    }

    when(val r=route) {
        Route.Home -> HomeScreen(
            onLogin={ if(repo.session()==null) route=Route.Login else goAfterLogin() },
            onPage={ key ->
                route = Route.HomePage(key)
            },
            onChat={if(repo.session()?.role=="cliente") route=Route.Chatbot else route=Route.Login},
            onThemeToggle={onThemeChange(!darkTheme)}
        )
        is Route.HomePage -> {
            val page = publicPages[r.key]
            if (r.key == "contato") {
                ContactScreen(repo) { route = Route.Home }
            } else if (page == null) {
                route = Route.Home
            } else {
                PublicPageScreen(page, { route = Route.Home })
            }
        }
        Route.Login -> LoginScreen(repo,{goAfterLogin()},{route=Route.Register})
        Route.Register -> RegisterScreen(repo,{route=Route.Login},{route=Route.Login})
        Route.Client -> {
            if(repo.session()?.role?.lowercase()!="cliente") route=Route.Home
            else ClientDashboard(repo,{route=Route.Chatbot},{route=Route.AR},
                {repo.clearSession();route=Route.Home},{route=Route.Home},{onThemeChange(!darkTheme)},{route=Route.Profile})
        }
        Route.PublicPortal -> {
            if (repo.session()?.role?.lowercase() != "gestor") {
                route = Route.Home
            } else {
                PublicPortalScreen(
                    onOpenApp = { route = Route.Manager },
                    onPage = { route = Route.Public(it) },
                    onProfile = { route = Route.Profile },
                    onLogout = { repo.clearSession(); route = Route.Home },
                    onBack = { route = Route.Home },
                    onTheme = { onThemeChange(!darkTheme) }
                )
            }
        }
        Route.Profile -> {
            if (repo.session() == null) route = Route.Login
            else ProfileScreen(repo) { route = if (repo.session()?.role?.lowercase() == "gestor") Route.PublicPortal else Route.Client }
        }
        Route.Manager -> {
            if(repo.session()?.role?.lowercase()!="gestor") route=Route.Home
            else ManagerDashboard(repo,{route=Route.ManagerModule(it)},{repo.clearSession();route=Route.Home},
                {route=Route.PublicPortal},{onThemeChange(!darkTheme)})
        }
        Route.Chatbot -> if(repo.session()?.role?.lowercase()!="cliente") route=Route.Login
            else ChatbotScreen(repo,{route=Route.Client})
        Route.AR -> if(repo.session()?.role?.lowercase()!="cliente") route=Route.Login
            else ARScreen{route=Route.Client}
        is Route.Public -> {
            if (repo.session()?.role?.lowercase() != "gestor") {
                route = Route.Home
            } else {
                val page = publicPages[r.key]
                if (r.key == "contato") ContactScreen(repo) { route = Route.PublicPortal }
                else if (page == null) route = Route.PublicPortal
                else PublicPageScreen(page, { route = Route.PublicPortal })
            }
        }
        is Route.ManagerModule -> {
            if(repo.session()?.role?.lowercase()!="gestor") route=Route.Home
            else {
                val module=managerModules.firstOrNull{it.route==r.key}
                if(module==null) route=Route.Manager
                else ManagerModuleScreen(module,repo,{route=Route.Manager})
            }
        }
    }
}
