package com.arquivoparanormal.app.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.view.ViewGroup
import android.widget.ImageView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.arquivoparanormal.app.data.LivroPdf
import com.arquivoparanormal.app.ui.Fundo
import com.arquivoparanormal.app.ui.Superficie
import com.arquivoparanormal.app.ui.TextoClaro
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

private sealed interface EstadoDownloadPdf {
    data object Verificando : EstadoDownloadPdf
    data class Baixando(val progresso: Float?) : EstadoDownloadPdf
    data object Pronto : EstadoDownloadPdf
    data class Erro(val mensagem: String) : EstadoDownloadPdf
}

private fun arquivoLocalDoPdf(context: Context, livro: LivroPdf): File =
    File(context.filesDir, livro.nomeArquivo)

private fun pdfValido(file: File): Boolean {
    if (!file.exists() || file.length() < 5) return false
    return runCatching {
        file.inputStream().use { input ->
            val cabecalho = ByteArray(5)
            input.read(cabecalho) == 5 && cabecalho.contentEquals(byteArrayOf(37, 80, 68, 70, 45))
        }
    }.getOrDefault(false)
}

/**
 * Baixa um dos livros do Google Drive e mantém uma cópia no armazenamento interno.
 * O mesmo mecanismo é usado para o Livro de Regras, Sobrevivendo ao Horror e
 * Arquivos Secretos, sem aumentar o tamanho do APK.
 */
private suspend fun baixarLivroPdf(
    context: Context,
    livro: LivroPdf,
    onProgresso: (Float?) -> Unit,
) = withContext(Dispatchers.IO) {
    val destino = arquivoLocalDoPdf(context, livro)
    val temporario = File(context.filesDir, "${livro.nomeArquivo}.tmp")

    var urlAtual = "https://drive.usercontent.google.com/download" +
        "?id=${livro.driveFileId}&export=download&confirm=t"
    var conexao: HttpURLConnection? = null

    try {
        // Antes o loop usava repeat(6) com return@repeat: como repeat() não tem "break",
        // ele continuava chamando o Google Drive até 6 vezes mesmo depois de já ter uma
        // resposta final, deixando conexões antigas penduradas sem disconnect(). Agora o
        // loop para assim que uma resposta não-redirecionamento chega.
        var tentativas = 0
        while (tentativas < 6 && conexao == null) {
            tentativas++
            val url = URL(urlAtual)
            val c = url.openConnection() as HttpURLConnection
            c.instanceFollowRedirects = false
            c.connectTimeout = 30_000
            c.readTimeout = 30_000
            c.setRequestProperty("User-Agent", "ArquivoParanormal/1.0")
            c.connect()

            val codigo = c.responseCode
            if (codigo in 300..399) {
                val proximo = c.getHeaderField("Location")
                c.disconnect()
                if (proximo != null) {
                    urlAtual = proximo
                    continue
                }
            }
            conexao = c
        }

        val c = conexao ?: throw IllegalStateException("Não foi possível conectar ao Google Drive.")
        if (c.responseCode != HttpURLConnection.HTTP_OK) {
            throw IllegalStateException("O Google Drive retornou o código ${c.responseCode}.")
        }

        val tamanhoTotal = c.contentLengthLong.takeIf { it > 0 }
        c.inputStream.use { input ->
            temporario.outputStream().use { output ->
                val buffer = ByteArray(64 * 1024)
                var totalLido = 0L
                var lidos: Int
                while (input.read(buffer).also { lidos = it } != -1) {
                    output.write(buffer, 0, lidos)
                    totalLido += lidos
                    onProgresso(tamanhoTotal?.let { totalLido.toFloat() / it.toFloat() })
                }
            }
        }
        c.disconnect()

        if (!pdfValido(temporario)) throw IllegalStateException("O arquivo baixado não é um PDF válido.")
        if (!temporario.renameTo(destino)) {
            temporario.copyTo(destino, overwrite = true)
            temporario.delete()
        }
        if (!pdfValido(destino)) {
            destino.delete()
            throw IllegalStateException("O PDF baixado está corrompido.")
        }
    } catch (e: Exception) {
        temporario.delete()
        throw e
    }
}

