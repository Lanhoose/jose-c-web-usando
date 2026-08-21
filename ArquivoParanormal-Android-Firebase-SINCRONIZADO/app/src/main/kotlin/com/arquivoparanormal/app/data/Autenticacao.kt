package com.arquivoparanormal.app.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Base64
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.concurrent.Executors

/**
 * Autenticação centralizada no Firebase Authentication.
 * O papel é salvo em /usuarios/{uid}; novos cadastros começam como "jogador".
 * Para promover alguém a mestre, altere o campo role no Firestore Console.
 *
 * A foto de perfil NÃO usa Firebase Storage nem o campo photoUrl do Firebase
 * Auth (que espera uma URL http(s) e poderia rejeitar/descartar uma URI
 * local). O arquivo em resolução completa fica salvo localmente no
 * armazenamento interno do app (como os retratos de personagem) e seu
 * caminho é guardado em SharedPreferences por uid. Só uma miniatura pequena
 * (JPEG ~128px, em Base64) é sincronizada pelo Firestore em
 * /usuarios/{uid}.photoThumb, para que outros lugares que leem esse
 * documento consigam mostrar algo sem depender do Storage.
 */
class Autenticacao(context: Context) {

    private val appContext = context.applicationContext
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val prefs = appContext.getSharedPreferences("arquivo_paranormal_perfil", Context.MODE_PRIVATE)
    private val mainHandler = Handler(Looper.getMainLooper())
    private val fotoExecutor = Executors.newSingleThreadExecutor { runnable ->
        Thread(runnable, "ArquivoParanormal-Foto").apply { isDaemon = true }
    }

    /** URI local (file://) da foto de perfil do usuário logado, se houver. */
    val fotoLocalUri: Uri?
        get() {
            val uid = auth.currentUser?.uid ?: return null
            val caminho = prefs.getString("foto_$uid", null) ?: return null
            val arquivo = File(caminho)
            return if (arquivo.exists()) Uri.fromFile(arquivo) else null
        }

    val usuarioAtualUser get() = auth.currentUser

    val logado: Boolean get() = auth.currentUser != null
    val uid: String? get() = auth.currentUser?.uid
    val usuarioAtual: String? get() = auth.currentUser?.email

    fun papelAtual(aoResultado: (String) -> Unit) {
        val user = auth.currentUser ?: run {
            aoResultado("jogador")
            return
        }
        db.collection("usuarios").document(user.uid).get()
            .addOnSuccessListener { snap ->
                aoResultado(snap.getString("role") ?: "jogador")
            }
            .addOnFailureListener { aoResultado("jogador") }
    }

    fun criarConta(email: String, senha: String, aoResultado: (String?) -> Unit) {
        val e = email.trim()
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(e).matches()) {
            aoResultado("Informe um e-mail válido.")
            return
        }
        if (senha.length < 6) {
            aoResultado("A senha precisa de pelo menos 6 caracteres.")
            return
        }

