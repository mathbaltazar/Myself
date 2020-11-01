package com.baltazarstudio.regular.observer

object TriggerEvent {
    
    class Toast(val message: String)
    
    class UpdateTelaMovimento
    class UpdateTelaDespesa
    class UpdateTelaEntradas
    class FiltrarMovimentosPelaDescricao(val newText: String?)
    class PrepareMultiChoiceRegistrosLayout(val show: Boolean)
}