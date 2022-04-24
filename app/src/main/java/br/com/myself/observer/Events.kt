package br.com.myself.observer

import android.view.View
import br.com.myself.domain.entity.Registro

open class Events {
    
    class Toast(val message: String)
    class Snack(val view: View, val message: String)
    
    class EditarRegistro(val registro: Registro)
    class AtualizarDetalhesRegistro(val registro: Registro)
    
}