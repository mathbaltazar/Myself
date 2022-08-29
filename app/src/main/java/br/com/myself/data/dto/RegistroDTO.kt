package br.com.myself.data.dto

data class RegistroDTO(
        val objectID: String,
        val descricao: String,
        val valor: Double,
        val data: String,
        val outros: String?,
        val isDeleted: Boolean
)
