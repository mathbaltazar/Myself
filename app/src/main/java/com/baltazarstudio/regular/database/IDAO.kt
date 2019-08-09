package com.baltazarstudio.regular.database

interface IDAO<T> {
    fun get(id: Long) : T
    fun getTodos() : List<T>
    fun inserir(objeto: T)
    fun alterar(objeto: T)
    fun excluir(objeto: T)
}
