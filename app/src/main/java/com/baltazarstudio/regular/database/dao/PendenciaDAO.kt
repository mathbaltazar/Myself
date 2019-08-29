package com.baltazarstudio.regular.database.dao

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.baltazarstudio.regular.database.Database
import com.baltazarstudio.regular.model.Pendencia
import java.util.*

class PendenciaDAO(context: Context) : Database<Pendencia>(context) {

    fun get(id: Int): Pendencia {
        val query = "SELECT * FROM $TABELA_PENDENCIA WHERE $TABLE_ID = $id"

        val cursor = readableDatabase.rawQuery(query, null)
        val itemCarteira = Pendencia()
        cursor.moveToFirst()
        bind(cursor, itemCarteira)

        cursor.close()
        return itemCarteira
    }

    fun getTodasPendencias(): List<Pendencia> {
        val pendencias = ArrayList<Pendencia>()
        val query = "SELECT * FROM $TABELA_PENDENCIA ORDER BY $TABLE_ID DESC"

        val cursor = readableDatabase.rawQuery(query, null)
        while (cursor.moveToNext()) {
            val item = Pendencia()
            bind(cursor, item)

            pendencias.add(item)
        }
        cursor.close()

        return pendencias
    }

    fun inserir(objeto: Pendencia) {
        val insert = "INSERT INTO $TABELA_PENDENCIA (" +
                "$PENDENCIA_DESCRICAO," +
                "$PENDENCIA_DATA," +
                "$PENDENCIA_VALOR," +
                "$PENDENCIA_PAGO)" +
                " VALUES (" +
                "'${objeto.descricao}'," +
                "'${objeto.data}'," +
                "'${objeto.valor}'," +
                "'${if (objeto.pago) 1 else 0}'" +
                ")"

        writableDatabase.execSQL(insert)
    }

    fun excluir(objeto: Pendencia) {
        val query = "DELETE FROM $TABELA_PENDENCIA WHERE $TABLE_ID = ${objeto.id}"
        writableDatabase.execSQL(query)
    }

    override fun bind(cursor: Cursor, objeto: Pendencia) {
        objeto.id = cursor.getInt(cursor.getColumnIndex(TABLE_ID))
        objeto.descricao = cursor.getString(cursor.getColumnIndex(PENDENCIA_DESCRICAO))
        objeto.valor = cursor.getString(cursor.getColumnIndex(PENDENCIA_VALOR)).toBigDecimal()
        objeto.data = cursor.getString(cursor.getColumnIndex(PENDENCIA_DATA))
        objeto.pago = cursor.getInt(cursor.getColumnIndex(PENDENCIA_PAGO)) == 1
    }

    fun definirComoPago(pendencia: Pendencia) {
        val update = "UPDATE $TABELA_PENDENCIA" +
                " SET $PENDENCIA_PAGO = 1" +
                " WHERE $TABLE_ID = ${pendencia.id}"

        writableDatabase.execSQL(update)
    }

    companion object {
        private const val TABELA_PENDENCIA = "Pendencia"

        private const val PENDENCIA_DESCRICAO = "descricao"
        private const val PENDENCIA_DATA = "data"
        private const val PENDENCIA_VALOR = "valor"
        private const val PENDENCIA_PAGO = "pago"

        fun onCreate(db: SQLiteDatabase) {
            val create = "CREATE TABLE $TABELA_PENDENCIA (" +
                    "$TABLE_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$PENDENCIA_DESCRICAO TEXT," +
                    "$PENDENCIA_DATA TEXT," +
                    "$PENDENCIA_VALOR TEXT," +
                    "$PENDENCIA_PAGO INTEGER" +
                    ")"

            db.execSQL(create)
        }
    }

}
