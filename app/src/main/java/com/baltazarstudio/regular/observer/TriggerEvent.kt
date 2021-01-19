package com.baltazarstudio.regular.observer

object TriggerEvent {
    
    class Toast(val message: String)
    class Snack(val message: String)
    
    class UpdateTelaMovimento
    class UpdateTelaDespesa
    class UpdateTelaEntradas
    class FiltrarMovimentosPelaDescricao
    class HabilitarModoMultiSelecao
    class DesabilitarModoMultiSelecao
}