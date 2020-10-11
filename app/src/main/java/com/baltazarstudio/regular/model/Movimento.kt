package com.baltazarstudio.regular.model

class Movimento {
    companion object {
        const val GASTO = 0
        const val DESPESA = 1
    }
    
    var id: Int? = null
    var descricao: String? = null
    var data: Long? = 0
    var valor: Double = 0.0
    var referenciaDespesa: Int? = null
    var tipoMovimento: Int? = 0
}