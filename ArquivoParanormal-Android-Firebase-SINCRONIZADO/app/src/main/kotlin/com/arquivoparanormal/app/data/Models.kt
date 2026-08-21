package com.arquivoparanormal.app.data

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable
import kotlin.random.Random

fun novoId(): String = Random.nextLong().toULong().toString(36).take(8) + Random.nextInt(100, 999)

@Serializable
@Immutable
data class Pericia(val treino: Int = 0, val outros: Int = 0)

@Serializable
@Immutable
data class Item(
    val id: String = novoId(),
    val nome: String = "",
    val categoria: Int = 1,
    val espacos: Double = 1.0,
    val qtd: Int = 1,
    val desc: String = "",
    val equipado: Boolean = false,
    val tipoInventario: String = "Itens",
)

@Serializable
@Immutable
data class Arma(
    val id: String = novoId(),
    val nome: String = "",
    val tipo: String = "Corpo a corpo",
    val grupo: String = "Armas Simples",
    val pericia: String = "Luta",
    val dano: String = "1d6",
    val critico: String = "20/x2",
    val alcance: String = "—",
    val tipoDano: String = "Impacto",
    val categoria: Int = 0,
    val espacos: Double = 1.0,
    val icone: String = "⚔",
    val municao: String = "—",
    val pacotesMunicao: Int = 0,
    val descricao: String = "",
    val obs: String = "",
)

@Serializable
@Immutable
data class Ritual(
    val id: String = novoId(),
    val nome: String = "",
    val circulo: String = "1º",
    val elemento: String = "",
    val execucao: String = "Padrão",
    val alcance: String = "Curto",
    val afinidade: String = "Nenhuma",
    val simbolo: String = "",
    val descricao: String = "",
    val efeito: String = "",
)

@Serializable
@Immutable
data class Personagem(
    val id: String = novoId(),
    val nome: String = "Novo Agente",
    val jogador: String = "",
    val idade: String = "",
    val descricao: String = "",
    val classe: String = "Combatente",
    val trilha: String = "",
    val origem: String = "",
    val patente: String = "Recruta (NEX 5%)",
    val nex: Int = 5,
    val atributos: Map<String, Int> = mapOf("for" to 1, "agi" to 1, "int" to 1, "vig" to 1, "pre" to 1),
    val pvAtual: Int = 21,
    val pvMax: Int = 21,
    val peAtual: Int = 3,
    val peMax: Int = 3,
    val sanAtual: Int = 12,
    val sanMax: Int = 12,
    val defesa: Int = 10,
    val defesaEquipamento: Int = 0,
    val defesaOutros: Int = 0,
    val deslocamento: Int = 9,
    /** Quando preenchido, o valor manual substitui o cálculo automático daquela chave. */
    val overrides: Map<String, Int> = emptyMap(),
    /** Perícias adicionadas automaticamente pela origem selecionada. */
    val periciasAutomaticas: List<String> = emptyList(),
    val pericias: Map<String, Pericia> = emptyMap(),
    val afinidade: String = "",
    val resistencias: Map<String, Int> = RESISTENCIAS.associateWith { 0 },
    val condicoes: List<String> = emptyList(),
    val historia: String = "",
    val aparencia: String = "",
    val personalidade: String = "",
    val objetivo: String = "",
    val medos: String = "",
    val itens: List<Item> = emptyList(),
    val armas: List<Arma> = emptyList(),
    val rituais: List<Ritual> = emptyList(),
    val habilidades: String = "",
    val fotoArquivo: String? = null,
    val imagem: String? = null,
    val atualizadoEm: Long = System.currentTimeMillis(),
)

@Serializable
@Immutable
data class Monstro(
    val id: String = novoId(),
    val nome: String = "Nova Criatura",
    val tipo: String = "Criatura Paranormal",
    val elemento: String = "",
    val vd: String = "1",
    val pv: Int = 20,
    val defesa: Int = 15,
    val deslocamento: Int = 9,
    val iniciativa: Int = 0,
    val atributos: Map<String, Int> = mapOf("for" to 2, "agi" to 2, "int" to 1, "vig" to 2, "pre" to 2),
    val ataques: String = "",
    val habilidades: String = "",
    val descricao: String = "",
    val notas: String = "",
    val derrotado: Boolean = false,
    val fotoArquivo: String? = null,
    val imagem: String? = null,
    val atualizadoEm: Long = System.currentTimeMillis(),
)

@Serializable
@Immutable
data class Combatente(
    val id: String = novoId(),
    val nome: String = "",
    val iniciativa: Int = 0,
    val pv: Int = 10,
    val pvMax: Int = 10,
    val aliado: Boolean = true,
    val condicoes: List<String> = emptyList(),
)

@Serializable
@Immutable
data class Batalha(
    val salaId: String = "principal",
    val rodada: Int = 1,
    val turno: Int = 0,
    val combatentes: List<Combatente> = emptyList(),
)

/**
 * Token posicionado na grade tática da Mesa de Batalha (posição em células, não em pixels).
 */
