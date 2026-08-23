package com.arquivoparanormal.app.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Build
import android.os.ParcelFileDescriptor
import android.util.Log
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import kotlin.math.max

/**
 * Importa imagens escolhidas pelo seletor do Android ou a primeira página de um PDF.
 * O arquivo é sempre COPIADO para o armazenamento interno do aplicativo, então o
 * retrato continua funcionando mesmo depois que o usuário fecha o Drive/galeria,
 * remove o arquivo original ou fica sem internet.
 */
object ImagemImportador {

    private const val TAG = "ImagemImportador"
    private const val LADO_MAXIMO_RETRATO = 720
    private const val LADO_MAXIMO_DECODIFICACAO = 1440

    /** Tipos de imagem aceitos; também funciona com Google Drive, Fotos e gerenciadores. */
    val TIPOS_ACEITOS = arrayOf("image/*", "application/pdf")

    fun importar(
        context: Context,
        uri: Uri,
        tipoMime: String?,
        pasta: String = "retratos",
    ): String? {
        return runCatching {
            val mime = tipoMime?.lowercase().orEmpty()
            val nome = nomeDoArquivo(context, uri)?.lowercase().orEmpty()

            // Alguns provedores (principalmente nuvem) devolvem application/octet-stream
            // ou não informam o MIME. Por isso também verificamos a extensão e, para os
            // demais casos, tentamos decodificar como imagem em vez de rejeitar o arquivo.
            val ehPdf = mime == "application/pdf" || nome.endsWith(".pdf")

            val bitmap = if (ehPdf) {
                primeiraPaginaPdf(context, uri)
            } else {
                decodificarImagem(context, uri)
            } ?: return null

            val diretorio = File(context.filesDir, pasta).apply { mkdirs() }
            val destino = File(
                diretorio,
                "img_${System.currentTimeMillis()}_${novoId()}.png",
            )

            FileOutputStream(destino).use { saida ->
                check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, saida)) {
                    "Falha ao salvar a imagem"
                }
            }
            bitmap.recycle()
            destino.absolutePath
        }.onFailure { erro ->
            // Antes o erro era engolido em silêncio; agora fica visível no Logcat
            // com a tag "ImagemImportador" para facilitar o diagnóstico.
            Log.e(TAG, "Falha ao importar imagem: ${erro.message}", erro)
        }.getOrNull()
    }

    /** Gera uma miniatura Base64 pequena para sincronizar retratos entre aparelhos. */
    fun gerarMiniaturaDataUrl(caminhoArquivo: String, ladoMaximo: Int = 160, qualidade: Int = 72): String? {
        val original = BitmapFactory.decodeFile(caminhoArquivo) ?: return null
        return runCatching {
            val maior = max(original.width, original.height)
            val escala = ladoMaximo.toFloat() / maior.coerceAtLeast(1)
            val miniatura = if (escala < 1f) {
                Bitmap.createScaledBitmap(
                    original,
                    (original.width * escala).toInt().coerceAtLeast(1),
                    (original.height * escala).toInt().coerceAtLeast(1),
                    true,
                )
            } else original
            val saida = ByteArrayOutputStream()
            check(miniatura.compress(Bitmap.CompressFormat.JPEG, qualidade.coerceIn(40, 90), saida))
            if (miniatura !== original) miniatura.recycle()
            original.recycle()
            "data:image/jpeg;base64," + Base64.encodeToString(saida.toByteArray(), Base64.NO_WRAP)
        }.getOrElse {
            original.recycle()
            null
        }
    }

    fun remover(caminho: String?) {
        if (caminho.isNullOrBlank()) return
        runCatching { File(caminho).delete() }
    }

    private fun decodificarImagem(context: Context, uri: Uri): Bitmap? {
        // O BitmapFactory tem um bug antigo e conhecido do Android: ele falha
        // silenciosamente (retorna null) para JPEGs no formato CMYK, muito comuns
        // em imagens baixadas da internet (ex.: "images.jpeg" do Google Imagens).
        // O ImageDecoder (API 28+) não tem esse problema e também lida melhor com
        // WEBP animado, HEIC e orientação EXIF. Por isso ele é o caminho principal,
        // com o BitmapFactory como reserva para aparelhos mais antigos (minSdk 24)
        // ou caso o ImageDecoder também falhe por algum motivo.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val viaImageDecoder = runCatching {
                val fonte = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(fonte) { decoder, info, _ ->
                    decoder.isMutableRequired = true
                    val largura = info.size.width
                    val altura = info.size.height
                    if (largura > 0 && altura > 0) {
                        val amostra = calcularAmostra(largura, altura)
                        if (amostra > 1) {
                            decoder.setTargetSampleSize(amostra)
                        }
                    }
                }
            }.onFailure { erro ->
                Log.w(TAG, "ImageDecoder falhou, tentando BitmapFactory: ${erro.message}")
            }.getOrNull()

            if (viaImageDecoder != null) return reduzir(viaImageDecoder)
        }

        val resolver = context.contentResolver

        val limites = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        resolver.openInputStream(uri)?.use { fluxo ->
            BitmapFactory.decodeStream(fluxo, null, limites)
        } ?: return null

        if (limites.outWidth <= 0 || limites.outHeight <= 0) return null

        val opcoes = BitmapFactory.Options().apply {
            inSampleSize = calcularAmostra(limites.outWidth, limites.outHeight)
            inPreferredConfig = Bitmap.Config.ARGB_8888
            inScaled = false
        }

        val bitmap = resolver.openInputStream(uri)?.use { fluxo ->
            BitmapFactory.decodeStream(fluxo, null, opcoes)
        } ?: return null

        return reduzir(bitmap)
    }

    private fun primeiraPaginaPdf(context: Context, uri: Uri): Bitmap? {
        val descritor: ParcelFileDescriptor =
            context.contentResolver.openFileDescriptor(uri, "r") ?: return null

        descritor.use { pfd ->
            PdfRenderer(pfd).use { renderizador ->
                if (renderizador.pageCount <= 0) return null

                renderizador.openPage(0).use { pagina ->
                    val escala =
                        (LADO_MAXIMO_RETRATO.toFloat() / max(pagina.width, pagina.height))
                            .coerceAtMost(4f)
                    val largura = (pagina.width * escala).toInt().coerceAtLeast(1)
                    val altura = (pagina.height * escala).toInt().coerceAtLeast(1)
                    val bitmap = Bitmap.createBitmap(
                        largura,
                        altura,
                        Bitmap.Config.ARGB_8888,
                    )
                    bitmap.eraseColor(android.graphics.Color.WHITE)
                    pagina.render(
                        bitmap,
                        null,
                        null,
                        PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY,
                    )
                    return bitmap
                }
            }
        }
    }

    private fun reduzir(bitmap: Bitmap): Bitmap {
        val maior = max(bitmap.width, bitmap.height)
        if (maior <= LADO_MAXIMO_RETRATO) return bitmap

        val escala = LADO_MAXIMO_RETRATO.toFloat() / maior
        val novo = Bitmap.createScaledBitmap(
            bitmap,
            (bitmap.width * escala).toInt().coerceAtLeast(1),
            (bitmap.height * escala).toInt().coerceAtLeast(1),
            true,
        )
        if (novo !== bitmap) bitmap.recycle()
        return novo
    }

    private fun calcularAmostra(largura: Int, altura: Int): Int {
        var amostra = 1
        while (
            largura / amostra > LADO_MAXIMO_DECODIFICACAO ||
            altura / amostra > LADO_MAXIMO_DECODIFICACAO
        ) {
            amostra *= 2
        }
        return amostra.coerceAtLeast(1)
    }

    private fun nomeDoArquivo(context: Context, uri: Uri): String? {
        return runCatching {
            context.contentResolver.query(uri, arrayOf("_display_name"), null, null, null)
                ?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        cursor.getString(0)
                    } else null
                }
        }.getOrNull()
    }
}
