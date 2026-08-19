package com.getech.app

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.getech.app.data.LocalRepository
import com.getech.app.ui.GeTechTheme
import com.getech.app.ui.GeTechApp
import java.io.PrintWriter
import java.io.StringWriter

class MainActivity : ComponentActivity() {
    private lateinit var repository: LocalRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("GeTechCrash", "Crash em ${thread.name}", throwable)
            try {
                getSharedPreferences("getech_crash", MODE_PRIVATE)
                    .edit()
                    .putString("last_error", throwable.toString() + "\n" + throwable.stackTrace.joinToString("\n"))
                    .apply()
            } catch (_: Throwable) {}
            // Não tentar reiniciar Activity automaticamente: evita loop infinito de crash.
        }

        try {
            repository = LocalRepository(applicationContext)
            repository.ensureDemoUsers()

            setContent {
                GeTechTheme {
                    SafeRoot(repository)
                }
            }
        } catch (t: Throwable) {
            Log.e("GeTechStartup", "Falha na inicialização", t)
            setContent {
                GeTechTheme {
                    StartupErrorScreen(
                        message = t.message ?: "Erro desconhecido",
                        onReset = {
                            try {
                                repository = LocalRepository(applicationContext)
                                repository.resetBrokenLocalData()
                                recreate()
                            } catch (e: Throwable) {
                                Toast.makeText(
                                    this,
                                    "Não foi possível recuperar os dados locais.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SafeRoot(repository: LocalRepository) {
    var error by remember { mutableStateOf<String?>(null) }

    if (error == null) {
        try {
            GeTechApp(repository)
        } catch (t: Throwable) {
            Log.e("GeTechUI", "Erro de UI", t)
            error = t.message ?: "Erro ao abrir a interface."
        }
    } else {
        StartupErrorScreen(message = error!!)
    }
}

@Composable
private fun StartupErrorScreen(message: String, onReset: (() -> Unit)? = null) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("GeTech não conseguiu abrir", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(12.dp))
            Text(
                "O aplicativo encontrou um erro e não foi encerrado automaticamente.",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(Modifier.height(8.dp))
            Text(message, style = MaterialTheme.typography.bodySmall)
            if (onReset != null) {
                Spacer(Modifier.height(18.dp))
                Button(onClick = onReset) { Text("Recuperar dados locais") }
            }
        }
    }
}
