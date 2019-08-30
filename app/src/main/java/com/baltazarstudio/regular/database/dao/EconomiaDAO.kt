package com.baltazarstudio.regular.database.dao

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.baltazarstudio.regular.database.Database
import com.baltazarstudio.regular.model.Economia
import java.math.BigDecimal

class EconomiaDAO(context: Context) : Database<Economia>(context) {

    fun get(id: Int): Economia {
        val economia = Economia()
        val query = "SELECT * FROM $TABELA_ECONOMIA WHERE $TABLE_ID = $id"

        val cursor = readableDatabase.rawQuery(query, null)
        cursor.moveToFirst()
        bind(cursor, economia)
        cursor.close()

        return economia
    }

    fun getTodos(): List<Economia> {
        val economias = ArrayList<Economia>()
        val query = "SELECT * FROM $TABELA_ECONOMIA ORDER BY $TABLE_ID DESC"

        val cursor = readableDatabase.rawQuery(query, null)
        while (cursor.moveToNext()) {
            val economia = Economia()
            bind(cursor, economia)

            economias.add(economia)
        }
        cursor.close()

        return economias
    }

    fun inserir(objeto: Economia) {
        val query = "INSERT INTO $TABELA_ECONOMIA (" +
                "$ECONOMIA_DESCRICAO," +
                "$ECONOMIA_VALOR," +
                "$ECONOMIA_VALOR_POUPANCA," +
                "$ECONOMIA_DATA," +
                "$ECONOMIA_CONQUISTADO)" +
                " VALUES (" +
                "'${objeto.descricao}'," +
                "'${objeto.valor}'," +
                "'${objeto.valorPoupanca}'," +
                "'${objeto.data}'," +
                "'${if (objeto.conquistado) 1 else 0}'" +
                ")"

        writableDatabase.execSQL(query)
    }

    fun excluir(objeto: Economia) {
        val query = "DELETE FROM $TABELA_ECONOMIA WHERE $TABLE_ID = ${objeto.id}"
        writableDatabase.execSQL(query)
    }

    override fun bind(cursor: Cursor, objeto: Economia) {
        objeto.id = cursor.getInt(cursor.getColumnIndex(TABLE_ID))
        objeto.descricao = cursor.getString(cursor.getColumnIndex(ECONOMIA_DESCRICAO))
        objeto.valor = BigDecimal(cursor.getString(cursor.getColumnIndex(ECONOMIA_VALOR)))
        objeto.valorPoupanca = BigDecimal(cursor.getString(cursor.getColumnIndex(ECONOMIA_VALOR_POUPANCA)))
        objeto.data = cursor.getString(cursor.getColumnIndex(ECONOMIA_DATA))
        objeto.conquistado = cursor.getInt(cursor.getColumnIndex(ECONOMIA_CONQUISTADO)) == 1
    }

    fun adicionarPoupanca(item: Economia, valor: BigDecimal) {
        val query = "UPDATE $TABELA_ECONOMIA" +
                " SET $ECONOMIA_VALOR_POUPANCA = '${item.valorPoupanca.add(valor)}'" +
                " WHERE $TABLE_ID = ${item.id}"
        writableDatabase.execSQL(query)
    }

    fun retirarPoupanca(item: Economia, valor: BigDecimal) {
        val query = "UPDATE $TABELA_ECONOMIA" +
                " SET $ECONOMIA_VALOR_POUPANCA = '${item.valorPoupanca.subtract(valor)}'" +
                " WHERE $TABLE_ID = ${item.id}"
        writableDatabase.execSQL(query)
    }

    fun definirEconomiaConquistada(item: Economia) {
        val update = "UPDATE $TABELA_ECONOMIA" +
                " SET $ECONOMIA_CONQUISTADO = 1" +
                " WHERE $TABLE_ID = ${item.id}"

        writableDatabase.execSQL(update)
    }


    companion object {
        private const val TABELA_ECONOMIA = "Economia"

        private const val ECONOMIA_DESCRICAO = "descricao"
        private const val ECONOMIA_VALOR = "valor"
        private const val ECONOMIA_VALOR_POUPANCA = "valor_poupanca"
        private const val ECONOMIA_DATA = "data"
        private const val ECONOMIA_CONQUISTADO = "conquistado"

        fun onCreate(db: SQLiteDatabase) {
            val query = "CREATE TABLE $TABELA_ECONOMIA (" +
                    "$TABLE_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$ECONOMIA_DESCRICAO TEXT," +
                    "$ECONOMIA_VALOR TEXT," +
                    "$ECONOMIA_VALOR_POUPANCA TEXT," +
                    "$ECONOMIA_DATA TEXT," +
                    "$ECONOMIA_CONQUISTADO INTEGER" +
                    ")"

            db.execSQL(query)
        }

    }

}
