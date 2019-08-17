package com.baltazarstudio.regular.model

import java.math.BigDecimal

class Economia {
    var id: Int? = null
    var descricao: String? = null
    var valor: BigDecimal? = null
    var data: String? = null

    var poupancas = ArrayList<Poupanca>()
}
