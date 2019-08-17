package com.baltazarstudio.regular.database

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.baltazarstudio.regular.model.Economia
import java.math.BigDecimal

class EconomiaDAO(context: Context) : Database<Economia>(context) {

    private val poupancaDAO = PoupancaDAO(context)

    override fun get(id: Int): Economia {
        val economia = Economia()
        val query = "SELECT * FROM $TABELA_ECONOMIA WHERE $TABLE_ID = $id"

        val cursor = readableDatabase.rawQuery(query, null)
        cursor.moveToFirst()
        bind(cursor, economia)
        cursor.close()

        return economia
    }

    override fun getTodos(): List<Economia> {
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

    override fun inserir(objeto: Economia) {
        val query = "INSERT INTO $TABELA_ECONOMIA (" +
                "$ECONOMIA_DESCRICAO," +
                "$ECONOMIA_VALOR," +
                "$ECONOMIA_DATA)" +
                " VALUES (" +
                "'${objeto.descricao}'," +
                "'${objeto.valor.toString()}'," +
                "'${objeto.data}'" +
                ")"

        writableDatabase.execSQL(query)
    }

    override fun alterar(objeto: Economia) {
    }

    override fun excluir(objeto: Economia) {
        val query = "DELETE FROM $TABELA_ECONOMIA WHERE $TABLE_ID = ${objeto.id}"
        writableDatabase.execSQL(query)

        objeto.poupancas.forEach { poupancaDAO.excluir(it) }
    }


    override fun bind(cursor: Cursor, objeto: Economia) {
        objeto.id = cursor.getInt(cursor.getColumnIndex(TABLE_ID))
        objeto.descricao = cursor.getString(cursor.getColumnIndex(ECONOMIA_DESCRICAO))
        objeto.valor = BigDecimal(cursor.getString(cursor.getColumnIndex(ECONOMIA_VALOR)))
        objeto.data = cursor.getString(cursor.getColumnIndex(ECONOMIA_DATA))

        objeto.poupancas.addAll(poupancaDAO.getTodos(objeto.id))
    }


    companion object {
        private const val TABELA_ECONOMIA = "Economia"

        private const val ECONOMIA_DESCRICAO = "descricao"
        private const val ECONOMIA_VALOR = "valor"
        private const val ECONOMIA_DATA = "data"

        fun onCreate(db: SQLiteDatabase) {
            val query = "CREATE TABLE $TABELA_ECONOMIA (" +
                    "$TABLE_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$ECONOMIA_DESCRICAO TEXT," +
                    "$ECONOMIA_VALOR TEXT," +
                    "$ECONOMIA_DATA TEXT" +
                    ")"

            db.execSQL(query)
        }

    }
}
