package br.com.myself.model

import br.com.myself.util.IDateFilterable

class Entrada : IDateFilterable {
    
    var id: Int? = null
    var descricao: String? = null
    var data: Long? = 0
    var valor: Double = 0.0
    
    override fun getDate(): Long? {
        return data
    }
}
