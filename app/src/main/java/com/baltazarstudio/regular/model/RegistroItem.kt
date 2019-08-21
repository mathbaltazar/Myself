package com.baltazarstudio.regular.model

import java.math.BigDecimal

class RegistroItem {
    var id: Int? = null
    var descricao: String? = null
    var valor: BigDecimal? = BigDecimal.ZERO

    var carteiraPendencia: CarteiraPendencia? = null
}
