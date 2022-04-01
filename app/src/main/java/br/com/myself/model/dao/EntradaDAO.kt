package br.com.myself.database.dao

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import br.com.myself.database.Database
import br.com.myself.model.Entrada
import br.com.myself.util.Utils
import java.util.*
import kotlin.collections.ArrayList

class EntradaDAO(context: Context) : Database<Entrada>(context) {
    
    
    override fun bind(cursor: Cursor, elemento: Entrada) {
        cursor.getColumnIndex(ID).also { elemento.id = cursor.getLong(it) }
        cursor.getColumnIndex(FONTE).also { elemento.fonte = cursor.getString(it) }
        cursor.getColumnIndex(DATA).also { elemento.data = cursor.getLong(it) }
        cursor.getColumnIndex(VALOR).also { elemento.valor = cursor.getDouble(it) }
    }
    
    fun getTodasEntradas(): ArrayList<Entrada> {
        val entradas = arrayListOf<Entrada>()
        
        val sql = "SELECT * FROM $TAB_ENTRADA ORDER BY $DATA DESC, $ID DESC"
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
        val sql = "INSERT INTO $TAB_ENTRADA ($FONTE, $VALOR, $DATA)" +
                " VALUES ('${entrada.fonte}', ${entrada.valor}, ${entrada.data})"
        
        writableDatabase.execSQL(sql)
    }
    
    fun alterar(entrada: Entrada) {
        val sql = " UPDATE $TAB_ENTRADA SET " +
                "  $FONTE = '${entrada.fonte}' , " +
                "  $VALOR = ${entrada.valor} , " +
                "  $DATA = ${entrada.data}  " +
                "  WHERE $ID = ${entrada.id}"
        
        writableDatabase.execSQL(sql)
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
                "INSERT INTO $TAB_ENTRADA ($FONTE, $VALOR, $DATA) VALUES (?, ?, ?)")
            
            entradas.forEach { entrada ->
                stmt.bindString(1, entrada.fonte)
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
        
        private const val FONTE = "fonte"
        internal const val DATA = "data"
        private const val VALOR = "valor"
        
        fun onCreate(db: SQLiteDatabase) {
            val create =
                "CREATE TABLE $TAB_ENTRADA (" +
                        "$ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "$FONTE TEXT," +
                        "$DATA NUMERIC," +
                        "$VALOR DECIMAL(10, 2)" +
                        ")"
            
            db.execSQL(create)
        }
        
    }
}
