package br.com.myself.ui.adapter.state

import br.com.myself.data.model.Entrada

sealed class UIModelState {
    //Entrada
    data class UIEntradaState(val entrada: Entrada) : UIModelState()
    data class SeparatorEntrada(val mes: Int) : UIModelState()

    // Registro
    data class UIRegistroState(
        val id: Long,
        val descricao: String,
        val valor: String,
        val data: String,
        val outros: String?,
        val isSync: Boolean,

        val onItemSelected: () -> Unit
    ): UIModelState()
}