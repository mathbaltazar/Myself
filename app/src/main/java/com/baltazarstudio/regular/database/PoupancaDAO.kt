package com.baltazarstudio.regular.database

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.baltazarstudio.regular.model.Poupanca
import java.math.BigDecimal
import java.util.*
import kotlin.collections.ArrayList

class PoupancaDAO(context: Context) : Database<Poupanca>(context) {
    override fun bind(cursor: Cursor, objeto: Poupanca) {
        objeto.id = cursor.getInt(cursor.getColumnIndex(TABLE_ID))
        objeto.valor = BigDecimal(cursor.getString(cursor.getColumnIndex(POUPANCA_VALOR)))
        objeto.descricao = cursor.getString(cursor.getColumnIndex(POUPANCA_DESCRICAO))
    }

    override fun get(id: Int): Poupanca {
        return Poupanca()
    }

    @Deprecated("")
    override fun getTodos(): List<Poupanca> {
        return Collections.emptyList()
    }

    override fun inserir(objeto: Poupanca) {
        val query = "INSERT INTO $TABELA_POUPANCA (" +
                "$POUPANCA_VALOR," +
                "$POUPANCA_DESCRICAO," +
                "$POUPANCA_FK_ECONOMIA_TABLE_ID)" +
                " VALUES (" +
                "${objeto.valor.toString()}," +
                "${objeto.descricao}," +
                "${objeto.economia!!.id}" +
                ")"

        writableDatabase.execSQL(query)
    }

    override fun alterar(objeto: Poupanca) {
    }

    override fun excluir(objeto: Poupanca) {
        val query = "DELETE FROM $TABELA_POUPANCA WHERE $TABLE_ID = ${objeto.id}"
        writableDatabase.execSQL(query)
    }

    fun getTodos(id: Int?): List<Poupanca> {
        val poupancas = ArrayList<Poupanca>()
        val query = "SELECT * FROM $TABELA_POUPANCA" +
                " WHERE $POUPANCA_FK_ECONOMIA_TABLE_ID = $id ORDER BY $TABLE_ID DESC"

        val cursor = readableDatabase.rawQuery(query, null)
        while (cursor.moveToNext()) {
            val poupanca = Poupanca()
            bind(cursor, poupanca)

            poupancas.add(poupanca)
        }

        return poupancas
    }

    companion object {

        private const val TABELA_POUPANCA = "Poupanca"

        private const val POUPANCA_FK_ECONOMIA_TABLE_ID = "fk_id_economia"
        private const val POUPANCA_VALOR = "valor"
        private const val POUPANCA_DESCRICAO = "descricao"


        fun onCreate(db: SQLiteDatabase) {
            val query = "CREATE TABLE $TABELA_POUPANCA (" +
                    "$TABLE_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$POUPANCA_VALOR TEXT," +
                    "$POUPANCA_DESCRICAO TEXT," +
                    "$POUPANCA_FK_ECONOMIA_TABLE_ID INTEGER NOT NULL" +
                    ")"

            db.execSQL(query)
        }
    }

}
