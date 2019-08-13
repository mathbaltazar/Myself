package com.baltazarstudio.regular.database

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.baltazarstudio.regular.model.ItemCarteiraAberta
import com.baltazarstudio.regular.model.RegistroItem
import java.math.BigDecimal

class RegistroItemDAO(context: Context) : Database<RegistroItem>(context) {

    override fun get(id: Int): RegistroItem {
        TODO("not implemented")
    }

    override fun getTodos(): List<RegistroItem> {
        val query = "SELECT * FROM $TABELA_REGISTRO_ITEM"

        val listaRegistros = ArrayList<RegistroItem>()

        val cursor = readableDatabase.rawQuery(query, null)
        while (cursor.moveToNext()) {
            val registro = RegistroItem()
            bind(cursor, registro)

            listaRegistros.add(registro)
        }
        cursor.close()
        return listaRegistros

    }

    override fun inserir(objeto: RegistroItem) {
        val query = "INSERT INTO $TABELA_REGISTRO_ITEM (" +
                "$REGISTRO_ITEM_DESCRICAO," +
                "$REGISTRO_ITEM_VALOR," +
                "$REGISTRO_ITEM_FK_ITEM_CARTEIRA_TABLE_ID)" +
                " VALUES (" +
                "'${objeto.descricao}'," +
                "'${objeto.valor.toString()}'," +
                "${objeto.itemCarteiraAberta!!.id}" +
                ")"

        writableDatabase.execSQL(query)
    }

    override fun alterar(objeto: RegistroItem) {}

    override fun excluir(objeto: RegistroItem) {
        val query = "DELETE FROM $TABELA_REGISTRO_ITEM " +
                "WHERE $TABLE_ID = " + objeto.id

        writableDatabase.execSQL(query)
    }

    override fun bind(cursor: Cursor, objeto: RegistroItem) {
        objeto.id = cursor.getInt(cursor.getColumnIndex(TABLE_ID))
        objeto.descricao = cursor.getString(cursor.getColumnIndex(REGISTRO_ITEM_DESCRICAO))
        objeto.valor = BigDecimal(cursor.getString(cursor.getColumnIndex(REGISTRO_ITEM_VALOR)))
        objeto.itemCarteiraAberta = ItemCarteiraAberta()
        objeto.itemCarteiraAberta!!.id = cursor.getInt(cursor.getColumnIndex(REGISTRO_ITEM_FK_ITEM_CARTEIRA_TABLE_ID))
    }


    companion object {
        private const val TABELA_REGISTRO_ITEM = "RegistroItem"

        private const val REGISTRO_ITEM_DESCRICAO = "descricao"
        private const val REGISTRO_ITEM_VALOR = "valor"

        private const val REGISTRO_ITEM_FK_ITEM_CARTEIRA_TABLE_ID = "fk_id_item_carteira_aberta"

        fun onCreate(db: SQLiteDatabase) {
            val query = "CREATE TABLE $TABELA_REGISTRO_ITEM (" +
                    "$TABLE_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$REGISTRO_ITEM_DESCRICAO TEXT," +
                    "$REGISTRO_ITEM_VALOR TEXT," +
                    "$REGISTRO_ITEM_FK_ITEM_CARTEIRA_TABLE_ID INT NOT NULL)"

            db.execSQL(query)
        }
    }


}
