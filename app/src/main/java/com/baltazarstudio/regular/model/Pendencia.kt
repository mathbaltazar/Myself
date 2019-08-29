package com.baltazarstudio.regular.model

import java.math.BigDecimal

class Pendencia {
    var id: Int? = null
    var descricao: String? = null
    var data: String? = null
    var valor: BigDecimal = BigDecimal.ZERO
    var pago: Boolean = false
}
