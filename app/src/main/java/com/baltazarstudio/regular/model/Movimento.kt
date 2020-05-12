package com.baltazarstudio.regular.model

import java.math.BigDecimal

class Movimento {
    var id: Int? = null
    var descricao: String? = null
    var data: Long? = null
    var valor: BigDecimal = BigDecimal.ZERO
}
