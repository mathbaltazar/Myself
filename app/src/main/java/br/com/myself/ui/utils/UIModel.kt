package br.com.myself.ui.utils

import br.com.myself.data.model.Entrada

sealed class UIModel {
    data class UIEntrada(val entrada: Entrada) : UIModel()
    data class SeparatorEntrada(val mes: Int) : UIModel()
}