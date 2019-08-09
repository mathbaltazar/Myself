package com.baltazarstudio.regular.model

import java.math.BigDecimal

class ItemCarteiraAberta {
    var id: Int? = null
    var descricao: String? = null
    var valor: BigDecimal? = null

    constructor()
    constructor(id: Int, descricao: String, valor: BigDecimal) {
        this.id = id
        this.descricao = descricao
        this.valor = valor
    }

}
