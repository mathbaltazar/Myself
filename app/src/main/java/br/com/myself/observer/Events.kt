package br.com.myself.observer

object Events {
    
    open class Toast(val message: String)
    open class Snack(val message: String)
    
    open class UpdateRegistros
    open class UpdateDespesas
    open class UpdateEntradas
    open class FiltrarRegistrosPelaDescricao
    open class HabilitarModoMultiSelecao
    open class DesabilitarModoMultiSelecao
}