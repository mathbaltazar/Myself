package com.baltazarstudio.regular.database.dao

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.baltazarstudio.regular.database.Database
import com.baltazarstudio.regular.model.Entrada
import com.baltazarstudio.regular.util.Utils
import java.util.*
import kotlin.collections.ArrayList

class EntradaDAO(context: Context) : Database<Entrada>(context) {
    
    
    override fun bind(cursor: Cursor, elemento: Entrada) {
        elemento.id = cursor.getInt(cursor.getColumnIndex(TABLE_ID))
        elemento.descricao = cursor.getString(cursor.getColumnIndex(DESCRICAO))
        elemento.data = cursor.getLong(cursor.getColumnIndex(DATA))
        elemento.valor = cursor.getDouble(cursor.getColumnIndex(VALOR))
    }
    
    fun getTodasEntradas(): ArrayList<Entrada> {
        val entradas = arrayListOf<Entrada>()
        
        val sql = "SELECT * FROM $TABELA ORDER BY $DATA DESC, $TABLE_ID DESC"
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
    
    fun alterar(entrada: Entrada) {
        val sql = " UPDATE $TABELA SET " +
                "  $DESCRICAO = '${entrada.descricao}' , " +
                "  $VALOR = ${entrada.valor} , " +
                "  $DATA = ${entrada.data}  " +
                "  WHERE $TABLE_ID = ${entrada.id}"
        
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
    
    fun getQuantidadeEntradas(): Int {
        val sql = "SELECT COUNT(*) FROM $TABELA"
        
        val cursor = readableDatabase.rawQuery(sql, null)
        cursor.moveToNext()
        val count = cursor.getInt(0)
        cursor.close()
        return count
    }
    
    fun getValorTotalEntradas(): Double {
        val sql = "SELECT SUM($VALOR) FROM $TABELA"
        
        val cursor = readableDatabase.rawQuery(sql, null)
        cursor.moveToNext()
        val total = cursor.getDouble(0)
        cursor.close()
        return total
    }
    
    fun getValorMediaEntradasPorMes(meses: Int): Double {
        val calendar = Utils.getUTCCalendar()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        
        calendar.add(Calendar.MONTH, -meses)
        
        val sql = "SELECT AVG($VALOR) FROM $TABELA WHERE $DATA >= ${calendar.timeInMillis}"
        
        val cursor = readableDatabase.rawQuery(sql, null)
        cursor.moveToNext()
        val media = cursor.getDouble(0)
        cursor.close()
        return media
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
