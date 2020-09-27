package com.baltazarstudio.regular.model

class Gasto {
    var id: Int? = null
    var descricao: String? = null
    var valor: Double = 0.0
    var mes: Int = 1
    var ano: Int = 1900
    var data: Long = 0
    var referenciaDespesa: Int? = null
    var margemDespesa: Double = 0.0
}
