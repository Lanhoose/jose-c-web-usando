package com.arquivoparanormal.app.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.LruCache
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.arquivoparanormal.app.data.ImagemImportador
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Cache pequeno e compartilhado de miniaturas. O antigo código decodificava
 * a foto inteira na thread principal a cada composição; imagens grandes podiam
 * congelar a lista por vários frames.
 */
private val imagemCache = object : LruCache<String, Bitmap>(12 * 1024) {
    override fun sizeOf(key: String, value: Bitmap): Int =
        (value.byteCount / 1024).coerceAtLeast(1)
}

private fun chaveImagem(caminho: String, maxDimensionPx: Int) =
    "$caminho#$maxDimensionPx"

private fun decodificarMiniatura(caminho: String, maxDimensionPx: Int): Bitmap? {
    val key = chaveImagem(caminho, maxDimensionPx)
    synchronized(imagemCache) {
        imagemCache.get(key)?.let { return it }
    }

    // O site publica a foto do token no Firebase dentro do tacticalPayload
    // como uma data URL (data:image/png;base64,...). O Android consegue
    // renderizar essa imagem diretamente, sem precisar de um arquivo local.
    val bitmap = if (caminho.startsWith("data:image/", ignoreCase = true)) {
        val separador = caminho.indexOf(',')
        if (separador <= 0) {
            null
        } else {
            val base64 = caminho.substring(separador + 1)
            runCatching {
                val bytes = Base64.decode(base64, Base64.DEFAULT)
                val limites = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size, limites)
                if (limites.outWidth <= 0 || limites.outHeight <= 0) return@runCatching null

                var sample = 1
                while (limites.outWidth / sample > maxDimensionPx ||
                    limites.outHeight / sample > maxDimensionPx
                ) {
                    sample *= 2
                }

                val options = BitmapFactory.Options().apply {
                    inSampleSize = sample.coerceAtLeast(1)
                    inPreferredConfig = Bitmap.Config.ARGB_8888
                    inScaled = false
                }
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
            }.getOrNull()
        }
    } else {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(caminho, bounds)
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null

        var sample = 1
        while (bounds.outWidth / sample > maxDimensionPx ||
            bounds.outHeight / sample > maxDimensionPx
        ) {
            sample *= 2
        }

        val options = BitmapFactory.Options().apply {
            inSampleSize = sample.coerceAtLeast(1)
            inPreferredConfig = Bitmap.Config.ARGB_8888
            inScaled = false
        }
        BitmapFactory.decodeFile(caminho, options)
    } ?: return null

    synchronized(imagemCache) {
        imagemCache.put(key, bitmap)
    }
    return bitmap
}

/** Carrega uma miniatura em background e a mantém em cache por caminho/tamanho. */
@Composable
fun lembrarImagemLocal(
    caminho: String?,
    maxDimensionPx: Int = 256,
): Bitmap? {
    return produceState<Bitmap?>(initialValue = null, caminho, maxDimensionPx) {
        value = if (caminho.isNullOrBlank()) {
            null
        } else {
            withContext(Dispatchers.IO) {
                runCatching {
                    decodificarMiniatura(caminho, maxDimensionPx.coerceAtLeast(64))
                }.getOrNull()
            }
        }
    }.value
}

/** Miniatura quadrada de um retrato salvo; mostra um placeholder quando não há imagem. */
@Composable
fun Retrato(
    caminho: String?,
    modifier: Modifier = Modifier,
    tamanho: Dp = 44.dp,
    forma: Shape = RoundedCornerShape(3.dp),
    vazio: @Composable () -> Unit = {
        Icon(Icons.Default.Image, contentDescription = null, tint = TextoFraco)
    },
) {
    val density = LocalDensity.current
    val maxDimensionPx = remember(tamanho, density) {
        with(density) { max(tamanho.roundToPx(), 64) }
    }
    val imagem = lembrarImagemLocal(caminho, maxDimensionPx)
    val imageBitmap = remember(imagem) { imagem?.asImageBitmap() }

    Box(
        modifier
            .size(tamanho)
            .clip(forma)
            .border(1.dp, Borda, forma)
            .background(SuperficieAlta, forma),
        contentAlignment = Alignment.Center,
    ) {
        if (imageBitmap != null) {
            Image(
                bitmap = imageBitmap,
                contentDescription = "Retrato",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        } else {
            vazio()
        }
    }
}

/**
 * Seletor de imagem (formatos de imagem ou 1ª página de PDF) com pré-visualização.
 * A imagem é copiada para o armazenamento interno do app e o caminho é devolvido em [aoDefinir].
 */
@Composable
fun SeletorImagem(
    caminho: String?,
    aoDefinir: (String?) -> Unit,
    modifier: Modifier = Modifier,
    rotuloVazio: String = "Escolher imagem (PNG, JPG ou PDF)",
    tamanhoPreview: Dp = 64.dp,
    pasta: String = "retratos",
) {
    val contexto = LocalContext.current
    val scope = rememberCoroutineScope()
    var erro by remember { mutableStateOf<String?>(null) }
    var carregando by remember { mutableStateOf(false) }

    val seletor = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        erro = null
        carregando = true

        scope.launch(Dispatchers.IO) {
            val resultado = runCatching {
                val tipo = contexto.contentResolver.getType(uri)
                ImagemImportador.importar(contexto, uri, tipo, pasta)?.let {
                    null to it
                } ?: ("Não foi possível carregar a imagem. Escolha PNG, JPG, WEBP, HEIC ou PDF." to null)
            }.getOrElse {
                "Não foi possível carregar esse arquivo." to null
            }

            withContext(Dispatchers.Main.immediate) {
                val (mensagem, novo) = resultado
                erro = mensagem
                if (novo != null) {
                    ImagemImportador.remover(caminho)
                    aoDefinir(novo)
                }
                carregando = false
            }
        }
    }

    Column(modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Retrato(
                caminho,
                tamanho = tamanhoPreview,
                modifier = Modifier.clickable { seletor.launch(ImagemImportador.TIPOS_ACEITOS) },
            )
            Column(Modifier.padding(end = 4.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                OutlinedButton(
                    onClick = { seletor.launch(ImagemImportador.TIPOS_ACEITOS) },
                    shape = RoundedCornerShape(3.dp),
                    border = BorderStroke(1.dp, Borda),
                ) {
                    Text(
                        when {
                            carregando -> "Processando…"
                            caminho != null -> "Trocar imagem"
                            else -> rotuloVazio
                        },
                        color = TextoClaro,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                if (caminho != null) {
                    Row(
                        Modifier
                            .clickable {
                                ImagemImportador.remover(caminho)
                                aoDefinir(null)
                            }
                            .padding(2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Remover imagem", tint = Perigo)
                        Text("Remover", color = Perigo, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
        erro?.let { Text(it, color = Perigo, style = MaterialTheme.typography.bodySmall) }
    }
}
