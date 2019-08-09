package com.baltazarstudio.regular.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.baltazarstudio.regular.model.ItemCarteiraAberta
import java.util.*

class ItemCarteiraAbertaDAO(context: Context) : Database(context), IDAO<ItemCarteiraAberta> {


    override fun get(id: Long): ItemCarteiraAberta {
        val itemCarteiraAberta = ItemCarteiraAberta()
        return itemCarteiraAberta
    }

    override fun getTodos(): List<ItemCarteiraAberta> {
        val itensCarteiraAberta = ArrayList<ItemCarteiraAberta>()
        return itensCarteiraAberta
    }

    override fun inserir(objeto: ItemCarteiraAberta) {
    }

    override fun alterar(objeto: ItemCarteiraAberta) {
    }

    override fun excluir(objeto: ItemCarteiraAberta) {
    }

    companion object {
        private val TABELA_ITEM_CARTEIRA = "Carteira"

        private val ITEM_CARTEIRA_DESCRICAO = "descricao"
        private val ITEM_CARTEIRA_VALOR = "descricao"

        fun onCreate(db: SQLiteDatabase) {
            var create_query = "CREATE TABLE " + TABELA_ITEM_CARTEIRA + "("
            create_query += ITEM_CARTEIRA_DESCRICAO + " TEXT,"
            create_query += ITEM_CARTEIRA_VALOR + " NUMERIC)"

            db.execSQL(create_query)
        }
    }

}
