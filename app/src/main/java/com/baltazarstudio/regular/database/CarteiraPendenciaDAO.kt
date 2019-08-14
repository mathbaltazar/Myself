package com.baltazarstudio.regular.database

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.baltazarstudio.regular.model.CarteiraPendencia
import java.util.*

class CarteiraPendenciaDAO(context: Context) : Database<CarteiraPendencia>(context) {
    val registroItemDAO = RegistroItemDAO(context)

    override fun get(id: Int): CarteiraPendencia {
        val query = "SELECT * FROM $TABELA_ITEM_CARTEIRA WHERE $TABLE_ID = $id"

        val cursor = readableDatabase.rawQuery(query, null)
        val itemCarteira = CarteiraPendencia()
        cursor.moveToFirst()
        bind(cursor, itemCarteira)

        cursor.close()
        return itemCarteira
    }

    override fun getTodos(): List<CarteiraPendencia> {
        val listaItens = ArrayList<CarteiraPendencia>()
        val query = "SELECT * FROM $TABELA_ITEM_CARTEIRA ORDER BY $TABLE_ID DESC"

        val cursor = readableDatabase.rawQuery(query, null)
        while (cursor.moveToNext()) {
            val item = CarteiraPendencia()
            bind(cursor, item)

            listaItens.add(item)
        }
        cursor.close()

        return listaItens
    }

    override fun inserir(objeto: CarteiraPendencia) {
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

    override fun alterar(objeto: CarteiraPendencia) {}

    override fun excluir(objeto: CarteiraPendencia) {
        val query = "DELETE FROM $TABELA_ITEM_CARTEIRA " +
                "WHERE $TABLE_ID = " + objeto.id

        writableDatabase.execSQL(query)

        for (registro in objeto.registros) {
            registroItemDAO.excluir(registro)
        }
    }


    override fun bind(cursor: Cursor, objeto: CarteiraPendencia) {
        objeto.id = cursor.getInt(cursor.getColumnIndex(TABLE_ID))
        objeto.descricao = cursor.getString(cursor.getColumnIndex(ITEM_CARTEIRA_DESCRICAO))
        objeto.valor = cursor.getString(cursor.getColumnIndex(ITEM_CARTEIRA_VALOR)).toBigDecimal()
        objeto.data = cursor.getString(cursor.getColumnIndex(ITEM_CARTEIRA_DATA))
        objeto.registros.addAll(registroItemDAO.getTodos().filter { it.carteiraPendencia?.id == objeto.id })
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
