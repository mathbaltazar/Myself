package br.com.myself.model.dao

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import br.com.myself.model.database.Database
import br.com.myself.model.entity.Entrada
import br.com.myself.util.Utils
import br.com.myself.util.Utils.Companion.getCalendar
import java.util.*
import kotlin.collections.ArrayList

class EntradaDAO(context: Context) : Database<Entrada>(context) {
    
    
    override fun bind(cursor: Cursor, elemento: Entrada) {
        elemento.id = cursor.getLong(cursor.getColumnIndexOrThrow(ID))
        elemento.descricao = cursor.getString(cursor.getColumnIndexOrThrow(DESCRICAO))
        //elemento.data = cursor.getLong(cursor.getColumnIndexOrThrow(DATA))
        elemento.valor = cursor.getDouble(cursor.getColumnIndexOrThrow(VALOR))
    }
    
    fun bind(cursor: Cursor): Entrada {
        return Entrada(
            id = cursor.getLong(cursor.getColumnIndexOrThrow(ID)),
            descricao = cursor.getString(cursor.getColumnIndexOrThrow(DESCRICAO)),
            data = cursor.getLong(cursor.getColumnIndexOrThrow(DATA)).getCalendar(),
            valor = cursor.getDouble(cursor.getColumnIndexOrThrow(VALOR))
        )
    }
    
    fun getTodasEntradas(): ArrayList<Entrada> {
        val entradas = arrayListOf<Entrada>()
        
        val sql = "SELECT * FROM $TAB_ENTRADA ORDER BY $DATA DESC, $ID DESC"
        val cursor = readableDatabase.rawQuery(sql, null)
        
        while (cursor.moveToNext()) {
            entradas.add(bind(cursor))
        }

        cursor.close()
        return entradas
    }
    
    fun inserir(entrada: Entrada) {
        val insert = "INSERT INTO $TAB_ENTRADA" +
                " ($DESCRICAO, $VALOR, $DATA) " +
                " VALUES " +
                " ('${entrada.descricao}', ${entrada.valor}, ${entrada.data}')"
        
        writableDatabase.execSQL(insert)
    }
    
    fun deletar(entrada: Entrada) {
        val sql = "DELETE FROM $TAB_ENTRADA WHERE $ID = ${entrada.id}"
        
        writableDatabase.execSQL(sql)
    }
    
    fun restaurarEntradas(entradas: Collection<Entrada>?) {
        
        val db = writableDatabase
        db.beginTransaction()
        
        if (!entradas.isNullOrEmpty()) {
            val stmt = db.compileStatement(
                "INSERT INTO $TAB_ENTRADA ($DESCRICAO, $VALOR, $DATA) VALUES (?, ?, ?)")
            
            entradas.forEach { entrada ->
                stmt.bindString(1, entrada.descricao)
                stmt.bindDouble(2, entrada.valor)
    
                //stmt.bindLong(3, entrada.data)
                
                stmt.executeInsert()
                stmt.clearBindings()
            }
            
            db.setTransactionSuccessful()
        }
        
        db.endTransaction()
    }
    
    fun getQuantidadeEntradas(): Int {
        val sql = "SELECT COUNT(*) FROM $TAB_ENTRADA"
        
        val cursor = readableDatabase.rawQuery(sql, null)
        cursor.moveToNext()
        val count = cursor.getInt(0)
        cursor.close()
        return count
    }
    
    fun getValorTotalEntradas(): Double {
        val sql = "SELECT SUM($VALOR) FROM $TAB_ENTRADA"
        
        val cursor = readableDatabase.rawQuery(sql, null)
        cursor.moveToNext()
        val total = cursor.getDouble(0)
        cursor.close()
        return total
    }
    
    fun getValorMediaEntradasPorMes(meses: Int): Double {
        val calendar = Utils.getCalendar()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        
        calendar.add(Calendar.MONTH, -meses)
        
        val sql = "SELECT AVG($VALOR) FROM $TAB_ENTRADA WHERE $DATA >= ${calendar.timeInMillis}"
        
        val cursor = readableDatabase.rawQuery(sql, null)
        cursor.moveToNext()
        val media = cursor.getDouble(0)
        cursor.close()
        return media
    }
    
    companion object {
        internal const val TAB_ENTRADA = "Entrada"
        
        private const val DESCRICAO = "descricao"
        private const val DATA = "data"
        private const val VALOR = "valor"
        private const val REFERENCIA_ANO_MES = "referencia_ano_mes"
        
        fun onCreate(db: SQLiteDatabase) {
            val create =
                "CREATE TABLE $TAB_ENTRADA (" +
                        "$ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "$DESCRICAO TEXT," +
                        "$DATA NUMERIC," +
                        "$REFERENCIA_ANO_MES TEXT," +
                        "$VALOR DECIMAL(10, 2)" +
                        ")"
            
            db.execSQL(create)
        }
        
    }
}