package com.baltazarstudio.regular.observer

object Events {
    
    open class Toast(val message: String)
    open class Snack(val message: String)
    
    open class UpdateRegistros
    open class UpdateDespesas
    open class UpdateTelaEntradas
    open class FiltrarRegistrosPelaDescricao
    open class HabilitarModoMultiSelecao
    open class DesabilitarModoMultiSelecao
}