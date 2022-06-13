package br.com.myself.ui.utils

import br.com.myself.model.entity.Entrada

sealed class UIModel {
    data class UIEntrada(val entrada: Entrada) : UIModel()
    data class SeparatorEntrada(val mes: Int) : UIModel()
}