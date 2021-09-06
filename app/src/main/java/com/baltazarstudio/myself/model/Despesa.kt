package com.baltazarstudio.myself.model

import com.baltazarstudio.myself.model.exception.ModelException
import java.math.BigDecimal

class Despesa {
    var codigo: Int = 0
    var nome: String? = null
        set(value) {
            if (value.isNullOrBlank()) throw ModelException("Nome inválido")
            field = value
        }
    var valor: Double = 0.0
        set(value) {
            if (value.toBigDecimal() <= BigDecimal.ZERO) throw ModelException("Valor inválido")
            field = value
        }
    var diaVencimento: Int = 0
    var arquivado: Int = 0
}