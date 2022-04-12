package br.com.myself.observer

import android.view.View
import br.com.myself.domain.entity.Despesa
import br.com.myself.domain.entity.Entrada
import br.com.myself.domain.entity.Registro

open class Events {
    
    class Toast(val message: String)
    class Snack(val view: View, val message: String)
    
    object UpdateRegistros
    object UpdateEntradas
    object UpdateDespesas
    class EditarRegistro(val registro: Registro)
    class AtualizarDetalhesRegistro(val registro: Registro)
    
    open class EditarEntrada(val entrada: Entrada)
    open class RegistrarDespesa(val despesa: Despesa)
    object UpdateCrises
}