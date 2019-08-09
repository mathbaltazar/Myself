package com.baltazarstudio.regular.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.baltazarstudio.regular.model.ItemCarteiraAberta
import java.util.*

class ItemCarteiraAbertaDAO(context: Context) : Database(context) {

    override fun get(id: Long): ItemCarteiraAberta {
        val query = "SELECT * FROM $TABELA_ITEM_CARTEIRA WHERE $TABLE_ID = $id"

        val cursor = readableDatabase.rawQuery(query, null)
        cursor.moveToFirst()
        val itemCarteira = ItemCarteiraAberta(
            cursor.getInt(cursor.getColumnIndex(TABLE_ID)),
            cursor.getString(cursor.getColumnIndex(ITEM_CARTEIRA_DESCRICAO)),
            cursor.getString(cursor.getColumnIndex(ITEM_CARTEIRA_VALOR)).toBigDecimal()
        )
        cursor.close()
        return itemCarteira
    }

    override fun getTodos(): List<ItemCarteiraAberta> {
        val listaItens = ArrayList<ItemCarteiraAberta>()

        val cursor = readableDatabase.rawQuery("SELECT * FROM $TABELA_ITEM_CARTEIRA", null)
        while (cursor.moveToNext()) {
            listaItens.add(
                ItemCarteiraAberta(
                    cursor.getInt(cursor.getColumnIndex(TABLE_ID)),
                    cursor.getString(cursor.getColumnIndex(ITEM_CARTEIRA_DESCRICAO)),
                    cursor.getString(cursor.getColumnIndex(ITEM_CARTEIRA_VALOR)).toBigDecimal()
                )
            )
        }
        cursor.close()

        return listaItens
    }

    override fun inserir(objeto: ItemCarteiraAberta) {
        val insert = "INSERT INTO $TABELA_ITEM_CARTEIRA ($ITEM_CARTEIRA_DESCRICAO, $ITEM_CARTEIRA_VALOR)" +
                " VALUES ('${objeto.descricao}','${objeto.valor.toString()}')"

        writableDatabase.execSQL(insert)
    }

    override fun alterar(objeto: ItemCarteiraAberta) {
    }

    override fun excluir(objeto: ItemCarteiraAberta) {
    }

    companion object {
        private const val TABELA_ITEM_CARTEIRA = "Carteira"

        private const val ITEM_CARTEIRA_DESCRICAO = "descricao"
        private const val ITEM_CARTEIRA_VALOR = "valor"

        fun onCreate(db: SQLiteDatabase) {
            val create = "CREATE TABLE $TABELA_ITEM_CARTEIRA ($TABLE_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$ITEM_CARTEIRA_DESCRICAO TEXT, $ITEM_CARTEIRA_VALOR TEXT)"

            db.execSQL(create)
        }
    }

}