@Composable
fun LivroPdfScreen(
    livro: LivroPdf,
    paginaInicial: Int,
) {
    val context = LocalContext.current
    var paginaAtual by remember(livro, paginaInicial) {
        mutableIntStateOf(paginaInicial.coerceIn(1, livro.ultimaPagina))
    }
    val pdfView = remember(livro) { PdfPageImageViewHolder(livro) }
    val arquivo = remember(livro) { arquivoLocalDoPdf(context, livro) }

    var estado by remember(livro) {
        mutableStateOf<EstadoDownloadPdf>(
            if (pdfValido(arquivo)) EstadoDownloadPdf.Pronto
            else EstadoDownloadPdf.Verificando,
        )
    }
    var tentativa by remember(livro) { mutableIntStateOf(0) }

    LaunchedEffect(livro, tentativa) {
        if (pdfValido(arquivo)) {
            estado = EstadoDownloadPdf.Pronto
            return@LaunchedEffect
        }

        estado = EstadoDownloadPdf.Baixando(null)
        try {
            baixarLivroPdf(context, livro) { progresso ->
                estado = EstadoDownloadPdf.Baixando(progresso)
            }
            estado = EstadoDownloadPdf.Pronto
        } catch (e: Exception) {
            estado = EstadoDownloadPdf.Erro(e.message ?: "Falha ao baixar o PDF.")
        }
    }

    LaunchedEffect(livro, paginaAtual, estado) {
        if (estado is EstadoDownloadPdf.Pronto) {
            pdfView.requestPage(paginaAtual)
        }
    }

    DisposableEffect(livro) {
        onDispose { pdfView.close() }
    }

    Box(Modifier.fillMaxSize().background(Fundo)) {
        when (val estadoAtual = estado) {
            EstadoDownloadPdf.Verificando -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = TextoClaro)
                }
            }

            is EstadoDownloadPdf.Baixando -> {
                Column(
                    Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        "Baixando ${livro.titulo}…",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextoClaro,
                    )
                    val progresso = estadoAtual.progresso
                    if (progresso != null) {
                        LinearProgressIndicator(
                            progress = { progresso },
                            modifier = Modifier.width(260.dp).padding(top = 16.dp),
                        )
                        Text(
                            "${(progresso * 100).toInt()}%",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextoClaro,
                            modifier = Modifier.padding(top = 8.dp),
                        )
                    } else {
                        LinearProgressIndicator(modifier = Modifier.width(260.dp).padding(top = 16.dp))
                    }
                    Text(
                        "O download acontece apenas uma vez. Mantenha o app aberto e conectado à internet.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextoClaro,
                        modifier = Modifier.padding(top = 16.dp),
                    )
                }
            }

            is EstadoDownloadPdf.Erro -> {
                Column(
                    Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text("Não foi possível baixar ${livro.titulo}.", style = MaterialTheme.typography.titleMedium, color = TextoClaro)
                    Text(
                        estadoAtual.mensagem,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextoClaro,
                        modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
                    )
                    Button(onClick = { tentativa++ }) { Text("Tentar novamente") }
                }
            }

            EstadoDownloadPdf.Pronto -> {
                AndroidView(
                    modifier = Modifier.fillMaxSize().padding(bottom = 66.dp),
                    factory = { ctx -> pdfView.createView(ctx) },
                )
            }
        }

        if (estado is EstadoDownloadPdf.Pronto) {
            Row(
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Superficie)
                    .navigationBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FilledIconButton(enabled = paginaAtual > 1, onClick = { paginaAtual-- }) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "Página anterior")
                }
                Text("${livro.titulo} • pág. $paginaAtual", style = MaterialTheme.typography.titleSmall, color = TextoClaro)
                FilledIconButton(enabled = paginaAtual < livro.ultimaPagina, onClick = { paginaAtual++ }) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "Próxima página")
                }
            }
        }
    }
}

private class PdfPageImageViewHolder(
    private val livro: LivroPdf,
) {
    private var renderer: PdfRenderer? = null
    private var descriptor: ParcelFileDescriptor? = null
    private var imageView: ImageView? = null
    private var pendingPrintedPage: Int? = null
    private var context: Context? = null
    private var closed = false

    private val generation = AtomicInteger(0)
    private val executor: ExecutorService = Executors.newSingleThreadExecutor { runnable ->
        Thread(runnable, "ArquivoParanormal-PDF").apply { isDaemon = true }
    }

    private val pageCache = object : android.util.LruCache<String, Bitmap>(12 * 1024) {
        override fun sizeOf(key: String, value: Bitmap): Int = (value.byteCount / 1024).coerceAtLeast(1)
    }

    fun createView(context: Context): ImageView {
        this.context = context.applicationContext
        return ImageView(context).also { view ->
            view.setBackgroundColor(Color.TRANSPARENT)
            view.scaleType = ImageView.ScaleType.FIT_CENTER
            view.adjustViewBounds = true
            view.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
            imageView = view
            pendingPrintedPage?.let { requestPage(it) }
        }
    }

    fun requestPage(printedPage: Int) {
        if (closed) return
        pendingPrintedPage = printedPage.coerceIn(1, livro.ultimaPagina)
        val request = generation.incrementAndGet()
        executor.execute { renderPrintedPage(pendingPrintedPage ?: 1, request) }
    }

    private fun ensureRenderer(): PdfRenderer? {
        if (renderer != null) return renderer
        val ctx = context ?: return null
        return runCatching {
            val file = arquivoLocalDoPdf(ctx, livro)
            val novoDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            descriptor = novoDescriptor
            PdfRenderer(novoDescriptor).also { renderer = it }
        }.getOrNull()
    }

    private fun renderPrintedPage(printedPage: Int, request: Int) {
        val pdf = ensureRenderer() ?: return
        val view = imageView ?: return

        val physicalIndex = (printedPage + livro.offsetFisicoZeroBased)
            .coerceIn(0, pdf.pageCount - 1)
        val width = view.width.takeIf { it > 0 } ?: 1200
        val key = "$physicalIndex@$width"

        synchronized(pageCache) {
            pageCache.get(key)?.let { bitmap ->
                publicarSeAtual(bitmap, request)
                return
            }
        }

        val page = runCatching { pdf.openPage(physicalIndex) }.getOrNull() ?: return
        try {
            val ratio = page.height.toFloat() / page.width.toFloat()
            val height = (width * ratio).toInt().coerceAtLeast(1)
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

            if (closed || request != generation.get()) {
                bitmap.recycle()
                return
            }

            synchronized(pageCache) { pageCache.put(key, bitmap) }
            publicarSeAtual(bitmap, request)
        } catch (_: Exception) {
            // A navegação pode ter mudado de página antes da renderização terminar.
        } finally {
            page.close()
        }
    }

    private fun publicarSeAtual(bitmap: Bitmap, request: Int) {
        val view = imageView ?: return
        if (closed || request != generation.get()) return
        view.post {
            if (!closed && request == generation.get()) view.setImageBitmap(bitmap)
        }
    }

    fun close() {
        if (closed) return
        closed = true
        generation.incrementAndGet()
        executor.shutdownNow()
        synchronized(pageCache) { pageCache.evictAll() }
        runCatching { renderer?.close() }
        runCatching { descriptor?.close() }
        renderer = null
        descriptor = null
        imageView = null
        context = null
    }
}
