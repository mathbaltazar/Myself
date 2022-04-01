package br.com.myself.model

import br.com.myself.util.IDateFilterable

class Entrada : IDateFilterable {
    
    var id: Long? = null
    var fonte: String? = null
    var data: Long? = 0
    var valor: Double = 0.0
    var referencia_ano_mes: String? = null
    
    override fun getDate(): Long? {
        return data
    }
    
    override fun toString(): String {
        return "Entrada(id=$id, fonte=$fonte, data=$data, valor=$valor, referencia_ano_mes=$referencia_ano_mes)"
    }
    
    
}
