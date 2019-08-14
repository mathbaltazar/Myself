package com.baltazarstudio.regular.model

import java.math.BigDecimal

class CarteiraPendencia {
    var id: Int? = null
    var descricao: String? = null
    var data: String? = null
    var valor: BigDecimal? = null

    val registros = ArrayList<RegistroItem>()
}
