package com.baltazarstudio.regular.database

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.baltazarstudio.regular.model.ItemCarteiraAberta
import java.util.*

class ItemCarteiraAbertaDAO(context: Context) : Database<ItemCarteiraAberta>(context) {
    val registroItemDAO = RegistroItemDAO(context)

    override fun get(id: Int): ItemCarteiraAberta {
        val query = "SELECT * FROM $TABELA_ITEM_CARTEIRA WHERE $TABLE_ID = $id"

        val cursor = readableDatabase.rawQuery(query, null)
        val itemCarteira = ItemCarteiraAberta()
        cursor.moveToFirst()
        bind(cursor, itemCarteira)

        cursor.close()
        return itemCarteira
    }

    override fun getTodos(): List<ItemCarteiraAberta> {
        val listaItens = ArrayList<ItemCarteiraAberta>()
        val query = "SELECT * FROM $TABELA_ITEM_CARTEIRA ORDER BY $TABLE_ID DESC"

        val cursor = readableDatabase.rawQuery(query, null)
        while (cursor.moveToNext()) {
            val item = ItemCarteiraAberta()
            bind(cursor, item)

            listaItens.add(item)
        }
        cursor.close()

        return listaItens
    }

    override fun inserir(objeto: ItemCarteiraAberta) {
        val insert = "INSERT INTO $TABELA_ITEM_CARTEIRA (" +
                "$ITEM_CARTEIRA_DESCRICAO," +
                "$ITEM_CARTEIRA_DATA," +
                "$ITEM_CARTEIRA_VALOR)" +
                " VALUES (" +
                "'${objeto.descricao}'," +
                "'${objeto.data}'," +
                "'${objeto.valor.toString()}'" +
                ")"

        writableDatabase.execSQL(insert)
    }

    override fun alterar(objeto: ItemCarteiraAberta) {}

    override fun excluir(objeto: ItemCarteiraAberta) {
        val query = "DELETE FROM $TABELA_ITEM_CARTEIRA " +
                "WHERE $TABLE_ID = " + objeto.id

        writableDatabase.execSQL(query)

        for (registro in objeto.registros) {
            registroItemDAO.excluir(registro)
        }
    }


    override fun bind(cursor: Cursor, objeto: ItemCarteiraAberta) {
        objeto.id = cursor.getInt(cursor.getColumnIndex(TABLE_ID))
        objeto.descricao = cursor.getString(cursor.getColumnIndex(ITEM_CARTEIRA_DESCRICAO))
        objeto.valor = cursor.getString(cursor.getColumnIndex(ITEM_CARTEIRA_VALOR)).toBigDecimal()
        objeto.data = cursor.getString(cursor.getColumnIndex(ITEM_CARTEIRA_DATA))
        objeto.registros.addAll(registroItemDAO.getTodos().filter { it.itemCarteiraAberta?.id == objeto.id })
    }

    companion object {
        private const val TABELA_ITEM_CARTEIRA = "Carteira"

        private const val ITEM_CARTEIRA_DESCRICAO = "descricao"
        private const val ITEM_CARTEIRA_DATA = "data"
        private const val ITEM_CARTEIRA_VALOR = "valor"

        fun onCreate(db: SQLiteDatabase) {
            val create = "CREATE TABLE $TABELA_ITEM_CARTEIRA (" +
                    "$TABLE_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$ITEM_CARTEIRA_DESCRICAO TEXT," +
                    "$ITEM_CARTEIRA_DATA TEXT," +
                    "$ITEM_CARTEIRA_VALOR TEXT)"

            db.execSQL(create)
        }
    }

}
