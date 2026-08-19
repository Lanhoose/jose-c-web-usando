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
import com.getech.app.ui.GeTechApp
import com.getech.app.ui.GeTechTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("GeTechCrash", "Crash em ${thread.name}", throwable)
            try {
                getSharedPreferences("getech_crash", MODE_PRIVATE)
                    .edit()
                    .putString(
                        "last_error",
                        throwable.toString() + "\n" +
                            throwable.stackTrace.joinToString("\n")
                    )
                    .apply()
            } catch (_: Throwable) {
                // Nunca deixar o próprio logger causar outro crash.
            }
        }

        val repository = try {
            LocalRepository(applicationContext).also {
                it.ensureDemoUsers()
                it.seedOperationalData()
            }
        } catch (t: Throwable) {
            Log.e("GeTechStartup", "Falha na inicialização", t)
            null
        }

        setContent {
            var darkTheme by remember { mutableStateOf(repository?.isDarkTheme() ?: true) }
            GeTechTheme(darkTheme = darkTheme) {
                if (repository == null) {
                    StartupErrorScreen(
                        message = "Não foi possível inicializar os dados locais.",
                        onReset = { recoverLocalData() }
                    )
                } else {
                    GeTechRoot(
                        repository,
                        darkTheme = darkTheme,
                        onThemeChange = {
                            darkTheme = it
                            repository.setDarkTheme(it)
                        }
                    )
                }
            }
        }
    }

    private fun recoverLocalData() {
        try {
            val fresh = LocalRepository(applicationContext)
            fresh.resetBrokenLocalData()
            recreate()
        } catch (e: Throwable) {
            Log.e("GeTechRecovery", "Falha ao recuperar dados locais", e)
            Toast.makeText(
                this,
                "Não foi possível recuperar os dados locais.",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}

@Composable
private fun GeTechRoot(
    repository: LocalRepository,
    darkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    GeTechApp(repository, darkTheme = darkTheme, onThemeChange = onThemeChange)
}

@Composable
private fun StartupErrorScreen(
    message: String,
    onReset: (() -> Unit)? = null
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "GeTech não conseguiu abrir",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(Modifier.height(12.dp))
            Text(
                "O aplicativo encontrou um problema nos dados locais.",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(Modifier.height(8.dp))
            Text(message, style = MaterialTheme.typography.bodySmall)

            if (onReset != null) {
                Spacer(Modifier.height(18.dp))
                Button(onClick = onReset) {
                    Text("Recuperar dados locais")
                }
            }
        }
    }
}
