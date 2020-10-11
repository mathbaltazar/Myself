package com.baltazarstudio.regular.database.dao

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.baltazarstudio.regular.database.Database
import com.baltazarstudio.regular.model.Entrada

class EntradaDAO(context: Context) : Database<Entrada>(context) {
    
    
    override fun bind(cursor: Cursor, elemento: Entrada) {
        elemento.id = cursor.getInt(cursor.getColumnIndex(TABLE_ID))
        elemento.descricao = cursor.getString(cursor.getColumnIndex(DESCRICAO))
        elemento.data = cursor.getLong(cursor.getColumnIndex(DATA))
        elemento.valor = cursor.getDouble(cursor.getColumnIndex(VALOR))
    }
    
    fun getTodasEntradas(): ArrayList<Entrada> {
        val entradas = arrayListOf<Entrada>()
        
        val sql = "SELECT * FROM $TABELA"
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
        val sql = "INSERT INTO $TABELA ($DESCRICAO, $VALOR, $DATA)" +
                " VALUES ('${entrada.descricao}', ${entrada.valor}, ${entrada.data})"
        
        writableDatabase.execSQL(sql)
    }
    
    fun deletar(entrada: Entrada) {
        val sql = "DELETE FROM $TABELA WHERE $TABLE_ID = ${entrada.id}"
        
        writableDatabase.execSQL(sql)
    }
    
    fun restaurarEntradas(entradas: Collection<Entrada>?) {
        
        val db = writableDatabase
        db.beginTransaction()
        
        if (!entradas.isNullOrEmpty()) {
            val stmt = db.compileStatement(
                "INSERT INTO $TABELA ($DESCRICAO, $VALOR, $DATA) VALUES (?, ?, ?)")
            
            entradas.forEach { entrada ->
                stmt.bindString(1, entrada.descricao)
                stmt.bindDouble(2, entrada.valor)
                
                if (entrada.data != null) stmt.bindLong(3, entrada.data!!)
                else stmt.bindNull(3)
                
                stmt.executeInsert()
                stmt.clearBindings()
            }
            
            db.setTransactionSuccessful()
        }
        
        db.endTransaction()
    }
    
    companion object {
        internal const val TABELA = "Entrada"
        
        private const val DESCRICAO = "descricao"
        internal const val DATA = "data"
        private const val VALOR = "valor"
        
        fun onCreate(db: SQLiteDatabase) {
            val create =
                "CREATE TABLE $TABELA (" +
                        "$TABLE_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "$DESCRICAO TEXT," +
                        "$DATA NUMERIC," +
                        "$VALOR DECIMAL(10, 2)" +
                        ")"
            
            db.execSQL(create)
        }
        
    }
}
