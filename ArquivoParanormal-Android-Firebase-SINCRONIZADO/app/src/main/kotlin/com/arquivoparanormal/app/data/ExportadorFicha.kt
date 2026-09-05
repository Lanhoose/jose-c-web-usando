package com.arquivoparanormal.app.data

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

/**
 * Monta um resumo em texto simples de toda a ficha — atributos, recursos, perícias, armas,
 * itens, rituais, habilidades e poderes paranormais — adequado tanto para compartilhar
 * diretamente (WhatsApp, Telegram, e-mail) quanto para servir de base ao PDF exportado.
 */
fun gerarResumoTextoFicha(p: Personagem): String = buildString {
    appendLine("=== ${p.nome.ifBlank { "Agente sem nome" }} ===")
    appendLine("Jogador: ${p.jogador.ifBlank { "-" }}  |  Idade: ${p.idade.ifBlank { "-" }}")
    appendLine(
        "Classe: ${p.classe.ifBlank { "-" }}" +
            (if (p.trilha.isNotBlank()) "  |  Trilha: ${p.trilha}" else "") +
            (if (p.origem.isNotBlank()) "  |  Origem: ${p.origem}" else ""),
    )
    appendLine("NEX: ${p.nex}%  |  Patente: ${patentePorPrestigio(p.prestigio)} (PP: ${p.prestigio})")
    appendLine()

    appendLine("--- Atributos ---")
    appendLine(listOf("for", "agi", "int", "vig", "pre").joinToString("   ") { attr -> "${attr.uppercase()} ${p.atributos[attr] ?: 0}" })
    appendLine()

    appendLine("--- Recursos ---")
    appendLine("PV: ${p.pvAtual}/${p.pvMax}  |  PE: ${p.peAtual}/${p.peMax}  |  Sanidade: ${p.sanAtual}/${p.sanMax}")
    appendLine("Defesa (equipamento): ${p.defesaEquipamento}")
    if (p.condicoes.isNotEmpty()) appendLine("Condições ativas: ${p.condicoes.joinToString(", ")}")
    appendLine()

    val periciasTreinadas = p.pericias.filter { it.value.treino > 0 }.keys.sorted()
    if (periciasTreinadas.isNotEmpty()) {
        appendLine("--- Perícias treinadas ---")
        appendLine(periciasTreinadas.joinToString(", "))
        appendLine()
    }

    if (p.armas.isNotEmpty()) {
        appendLine("--- Armas ---")
        p.armas.forEach { a -> appendLine("- ${a.nome.ifBlank { "Arma sem nome" }} | Dano ${a.dano.ifBlank { "-" }} | Crítico ${a.critico.ifBlank { "-" }} | Alcance ${a.alcance.ifBlank { "-" }}") }
        appendLine()
    }

    if (p.itens.isNotEmpty()) {
        appendLine("--- Inventário ---")
        p.itens.forEach { i -> appendLine("- ${i.nome.ifBlank { "Item sem nome" }}${if (i.qtd > 1) " x${i.qtd}" else ""}") }
        appendLine()
    }

    if (p.rituais.isNotEmpty()) {
        appendLine("--- Rituais conhecidos ---")
        p.rituais.forEach { r -> appendLine("- ${r.nome.ifBlank { "Ritual sem nome" }} — ${r.circulo.ifBlank { "?" }} círculo, ${r.elemento.ifBlank { "-" }}") }
        appendLine()
    }

    val nomesHabilidades = p.habilidades.lines().map(String::trim).filter { it.isNotBlank() }.distinct()
    if (nomesHabilidades.isNotEmpty()) {
        appendLine("--- Habilidades ---")
        appendLine(nomesHabilidades.joinToString(", "))
        appendLine()
    }

    if (p.poderesParanormais.isNotEmpty()) {
        appendLine("--- Poderes paranormais ---")
        p.poderesParanormais.forEach { pp ->
            appendLine(
                "- ${pp.nome}${if (pp.afinidade) " (Afinidade)" else ""} — ${pp.elemento}" +
                    if (pp.ritualNome.isNotBlank()) " | Ritual: ${pp.ritualNome}" else "",
            )
        }
        appendLine()
    }

    if (p.historia.isNotBlank()) {
        appendLine("--- História ---")
        appendLine(p.historia)
        appendLine()
    }

    appendLine("Exportado do Arquivo Paranormal.")
}

