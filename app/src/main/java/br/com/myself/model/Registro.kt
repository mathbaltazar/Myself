package br.com.myself.model

import br.com.myself.util.IDateFilterable

class Registro : IDateFilterable {
    
    var id: Int? = null
    var descricao: String? = null
    var local: String? = null
    var data: Long? = 0
    var valor: Double = 0.0
    var referenciaDespesa: Int? = null
    
    override fun getDate(): Long? {
        return data
    }
}