@Serializable
@Immutable
data class TokenMesa(
    val id: String = novoId(),
    val nome: String = "Token",
    val x: Int = 0,
    val y: Int = 0,
    val corNome: String = "Agente",
    val tipo: String = "agente", // "agente" | "ameaca"
    val pvAtual: Int = 10,
    val pvMax: Int = 10,
    /** Imagem compartilhada pelo site/Firebase (normalmente uma data URL). */
    val imagem: String? = null,
    /** Imagem local escolhida diretamente no Android. */
    val imagemArquivo: String? = null,
)

/**
 * Estado da Mesa de Batalha tática: grade, tokens, névoa de guerra e mapa importado
 * (PNG/JPG ou 1ª página de PDF, salvo como arquivo no armazenamento interno do app).
 */
@Serializable
@Immutable
data class AudioTrack(
    val id: String = novoId(),
    val nome: String = "Novo ambiente",
    val categoria: String = "Personalizado",
    val sourceKey: String? = null,
    /** URL externa opcional. Não é usada para os arquivos importados localmente. */
    val url: String? = null,
    /** Caminho privado no armazenamento do aparelho do Mestre. Nunca é sincronizado. */
    val localPath: String? = null,
    /** Nome original do arquivo importado, usado apenas para identificação. */
    val arquivoNome: String? = null,
)

@Serializable
@Immutable
data class AudioMesaState(
    val tracks: List<AudioTrack> = emptyList(),
    val ativoId: String? = null,
    val tocando: Boolean = false,
    val volume: Float = 0.75f,
    val loop: Boolean = true,
)

fun audiosPadrao(): List<AudioTrack> = listOf(
    AudioTrack("floresta", "Floresta", "Ambiente", sourceKey = "floresta"),
    AudioTrack("chuva", "Chuva", "Ambiente", sourceKey = "chuva"),
    AudioTrack("casa", "Casa abandonada", "Ambiente", sourceKey = "casa"),
    AudioTrack("perseguicao", "Perseguição", "Tensão", sourceKey = "perseguicao"),
    AudioTrack("combate", "Combate", "Ação", sourceKey = "combate"),
    AudioTrack("terror", "Terror", "Terror", sourceKey = "terror"),
)

@Serializable
@Immutable
data class MesaBatalha(
    val cols: Int = 12,
    val rows: Int = 9,
    val tokens: List<TokenMesa> = emptyList(),
    val ocultos: List<String> = emptyList(),
    val ambiente: String = "Névoa densa",
    val intensidade: Int = 55,
    val rodada: Int = 1,
    val mapaArquivo: String? = null,
    val mapaNome: String? = null,
    val mapaOpacidade: Int = 100,
    /** Mesmo ajuste usado pelo site: "cobrir" corta para preencher; "conter" mostra o mapa inteiro. */
    val mapaAjuste: String = "conter",
)

val AMBIENTES_MESA = listOf("Névoa densa", "Escuridão", "Sangue no ar", "Distorção de Energia", "Terror puro", "Limpo")
val PALETA_TOKEN = listOf("Agente", "Sangue", "Morte", "Conhecimento", "Energia", "Medo")

/** Limite de carga: 5 + Força x 5 (regra da mesa usada no site). */
fun limiteDeCarga(forca: Int): Int = 5 + forca * 5

fun espacosUsados(p: Personagem): Double =
    p.itens.sumOf { it.espacos * it.qtd } + p.armas.sumOf { it.espacos }


@Serializable
@Immutable
data class NpcCampanha(
    val id: String = novoId(),
    val nome: String = "Novo NPC",
    val descricao: String = "",
    val local: String = "",
    val segredo: String = "",
    val atualizadoEm: Long = System.currentTimeMillis(),
)

@Serializable
@Immutable
data class SessaoCampanha(
    val id: String = novoId(),
    val numero: Int = 1,
    val titulo: String = "Sessão 01",
    val resumo: String = "",
    val atualizadoEm: Long = System.currentTimeMillis(),
)

@Serializable
@Immutable
data class Campanha(
    val id: String = "principal",
    val nome: String = "Campanha sem nome",
    val notas: String = "",
    val objetivos: List<String> = emptyList(),
    val pistas: List<String> = emptyList(),
    val locais: List<String> = emptyList(),
    val npcs: List<NpcCampanha> = emptyList(),
    val sessoes: List<SessaoCampanha> = emptyList(),
    val atualizadoEm: Long = System.currentTimeMillis(),
)

@Serializable
data class CampanhaBackup(
    val versao: Int = 1,
    val exportadoEm: Long = System.currentTimeMillis(),
    val campanha: Campanha = Campanha(),
    val personagens: List<Personagem> = emptyList(),
    val monstros: List<Monstro> = emptyList(),
    val batalha: Batalha = Batalha(),
    val mesa: MesaBatalha = MesaBatalha(),
    val mapaBase64: String? = null,
    val mapaNome: String? = null,
)
