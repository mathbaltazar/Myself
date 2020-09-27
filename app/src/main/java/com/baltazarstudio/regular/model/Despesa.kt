package com.baltazarstudio.regular.model

class Despesa {
    var nome: String? = null
    /** Not serialized */
    var ultimoRegistro: Long = 0L
    var valor: Double = 0.0
    var referencia: Int? = null
}