package com.getech.app.ui

import androidx.compose.runtime.*
import com.getech.app.data.LocalRepository
import com.getech.app.screens.*

private sealed class Route {
    data object Home : Route()
    data object Login : Route()
    data object Register : Route()
    data object Client : Route()
    data object Manager : Route()
    data object Chatbot : Route()
    data object AR : Route()
    data class Public(val key: String) : Route()
    data class ManagerModule(val key: String) : Route()
}

@Composable
fun GeTechApp(repo: LocalRepository) {
    var route by remember { mutableStateOf<Route>(Route.Home) }

    fun goAfterLogin() {
        route = when (repo.session()?.role?.lowercase()) {
            "gestor" -> Route.Manager
            "cliente" -> Route.Client
            else -> Route.Home
        }
    }

    when (val r = route) {
        Route.Home -> HomeScreen(
            onLogin = {
                if (repo.session() == null) route = Route.Login else goAfterLogin()
            },
            onPage = { route = Route.Public(it) },
            onChat = {
                route = if (repo.session()?.role == "cliente") Route.Chatbot else Route.Login
            }
        )

        Route.Login -> LoginScreen(
            repo = repo,
            onLogged = { goAfterLogin() },
            onRegister = { route = Route.Register }
        )

        Route.Register -> RegisterScreen(
            repo = repo,
            onRegistered = { route = Route.Login },
            onBack = { route = Route.Login }
        )

        Route.Client -> {
            if (repo.session()?.role?.lowercase() != "cliente") {
                route = Route.Home
            } else {
                ClientDashboard(
                    repo = repo,
                    onChat = { route = Route.Chatbot },
                    onAR = { route = Route.AR },
                    onPublic = { route = Route.Public(it) },
                    onLogout = { repo.clearSession(); route = Route.Home },
                    onBack = { route = Route.Home }
                )
            }
        }

        Route.Manager -> {
            if (repo.session()?.role?.lowercase() != "gestor") {
                route = Route.Home
            } else {
                ManagerDashboard(
                    repo = repo,
                    onModule = { route = Route.ManagerModule(it) },
                    onLogout = { repo.clearSession(); route = Route.Home },
                    onBack = { route = Route.Home }
                )
            }
        }

        Route.Chatbot -> {
            if (repo.session()?.role?.lowercase() != "cliente") {
                route = Route.Login
            } else {
                ChatbotScreen(repo = repo, onBack = { route = Route.Client })
            }
        }

        Route.AR -> {
            if (repo.session()?.role?.lowercase() != "cliente") {
                route = Route.Login
            } else {
                ARScreen(onBack = { route = Route.Client })
            }
        }

        is Route.Public -> {
            val page = publicPages[r.key]
            if (page == null) {
                route = Route.Home
            } else {
                PublicPageScreen(page = page, onBack = { route = Route.Home })
            }
        }

        is Route.ManagerModule -> {
            if (repo.session()?.role?.lowercase() != "gestor") {
                route = Route.Home
            } else {
                val module = managerModules.firstOrNull { it.route == r.key }
                if (module == null) route = Route.Manager
                else ManagerModuleScreen(module = module, repo = repo, onBack = { route = Route.Manager })
            }
        }
    }
}
