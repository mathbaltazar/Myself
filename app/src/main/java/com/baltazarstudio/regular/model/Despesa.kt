package com.baltazarstudio.regular.model

import com.baltazarstudio.regular.model.exception.ModelException
import com.baltazarstudio.regular.util.Utils
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