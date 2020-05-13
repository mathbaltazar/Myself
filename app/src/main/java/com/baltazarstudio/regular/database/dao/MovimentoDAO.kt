package com.baltazarstudio.regular.database.dao

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.baltazarstudio.regular.database.Database
import com.baltazarstudio.regular.model.Movimento
import kotlin.collections.ArrayList

class MovimentoDAO(context: Context) : Database<Movimento>(context) {

    fun get(id: Int): Movimento {
        val query = "SELECT * FROM $TABELA_MOVIMENTO WHERE $TABLE_ID = $id"

        val cursor = readableDatabase.rawQuery(query, null)
        val movimento = Movimento()
        cursor.moveToFirst()
        bind(cursor, movimento)

        cursor.close()
        return movimento
    }

    fun getTodosMovimentos(): List<Movimento> {
        val movimentos = ArrayList<Movimento>()
        val query = "SELECT * FROM $TABELA_MOVIMENTO ORDER BY $TABLE_ID DESC"

        val cursor = readableDatabase.rawQuery(query, null)
        while (cursor.moveToNext()) {
            val item = Movimento()
            bind(cursor, item)

            movimentos.add(item)
        }
        cursor.close()

        return movimentos
    }

    fun inserir(objeto: Movimento) {
        val insert = "INSERT INTO $TABELA_MOVIMENTO (" +
                "$MOVIMENTO_DESCRICAO," +
                "$MOVIMENTO_DIA," +
                "$MOVIMENTO_MES," +
                "$MOVIMENTO_ANO," +
                "$MOVIMENTO_VALOR)" +
                " VALUES (" +
                "'${objeto.descricao}'," +
                "${objeto.dia}," +
                "${objeto.mes}," +
                "${objeto.ano}," +
                "${objeto.valor}" +
                ")"

        writableDatabase.execSQL(insert)
    }

    fun excluir(objeto: Movimento) {
        val query = "DELETE FROM $TABELA_MOVIMENTO WHERE $TABLE_ID = ${objeto.id}"
        writableDatabase.execSQL(query)
    }

    override fun bind(cursor: Cursor, objeto: Movimento) {
        objeto.id = cursor.getInt(cursor.getColumnIndex(TABLE_ID))
        objeto.descricao = cursor.getString(cursor.getColumnIndex(MOVIMENTO_DESCRICAO))
        objeto.dia = cursor.getInt(cursor.getColumnIndex(MOVIMENTO_DIA))
        objeto.mes = cursor.getInt(cursor.getColumnIndex(MOVIMENTO_MES))
        objeto.ano = cursor.getInt(cursor.getColumnIndex(MOVIMENTO_ANO))
        objeto.valor = cursor.getDouble(cursor.getColumnIndex(MOVIMENTO_VALOR))
    }

    fun alterar(item: Movimento) {
        val update = "UPDATE $TABELA_MOVIMENTO" +
                " SET " +
                "$MOVIMENTO_DESCRICAO = '${item.descricao}'," +
                "$MOVIMENTO_VALOR = '${item.valor}" +
                " WHERE $TABLE_ID = ${item.id}"

        writableDatabase.execSQL(update)
    }

    fun getAnosDisponiveis(): ArrayList<Int> {
        val anos = arrayListOf<Int>()

        val sql = "SELECT DISTINCT $MOVIMENTO_ANO FROM $TABELA_MOVIMENTO"

        val cursor = readableDatabase.rawQuery(sql, null)

        while (cursor.moveToNext()) {
            anos.add(cursor.getInt(cursor.getColumnIndex(MOVIMENTO_ANO)))
        }

        cursor.close()
        return anos
    }

    fun getMesDisponivelPorAno(ano: Int): ArrayList<Int> {
        val meses = arrayListOf<Int>()

        val sql = "SELECT DISTINCT $MOVIMENTO_MES FROM $TABELA_MOVIMENTO WHERE $MOVIMENTO_ANO = $ano"

        val cursor = readableDatabase.rawQuery(sql, null)

        while (cursor.moveToNext()) {
            meses.add(cursor.getInt(cursor.getColumnIndex(MOVIMENTO_MES)))
        }

        cursor.close()
        return meses
    }

    companion object {
        private const val TABELA_MOVIMENTO = "Movimento"

        private const val MOVIMENTO_DESCRICAO = "descricao"
        private const val MOVIMENTO_DIA = "dia"
        private const val MOVIMENTO_MES = "mes"
        private const val MOVIMENTO_ANO = "ano"
        private const val MOVIMENTO_VALOR = "valor"

        fun onCreate(db: SQLiteDatabase) {
            val create = "CREATE TABLE $TABELA_MOVIMENTO (" +
                    "$TABLE_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$MOVIMENTO_DESCRICAO TEXT," +
                    "$MOVIMENTO_DIA INTEGER," +
                    "$MOVIMENTO_MES INTEGER," +
                    "$MOVIMENTO_ANO INTEGER," +
                    "$MOVIMENTO_VALOR DECIMAL(10, 2)" +
                    ")"

            db.execSQL(create)
        }
    }

}
