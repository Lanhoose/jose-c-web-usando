package com.getech.app

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class GeTechRepository(context: Context) {
    private val prefs = context.getSharedPreferences("getech_local", Context.MODE_PRIVATE)

    private val seed = mapOf(
        "estoque" to listOf(
            mapOf("nome" to "Rolamento SKF 6205", "codigo" to "RLM-6205", "qtd" to "42", "minimo" to "15", "local" to "Almox. A-01"),
            mapOf("nome" to "Óleo hidráulico ISO 68", "codigo" to "OLE-ISO68", "qtd" to "8", "minimo" to "12", "local" to "Almox. B-04"),
            mapOf("nome" to "Correia dentada HTD 8M", "codigo" to "COR-8M", "qtd" to "26", "minimo" to "10", "local" to "Almox. A-07"),
            mapOf("nome" to "Filtro de ar comprimido", "codigo" to "FIL-AR32", "qtd" to "5", "minimo" to "8", "local" to "Almox. C-02"),
            mapOf("nome" to "Contator tripolar 25A", "codigo" to "ELT-C25", "qtd" to "31", "minimo" to "10", "local" to "Painel E-11"),
            mapOf("nome" to "Vedação NBR 80x100", "codigo" to "VED-80100", "qtd" to "64", "minimo" to "20", "local" to "Almox. B-09")
        ),
        "maquinas" to listOf(
            mapOf("nome" to "Torno CNC Romi GL 240", "modelo" to "GL 240", "serie" to "RM-2019-4471", "ultimaManutencao" to "21 dias atrás"),
            mapOf("nome" to "Prensa Hidráulica 150t", "modelo" to "PH-150", "serie" to "PH-2021-0088", "ultimaManutencao" to "9 dias atrás"),
            mapOf("nome" to "Centro de Usinagem VMC 850", "modelo" to "VMC-850", "serie" to "VM-2020-1123", "ultimaManutencao" to "45 dias atrás"),
            mapOf("nome" to "Compressor Parafuso 50HP", "modelo" to "CP-50", "serie" to "CP-2018-7702", "ultimaManutencao" to "3 dias atrás"),
            mapOf("nome" to "Ponte Rolante 10t", "modelo" to "PR-10", "serie" to "PR-2017-3390", "ultimaManutencao" to "60 dias atrás")
        ),
        "manutencao" to listOf(
            mapOf("maquina" to "Prensa Hidráulica 150t", "setor" to "Estamparia", "tipo" to "Corretiva", "responsavel" to "Diego Alves", "status" to "Em execução"),
            mapOf("maquina" to "Torno CNC Romi GL 240", "setor" to "Usinagem", "tipo" to "Preventiva", "responsavel" to "Marcos Lima", "status" to "Aberta"),
            mapOf("maquina" to "Compressor Parafuso 50HP", "setor" to "Utilidades", "tipo" to "Preditiva", "responsavel" to "Juliana Reis", "status" to "Concluída"),
            mapOf("maquina" to "Ponte Rolante 10t", "setor" to "Expedição", "tipo" to "Preventiva", "responsavel" to "Diego Alves", "status" to "Aberta")
        ),
        "producao" to listOf(
            mapOf("linha" to "Linha 01 — Usinagem", "turno" to "1º turno", "meta" to "480", "produzido" to "447"),
            mapOf("linha" to "Linha 02 — Estamparia", "turno" to "2º turno", "meta" to "620", "produzido" to "590"),
            mapOf("linha" to "Linha 03 — Montagem", "turno" to "1º turno", "meta" to "300", "produzido" to "312"),
            mapOf("linha" to "Linha 04 — Acabamento", "turno" to "3º turno", "meta" to "250", "produzido" to "198")
        ),
        "qualidade" to listOf(
            mapOf("lote" to "LT-2401", "item" to "Eixo usinado 40mm", "inspetor" to "Paula Fontes", "resultado" to "Aprovado"),
            mapOf("lote" to "LT-2402", "item" to "Flange estampada", "inspetor" to "Paula Fontes", "resultado" to "Aprovado"),
            mapOf("lote" to "LT-2403", "item" to "Suporte soldado", "inspetor" to "Renato Dias", "resultado" to "Reprovado"),
            mapOf("lote" to "LT-2404", "item" to "Bucha de bronze", "inspetor" to "Renato Dias", "resultado" to "Em análise"),
            mapOf("lote" to "LT-2405", "item" to "Engrenagem Z28", "inspetor" to "Paula Fontes", "resultado" to "Aprovado")
        ),
        "suprimentos" to listOf(
            mapOf("item" to "Óleo hidráulico ISO 68 (200L)", "fornecedor" to "Lubrimax", "quantidade" to "4 tambores", "status" to "Aprovado"),
            mapOf("item" to "Filtro de ar comprimido", "fornecedor" to "AirParts", "quantidade" to "20 un", "status" to "Cotação"),
            mapOf("item" to "Rolamento SKF 6205", "fornecedor" to "Rolatec", "quantidade" to "50 un", "status" to "Recebido"),
            mapOf("item" to "Chapa aço 1020 3mm", "fornecedor" to "Aços Norte", "quantidade" to "12 chapas", "status" to "Solicitado")
        ),
        "colaboradores" to listOf(
            mapOf("nome" to "Marcos Lima", "cargo" to "Técnico Mecânico", "setor" to "Manutenção", "entrada" to "há 6 horas"),
            mapOf("nome" to "Juliana Reis", "cargo" to "Engenheira de Confiabilidade", "setor" to "Engenharia", "entrada" to "há 7 horas"),
            mapOf("nome" to "Diego Alves", "cargo" to "Eletricista Industrial", "setor" to "Manutenção", "entrada" to "—"),
            mapOf("nome" to "Paula Fontes", "cargo" to "Inspetora de Qualidade", "setor" to "Qualidade", "entrada" to "há 4 horas")
        ),
        "pontos" to listOf(
            mapOf("colaborador" to "Paula Fontes", "tipo" to "Entrada"),
            mapOf("colaborador" to "Marcos Lima", "tipo" to "Entrada"),
            mapOf("colaborador" to "Juliana Reis", "tipo" to "Entrada"),
            mapOf("colaborador" to "Diego Alves", "tipo" to "Saída")
        ),
        "pedidos" to listOf(
            mapOf("op" to "OP-1042", "cliente" to "Metalúrgica Norte", "produto" to "Eixo usinado 40mm", "qtd" to "120", "status" to "Em Andamento", "prioridade" to "Urgente", "prazo" to "em 3 dias"),
            mapOf("op" to "OP-1043", "cliente" to "Fundição Vale", "produto" to "Flange estampada 6"", "qtd" to "300", "status" to "A Fazer", "prioridade" to "Normal", "prazo" to "em 9 dias"),
            mapOf("op" to "OP-1039", "cliente" to "Agro Máquinas Sul", "produto" to "Engrenagem Z28", "qtd" to "80", "status" to "Qualidade", "prioridade" to "Normal", "prazo" to "em 1 dia"),
            mapOf("op" to "OP-1035", "cliente" to "Cimento Atlântico", "produto" to "Bucha de bronze 60mm", "qtd" to "45", "status" to "Finalizado", "prioridade" to "Normal", "prazo" to "entregue")
        ),
        "siu-ativos" to listOf(
            mapOf("codigo" to "ATV-001", "nome" to "Torno CNC Romi GL 240", "setor" to "Usinagem", "situacao" to "Operacional", "freq" to "90"),
            mapOf("codigo" to "ATV-002", "nome" to "Prensa Hidráulica 150t", "setor" to "Estamparia", "situacao" to "Em Manutenção", "freq" to "60"),
            mapOf("codigo" to "ATV-003", "nome" to "Compressor Parafuso 50HP", "setor" to "Utilidades", "situacao" to "Operacional", "freq" to "30"),
            mapOf("codigo" to "ATV-004", "nome" to "Ponte Rolante 10t", "setor" to "Expedição", "situacao" to "Parada", "freq" to "120")
        ),
        "siu-tecnicos" to listOf(
            mapOf("matricula" to "T-1001", "nome" to "Marcos Lima", "especialidade" to "Mecânica pesada"),
            mapOf("matricula" to "T-1002", "nome" to "Diego Alves", "especialidade" to "Elétrica industrial"),
            mapOf("matricula" to "T-1003", "nome" to "Juliana Reis", "especialidade" to "Confiabilidade e preditiva")
        ),
        "siu-historico" to listOf(
            mapOf("codigo" to "INT-2201", "maquina" to "Prensa Hidráulica 150t", "data" to "há 2 dias", "tipo" to "Corretiva", "servico" to "Troca de vedação do cilindro principal", "tecnico" to "Marcos Lima"),
            mapOf("codigo" to "INT-2198", "maquina" to "Compressor Parafuso 50HP", "data" to "há 6 dias", "tipo" to "Preditiva", "servico" to "Análise de vibração e troca de filtro", "tecnico" to "Juliana Reis"),
            mapOf("codigo" to "INT-2190", "maquina" to "Torno CNC Romi GL 240", "data" to "há 21 dias", "tipo" to "Preventiva", "servico" to "Lubrificação de guias e ajuste de castanhas", "tecnico" to "Diego Alves")
        ),
        "chamados" to listOf(
            mapOf("nome" to "Carlos Menezes", "email" to "cliente@getech.com", "problema" to "Ruído anormal no redutor da linha 02.", "origem" to "Chatbot"),
            mapOf("nome" to "Fernanda Prado", "email" to "fernanda@fundicaovale.com", "problema" to "Solicito orçamento de manutenção preventiva anual.", "origem" to "Formulário de contato")
        )
    )

    init { seed() }

    private fun seed() {
        if (!prefs.getBoolean("seed_v1", false)) {
            val users = JSONArray()
                .put(JSONObject().put("nome","Ana Ribeiro").put("email","gestor@getech.com").put("senha","getech123").put("perfil","GESTOR"))
                .put(JSONObject().put("nome","Carlos Menezes").put("email","cliente@getech.com").put("senha","getech123").put("perfil","CLIENTE"))
            prefs.edit().putString("usuarios", users.toString()).apply()
            seed.forEach { (key, list) -> saveRecords(key, list.map { Registro(UUID.randomUUID().toString(), it) }) }
            val logs = listOf(
                "LOGIN|Ana Ribeiro entrou como gestor|INFO",
                "ATUALIZAR|OS da Prensa Hidráulica movida para Em execução|INFO",
                "ALERTA|Estoque crítico: Filtro de ar comprimido (5/8)|AVISO",
                "CRIAR|Nova inspeção registrada no lote LT-2405|INFO",
                "ATUALIZAR|Lote LT-2403 reprovado na inspeção dimensional|CRITICO"
            )
            prefs.edit().putString("logs", logs.joinToString("\n")).putBoolean("seed_v1", true).apply()
        }
    }

    fun session(): Sessao? {
        val raw = prefs.getString("sessao", null) ?: return null
        return runCatching {
            val o = JSONObject(raw)
            Sessao(o.getString("nome"), o.getString("email"), Perfil.valueOf(o.getString("perfil")), o.optString("foto"))
        }.getOrNull()
    }

    fun login(email: String, senha: String): Sessao? {
        val arr = runCatching { JSONArray(prefs.getString("usuarios","[]")) }.getOrDefault(JSONArray())
        for (i in 0 until arr.length()) {
            val o=arr.getJSONObject(i)
            if (o.getString("email").equals(email.trim(),true) && o.getString("senha")==senha) {
                val s=Sessao(o.getString("nome"),o.getString("email"),Perfil.valueOf(o.getString("perfil")))
                prefs.edit().putString("sessao",JSONObject().put("nome",s.nome).put("email",s.email).put("perfil",s.perfil.name).toString()).apply()
                log("LOGIN","${s.nome} entrou como ${if(s.perfil==Perfil.GESTOR) "gestor" else "cliente"}","INFO")
                return s
            }
        }
        return null
    }

    fun logout() { prefs.edit().remove("sessao").apply() }

    fun register(nome:String,email:String,senha:String,perfil:Perfil):Boolean {
        val arr=JSONArray(prefs.getString("usuarios","[]"))
        if ((0 until arr.length()).any { arr.getJSONObject(it).getString("email").equals(email.trim(),true) }) return false
        arr.put(JSONObject().put("nome",nome).put("email",email.trim().lowercase()).put("senha",senha).put("perfil",perfil.name))
        prefs.edit().putString("usuarios",arr.toString()).apply()
        return true
    }

    fun resetPassword(email:String,newPassword:String):Boolean {
        val arr=JSONArray(prefs.getString("usuarios","[]"))
        var found=false
        for(i in 0 until arr.length()){
            val o=arr.getJSONObject(i)
            if(o.getString("email").equals(email.trim(),true)){o.put("senha",newPassword);found=true}
        }
        if(found)prefs.edit().putString("usuarios",arr.toString()).apply()
        return found
    }

    fun records(key:String):MutableList<Registro> {
        val raw=prefs.getString("records_$key",null) ?: return mutableListOf()
        val arr=runCatching{JSONArray(raw)}.getOrDefault(JSONArray())
        return MutableList(arr.length()){i->
            val o=arr.getJSONObject(i)
            val fields=o.getJSONObject("campos")
            val map=mutableMapOf<String,String>()
            fields.keys().forEach{ k->map[k]=fields.optString(k) }
            Registro(o.getString("id"),map)
        }
    }

    fun add(key:String, fields:Map<String,String>):Registro {
        val r=Registro(UUID.randomUUID().toString(),fields)
        val list=records(key); list.add(0,r); saveRecords(key,list)
        log("CRIAR","Novo registro em $key","INFO")
        return r
    }

    fun update(key:String,id:String,fields:Map<String,String>){
        val list=records(key).map{if(it.id==id) it.copy(campos=it.campos+fields) else it}
        saveRecords(key,list); log("ATUALIZAR","Registro atualizado em $key","INFO")
    }

    fun remove(key:String,id:String){
        saveRecords(key,records(key).filterNot{it.id==id}); log("EXCLUIR","Registro removido de $key","AVISO")
    }

    fun logs():List<String> = prefs.getString("logs","")!!.lines().filter{it.isNotBlank()}

    fun log(action:String,desc:String,nivel:String="INFO"){
        val old=logs().toMutableList(); old.add(0,"$action|$desc|$nivel")
        prefs.edit().putString("logs",old.take(500).joinToString("\n")).apply()
    }

    private fun saveRecords(key:String,list:List<Registro>){
        val arr=JSONArray()
        list.forEach{r->
            val o=JSONObject().put("id",r.id)
            val f=JSONObject(); r.campos.forEach{(k,v)->f.put(k,v)}
            o.put("campos",f); arr.put(o)
        }
        prefs.edit().putString("records_$key",arr.toString()).apply()
    }
}
