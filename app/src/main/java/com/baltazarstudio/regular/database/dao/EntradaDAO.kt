package com.baltazarstudio.regular.database.dao

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.baltazarstudio.regular.database.Database
import com.baltazarstudio.regular.model.Entrada

class EntradaDAO(context: Context) : Database<Entrada>(context) {

    override fun bind(cursor: Cursor, objeto: Entrada) {
        objeto.id = cursor.getInt(cursor.getColumnIndex(TABLE_ID))
        objeto.descricao = cursor.getString(cursor.getColumnIndex(ENTRADA_DESCRICAO))
        objeto.valor = cursor.getDouble(cursor.getColumnIndex(ENTRADA_VALOR))
        objeto.data = cursor.getLong(cursor.getColumnIndex(ENTRADA_DATA))
        objeto.dono = cursor.getString(cursor.getColumnIndex(ENTRADA_DONO))
    }

    fun getTodasEntradas(): ArrayList<Entrada> {
        val entradas = arrayListOf<Entrada>()

        val sql = "SELECT * FROM $TABELA_ENTRADA ORDER BY $ENTRADA_DATA DESC"

        val cursor = readableDatabase.rawQuery(sql, null)
        while (cursor.moveToNext()) {
            val entrada = Entrada()
            bind(cursor, entrada)
            entradas.add(entrada)
        }

        cursor.close()
        return entradas
    }

    fun getTodasEntradas(dono: String): ArrayList<Entrada> {
        val entradas = arrayListOf<Entrada>()

        val sql = "SELECT * FROM $TABELA_ENTRADA" +
                " WHERE $ENTRADA_DONO LIKE '$dono'" +
                " ORDER BY $ENTRADA_DATA DESC"

        val cursor = readableDatabase.rawQuery(sql, null)
        while (cursor.moveToNext()) {
            val entrada = Entrada()
            bind(cursor, entrada)
            entradas.add(entrada)
        }

        cursor.close()
        return entradas
    }

    fun inserir(entrada: Entrada) {
        val sql = "INSERT INTO $TABELA_ENTRADA (" +
                "$ENTRADA_DESCRICAO, $ENTRADA_VALOR," +
                "$ENTRADA_DATA, $ENTRADA_DONO)" +
                " VALUES (" +
                "'${entrada.descricao}', ${entrada.valor}," +
                "${entrada.data}, '${entrada.dono}'" +
                ")"

        writableDatabase.execSQL(sql)
    }

    fun remover(entrada: Entrada) {
        val sql = "DELETE FROM $TABELA_ENTRADA WHERE $TABLE_ID = ${entrada.id}"
        writableDatabase.execSQL(sql)
    }

    fun restaurarEntradas(entradas: Iterable<Entrada>) {
        val db = writableDatabase
        db.beginTransaction()

        db.execSQL("DELETE FROM $TABELA_ENTRADA")

        if (!entradas.none()) {
            val insertStatement = "INSERT INTO $TABELA_ENTRADA (" +
                    //"$TABLE_ID," +
                    "$ENTRADA_DESCRICAO," +
                    "$ENTRADA_VALOR," +
                    "$ENTRADA_DATA," +
                    "$ENTRADA_DONO)" +
                    " VALUES (?, ?, ?, ?)"
            val stmt = db.compileStatement(insertStatement)

            entradas.forEach {
                //stmt.bindLong(1, it.id!!.toLong())
                stmt.bindString(1, it.descricao)
                stmt.bindDouble(2, it.valor)
                if (it.data == null) stmt.bindNull(3)
                else stmt.bindLong(3, it.data!!)
                stmt.bindString(4, it.dono)

                stmt.executeInsert()
                stmt.clearBindings()
            }

            db.setTransactionSuccessful()
        }

        db.endTransaction()

    }

    companion object {

        private const val TABELA_ENTRADA = "Entrada"
        private const val ENTRADA_DESCRICAO = "descricao"
        private const val ENTRADA_VALOR = "valor"
        private const val ENTRADA_DATA = "data"
        private const val ENTRADA_DONO = "dono"

        fun onCreate(db: SQLiteDatabase) {
            val sql = "CREATE TABLE $TABELA_ENTRADA (" +
                    "$TABLE_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$ENTRADA_DESCRICAO TEXT," +
                    "$ENTRADA_VALOR DECIMAL(10, 2)," +
                    "$ENTRADA_DATA NUMERIC," +
                    "$ENTRADA_DONO TEXT)"

            db.execSQL(sql)
        }

    }
}
