package br.com.myself.model

import br.com.myself.model.exception.ModelException
import java.math.BigDecimal

class Despesa {
    var id: Long = 0
    var nome: String? = null
    var valor: Double = 0.0
    var diaVencimento: Int = 0
    
    
    override fun toString(): String {
        return "Despesa(id=$id, nome=$nome, valor=$valor, diaVencimento=$diaVencimento)"
    }
}