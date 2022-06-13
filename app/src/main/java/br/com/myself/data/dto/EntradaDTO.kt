package br.com.myself.data.dto

data class EntradaDTO(
    var id: Long? = null,
    var fonte: String,
    var valor: Double,
    var data: String,
)