/**
 * Gera um PDF simples (texto formatado, paginado em tamanho A4) com o resumo da ficha e
 * devolve o arquivo pronto para compartilhamento via FileProvider. O arquivo fica em uma
 * pasta de cache exclusiva do app e é sobrescrito a cada exportação do mesmo personagem.
 */
fun gerarPdfFicha(context: Context, p: Personagem): File {
    val texto = gerarResumoTextoFicha(p)
    val documento = PdfDocument()

    // Dimensões aproximadas de uma página A4 a 72dpi.
    val largura = 595
    val altura = 842
    val margemEsquerda = 40f
    val margemSuperior = 50f
    val margemInferior = 40f
    val alturaLinha = 15f
    val larguraUtil = largura - margemEsquerda * 2

    val paintTitulo = Paint().apply { color = AndroidColor.rgb(20, 20, 20); textSize = 15f; isFakeBoldText = true; isAntiAlias = true }
    val paintSecao = Paint().apply { color = AndroidColor.rgb(90, 20, 20); textSize = 12f; isFakeBoldText = true; isAntiAlias = true }
    val paintCorpo = Paint().apply { color = AndroidColor.rgb(40, 40, 40); textSize = 10.5f; isAntiAlias = true }

    fun paintPara(linha: String): Paint = when {
        linha.startsWith("===") -> paintTitulo
        linha.startsWith("---") -> paintSecao
        else -> paintCorpo
    }

    fun quebrarLinha(linha: String, paint: Paint): List<String> {
        if (paint.measureText(linha) <= larguraUtil) return listOf(linha)
        val palavras = linha.split(" ")
        val partes = mutableListOf<String>()
        var atual = StringBuilder()
        for (palavra in palavras) {
            val tentativa = if (atual.isEmpty()) palavra else "${atual} $palavra"
            if (paint.measureText(tentativa) > larguraUtil && atual.isNotEmpty()) {
                partes.add(atual.toString())
                atual = StringBuilder(palavra)
            } else {
                atual = StringBuilder(tentativa)
            }
        }
        if (atual.isNotEmpty()) partes.add(atual.toString())
        return partes
    }

    val linhasFormatadas = texto.split("\n").flatMap { linha ->
        if (linha.isBlank()) listOf("" to paintCorpo)
        else quebrarLinha(linha, paintPara(linha)).map { it to paintPara(linha) }
    }

    var indice = 0
    var numeroPagina = 1
    while (indice < linhasFormatadas.size || numeroPagina == 1) {
        val pageInfo = PdfDocument.PageInfo.Builder(largura, altura, numeroPagina).create()
        val pagina = documento.startPage(pageInfo)
        val canvas: Canvas = pagina.canvas
        var y = margemSuperior
        while (indice < linhasFormatadas.size && y < altura - margemInferior) {
            val (linha, paint) = linhasFormatadas[indice]
            if (linha.isNotEmpty()) canvas.drawText(linha, margemEsquerda, y, paint)
            y += alturaLinha
            indice++
        }
        documento.finishPage(pagina)
        numeroPagina++
        if (indice >= linhasFormatadas.size) break
    }

    val pastaExport = File(context.cacheDir, "fichas_exportadas").apply { mkdirs() }
    val nomeSeguro = p.nome.ifBlank { "Agente" }.replace(Regex("[^A-Za-z0-9 _-]"), "").replace(" ", "_").take(60)
    val arquivo = File(pastaExport, "Ficha_${nomeSeguro.ifBlank { "Agente" }}.pdf")
    FileOutputStream(arquivo).use { saida -> documento.writeTo(saida) }
    documento.close()
    return arquivo
}

/** Monta o Intent de compartilhamento de um PDF já gerado, usando o FileProvider do app. */
fun intentCompartilharPdf(context: Context, arquivo: File): Intent {
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", arquivo)
    return Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
}

/** Monta o Intent de compartilhamento do resumo da ficha como texto simples. */
fun intentCompartilharTexto(p: Personagem): Intent {
    return Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Ficha - ${p.nome.ifBlank { "Agente" }}")
        putExtra(Intent.EXTRA_TEXT, gerarResumoTextoFicha(p))
    }
}
