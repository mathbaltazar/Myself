package br.com.myself.model

import br.com.myself.util.IDateFilterable

class Registro : IDateFilterable {
    
    var id: Long? = null
    var descricao: String? = null
    var outros: String? = null
    var data: Long? = 0
    var referencia_mes_ano: String? = null
    var valor: Double = 0.0
    var fk_despesa: Long? = null
    
    override fun getDate(): Long? {
        return data
    }
    
    override fun toString(): String {
        return "[id=$id, descricao=$descricao, outros=$outros, data=$data, referencia_mes_ano=$referencia_mes_ano, valor=$valor, fk_despesa=$fk_despesa]"
    }
    
    
}