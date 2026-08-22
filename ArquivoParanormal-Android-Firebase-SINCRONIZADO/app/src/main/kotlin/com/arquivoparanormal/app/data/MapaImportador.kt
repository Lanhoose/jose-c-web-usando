package com.arquivoparanormal.app.data

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.util.Base64
import android.os.ParcelFileDescriptor
import android.provider.OpenableColumns
import java.io.File
import java.io.FileOutputStream

/** Maior lado (em px) que um mapa importado pode ter, para não pesar no armazenamento do aparelho. */
private const val LADO_MAXIMO = 2200

/**
 * Importa um mapa (imagem PNG/JPG ou PDF) escolhido pelo usuário via seletor de arquivos do sistema,
 * salvando-o como PNG no armazenamento interno do app. Assim a Mesa de Batalha funciona 100% offline,
 * sem depender de permissão de armazenamento nem manter o arquivo original aberto.
 */
object MapaImportador {

    fun importar(context: Context, uri: Uri, tipoMime: String?): String? {
        val bitmap = if (tipoMime == "application/pdf") {
            renderizarPrimeiraPaginaPdf(context, uri)
        } else {
            decodificarImagem(context, uri)
        } ?: return null

        val pasta = File(context.filesDir, "mapas").apply { mkdirs() }
        pasta.listFiles()?.forEach { it.delete() } // mantém só o mapa mais recente

        val destino = File(pasta, "mapa_${System.currentTimeMillis()}.png")
        FileOutputStream(destino).use { saida -> bitmap.compress(Bitmap.CompressFormat.PNG, 100, saida) }
        bitmap.recycle()
        return destino.absolutePath
    }

    fun nomeDoArquivo(context: Context, uri: Uri): String? {
        var nome: String? = null
        val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val indice = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (indice >= 0 && it.moveToFirst()) nome = it.getString(indice)
        }
        return nome
    }

    fun removerMapas(context: Context) {
        File(context.filesDir, "mapas").listFiles()?.forEach { it.delete() }
    }

    /**
     * Recebe a imagem que o site publica no tacticalPayload como data URL e
     * salva os bytes originais no armazenamento interno. Diferente da
     * importação local, aqui não redimensionamos a imagem: o objetivo é que
     * o Android receba o mesmo mapa que está no computador/site.
     *
     * BUG CORRIGIDO: antes o arquivo era sempre salvo com extensão genérica
     * ".img". O Repositorio decide o Content-Type da data URL só pela
     * extensão do arquivo (jpg/webp/senão png) quando republica o mapa (por
     * exemplo depois que o Mestre move um token). Um mapa recebido como JPEG
     * ficava então republicado como "image/png" mesmo contendo bytes JPEG,
     * o que podia quebrar a exibição do mapa de volta no site. Agora a
     * extensão é derivada do cabeçalho real da data URL (image/jpeg,
     * image/webp, image/png etc.), então o mime detectado depois bate com o
     * conteúdo de verdade do arquivo.
     */
    fun salvarDataUrl(context: Context, dataUrl: String, nomeBase: String = "mapa_remoto"): String? {
        if (!dataUrl.startsWith("data:image/", ignoreCase = true)) return null
        val separador = dataUrl.indexOf(',')
        if (separador <= 0) return null
        val cabecalho = dataUrl.substring(5, separador) // ex.: "image/jpeg;base64"
        val mime = cabecalho.substringBefore(';').trim().lowercase()
        val extensao = when (mime) {
            "image/jpeg", "image/jpg" -> "jpg"
            "image/webp" -> "webp"
            else -> "png"
        }
        val dados = dataUrl.substring(separador + 1)
        // Rejeita payloads remotos absurdamente grandes antes de alocar um
        // ByteArray potencialmente enorme. A publicação normal da Mesa usa
        // um limite de ~700 KB, então 8 MB já é uma margem generosa para
        // compatibilidade com versões antigas do site.
        if (dados.length > 11_000_000) return null
        val bytes = runCatching { Base64.decode(dados, Base64.DEFAULT) }.getOrNull() ?: return null
        if (bytes.isEmpty()) return null

        val pasta = File(context.filesDir, "mapas").apply { mkdirs() }
        val destino = File(pasta, "${nomeBase}_${System.currentTimeMillis()}.$extensao")
        return runCatching {
            pasta.listFiles()?.forEach { it.delete() }
            destino.writeBytes(bytes)
            destino.absolutePath
        }.getOrNull()
    }

    private fun decodificarImagem(context: Context, uri: Uri): Bitmap? {
        val resolver = context.contentResolver
        val limites = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        val fluxoLimites = resolver.openInputStream(uri) ?: return null
        fluxoLimites.use { BitmapFactory.decodeStream(it, null, limites) }

        val opcoes = BitmapFactory.Options().apply {
            inSampleSize = calcularAmostra(limites.outWidth, limites.outHeight)
        }
        val fluxoFinal = resolver.openInputStream(uri) ?: return null
        return fluxoFinal.use { BitmapFactory.decodeStream(it, null, opcoes) }
    }

    private fun renderizarPrimeiraPaginaPdf(context: Context, uri: Uri): Bitmap? {
        val descritor: ParcelFileDescriptor =
            context.contentResolver.openFileDescriptor(uri, "r") ?: return null
        descritor.use { pfd ->
            PdfRenderer(pfd).use { renderizador ->
                if (renderizador.pageCount <= 0) return null
                renderizador.openPage(0).use { pagina ->
                    val escala = (LADO_MAXIMO.toFloat() / maxOf(pagina.width, pagina.height)).coerceAtMost(4f)
                    val largura = (pagina.width * escala).toInt().coerceAtLeast(1)
                    val altura = (pagina.height * escala).toInt().coerceAtLeast(1)
                    val bitmap = Bitmap.createBitmap(largura, altura, Bitmap.Config.ARGB_8888)
                    bitmap.eraseColor(android.graphics.Color.WHITE)
                    pagina.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    return bitmap
                }
            }
        }
    }

    private fun calcularAmostra(largura: Int, altura: Int): Int {
        var amostra = 1
        while (largura / amostra > LADO_MAXIMO * 2 || altura / amostra > LADO_MAXIMO * 2) {
            amostra *= 2
        }
        return amostra
    }
}