        auth.createUserWithEmailAndPassword(e, senha)
            .addOnSuccessListener { result ->
                val user = result.user ?: run {
                    aoResultado("Não foi possível criar o usuário.")
                    return@addOnSuccessListener
                }
                db.collection("usuarios").document(user.uid).set(
                    mapOf(
                        "uid" to user.uid,
                        "email" to (user.email ?: e),
                        "role" to "jogador",
                        "criadoEm" to System.currentTimeMillis(),
                    ),
                ).addOnSuccessListener { aoResultado(null) }
                    .addOnFailureListener { ex ->
                        user.delete().addOnCompleteListener {
                            aoResultado(ex.message ?: "Não foi possível criar o perfil da conta.")
                        }
                    }
            }
            .addOnFailureListener { ex ->
                aoResultado(ex.message ?: "Não foi possível criar a conta.")
            }
    }

    fun entrar(email: String, senha: String, aoResultado: (String?) -> Unit) {
        val e = email.trim()
        auth.signInWithEmailAndPassword(e, senha)
            .addOnSuccessListener { aoResultado(null) }
            .addOnFailureListener { ex ->
                aoResultado(ex.message ?: "Usuário ou senha inválidos.")
            }
    }

    fun atualizarNome(nome: String, aoResultado: (String?) -> Unit) {
        val user = auth.currentUser ?: return aoResultado("Usuário não autenticado.")
        val nomeLimpo = nome.trim().take(40)
        if (nomeLimpo.isBlank()) return aoResultado("Informe um nome.")
        user.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(nomeLimpo).build())
            .addOnSuccessListener {
                db.collection("usuarios").document(user.uid).set(
                    mapOf("displayName" to nomeLimpo, "updatedAt" to System.currentTimeMillis()),
                    com.google.firebase.firestore.SetOptions.merge(),
                ).addOnSuccessListener { aoResultado(null) }
                    .addOnFailureListener { ex ->
                        aoResultado(ex.message ?: "O nome foi alterado no login, mas não foi possível sincronizar o perfil.")
                    }
            }
            .addOnFailureListener { aoResultado(it.message ?: "Não foi possível atualizar o nome.") }
    }

    /**
     * Salva a foto escolhida localmente (armazenamento interno do app,
     * caminho guardado em SharedPreferences por uid) e sincroniza apenas
     * uma miniatura pelo Firestore.
     */
    fun atualizarFoto(uri: Uri, aoResultado: (String?) -> Unit) {
        val user = auth.currentUser ?: return aoResultado("Usuário não autenticado.")
        val fotoAnterior = prefs.getString("foto_${user.uid}", null)

        fotoExecutor.execute {
            val tipoMime = runCatching { appContext.contentResolver.getType(uri) }.getOrNull()
            val caminhoLocal = ImagemImportador.importar(appContext, uri, tipoMime, pasta = "perfil")
            if (caminhoLocal == null) {
                mainHandler.post { aoResultado("Não foi possível processar a imagem escolhida.") }
                return@execute
            }

            val miniatura = runCatching { gerarMiniaturaBase64(caminhoLocal) }.getOrNull()

            prefs.edit().putString("foto_${user.uid}", caminhoLocal).apply()
            if (!fotoAnterior.isNullOrBlank() && fotoAnterior != caminhoLocal) {
                runCatching { File(fotoAnterior).delete() }
            }

            mainHandler.post {
                val dados = mutableMapOf<String, Any>(
                    "updatedAt" to System.currentTimeMillis(),
                )
                if (miniatura != null) dados["photoThumb"] = miniatura
                db.collection("usuarios").document(user.uid).set(
                    dados,
                    com.google.firebase.firestore.SetOptions.merge(),
                ).addOnCompleteListener {
                    // A foto local já foi salva e já está disponível mesmo
                    // que a sincronização da miniatura falhe (ex.: sem
                    // internet); só reportamos erro se ela de fato falhou.
                    val erroSync = it.exception?.message
                    aoResultado(erroSync)
                }
            }
        }
    }

    private fun gerarMiniaturaBase64(caminhoArquivo: String): String? {
        val original = BitmapFactory.decodeFile(caminhoArquivo) ?: return null
        val maior = maxOf(original.width, original.height)
        val escala = MINIATURA_LADO_MAXIMO.toFloat() / maior
        val miniatura = if (escala < 1f) {
            Bitmap.createScaledBitmap(
                original,
                (original.width * escala).toInt().coerceAtLeast(1),
                (original.height * escala).toInt().coerceAtLeast(1),
                true,
            )
        } else {
            original
        }
        val saida = ByteArrayOutputStream()
        miniatura.compress(Bitmap.CompressFormat.JPEG, 60, saida)
        if (miniatura !== original) miniatura.recycle()
        original.recycle()
        return "data:image/jpeg;base64," + Base64.encodeToString(saida.toByteArray(), Base64.NO_WRAP)
    }

    fun enviarRedefinicaoSenha(aoResultado: (String?) -> Unit) {
        val email = auth.currentUser?.email ?: return aoResultado("Nenhum e-mail disponível.")
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener { aoResultado(null) }
            .addOnFailureListener { aoResultado(it.message ?: "Não foi possível enviar o e-mail.") }
    }

    fun sair() = auth.signOut()

    private companion object {
        const val MINIATURA_LADO_MAXIMO = 128
    }
}
