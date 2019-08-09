package com.baltazarstudio.regular.model

import java.math.BigDecimal

class ItemCarteiraAberta {
    private var id: Int? = null
    private var descricao: String? = null
    private var valor: BigDecimal? = null

    constructor() {}
    constructor(descricao: String, valor: BigDecimal) {
        this.descricao = descricao
        this.valor = valor
    }

}
