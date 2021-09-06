package br.com.myself.database.dao

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import br.com.myself.database.Database
import br.com.myself.model.Registro
import br.com.myself.util.Utils
import java.util.*
import kotlin.collections.ArrayList

class RegistroDAO(context: Context) : Database<Registro>(context) {
    
    
    fun getTodosRegistros(pesquisa: String? = null): List<Registro> {
        val movimentos = ArrayList<Registro>()
        var query = " SELECT * FROM $TABELA "
        
        if (!pesquisa.isNullOrBlank()) {
            query += " WHERE $DESCRICAO LIKE '%$pesquisa%' "
            query += " OR $LOCAL LIKE '%$pesquisa%' "
        }
        
        query += " ORDER BY $DATA DESC, $TABLE_ID DESC "
        
        val cursor = readableDatabase.rawQuery(query, null)
        while (cursor.moveToNext()) {
            val item = Registro()
            bind(cursor, item)
            
            movimentos.add(item)
        }
        cursor.close()
        
        return movimentos
    }
    
    fun getRegistrosFiltradosPelaDespesa(codigoDespesa: Int): ArrayList<Registro> {
        val movimentos = ArrayList<Registro>()
        
        val queryBuilder = StringBuilder()
        queryBuilder.append(" SELECT * FROM $TABELA ")
        queryBuilder.append(" WHERE $REFERENCIA_DESPESA = $codigoDespesa ")
        queryBuilder.append(" ORDER BY $DATA DESC, $TABLE_ID DESC ")
    
        val cursor = readableDatabase.rawQuery(queryBuilder.toString(), null)
        while (cursor.moveToNext()) {
            val item = Registro()
            bind(cursor, item)
        
            movimentos.add(item)
        }
        cursor.close()
    
        return movimentos
    }
    
    fun getUltimoRegistro(codigo: Int): Long {
        
        val sql = "SELECT MAX($DATA) FROM $TABELA WHERE $REFERENCIA_DESPESA = $codigo"
        
        val cursor = readableDatabase.rawQuery(sql, null)
        
        var data = 0L
        if (cursor.moveToNext()) {
            data = cursor.getLong(0)
        }
        
        cursor.close()
        return data
    }
    
    fun inserir(registro: Registro) {
        val insert = writableDatabase.compileStatement(
            "INSERT INTO $TABELA (" +
                "$DESCRICAO," +
                "$LOCAL," +
                "$VALOR," +
                "$DATA," +
                "$REFERENCIA_DESPESA)" +
                " VALUES (?,?,?,?,?)")
    
        insert.bindString(1, registro.descricao)
        insert.bindString(2, registro.local)
        insert.bindDouble(3, registro.valor)
        registro.data?.let { insert.bindLong(4, it) } ?: insert.bindNull(4)
        registro.referenciaDespesa?.let { insert.bindLong(5, it.toLong()) } ?: insert.bindNull(5)
    
        insert.executeInsert()
    }
    
    fun alterar(registro: Registro) {
        val queryBuilder = StringBuilder()
        queryBuilder.append(" UPDATE $TABELA SET ")
        queryBuilder.append(" $DESCRICAO = '${registro.descricao}', ")
        queryBuilder.append(" $LOCAL = '${registro.local}', ")
        queryBuilder.append(" $VALOR = ${registro.valor}, ")
        queryBuilder.append(" $DATA = ${registro.data}, ")
        queryBuilder.append(" $REFERENCIA_DESPESA = ? ")
        queryBuilder.append(" WHERE $TABLE_ID = ${registro.id} ")
        
        val update = writableDatabase.compileStatement(queryBuilder.toString())
    
        if (registro.referenciaDespesa == null) update.bindNull(1)
        else update.bindLong(1, registro.referenciaDespesa!!.toLong())
        
        update.executeUpdateDelete()
    
    }
    
    fun excluir(registro: Registro) {
        val query = "DELETE FROM $TABELA WHERE $TABLE_ID = ${registro.id}"
        writableDatabase.execSQL(query)
    }
    
    override fun bind(cursor: Cursor, elemento: Registro) {
        elemento.id = cursor.getInt(cursor.getColumnIndex(TABLE_ID))
        elemento.descricao = cursor.getString(cursor.getColumnIndex(DESCRICAO))
        elemento.local = cursor.getString(cursor.getColumnIndex(LOCAL))
        elemento.data = cursor.getLong(cursor.getColumnIndex(DATA))
        elemento.valor = cursor.getDouble(cursor.getColumnIndex(VALOR))
        elemento.referenciaDespesa = cursor.getInt(cursor.getColumnIndex(REFERENCIA_DESPESA))
    }
    
    fun restaurarRegistros(registros: List<Registro>?) {
        val db = writableDatabase
        db.beginTransaction()
        
        if (!registros.isNullOrEmpty()) {
            val sqlInsertStatement = "INSERT INTO $TABELA (" +
                    "$DESCRICAO," +
                    "$LOCAL," +
                    "$VALOR," +
                    "$DATA," +
                    "$REFERENCIA_DESPESA)" +
                    " VALUES (?,?,?,?,?)"
            val stmt = db.compileStatement(sqlInsertStatement)
    
            registros.forEach { registro ->
                stmt.bindString(1, registro.descricao)
                stmt.bindString(2, registro.local)
                stmt.bindDouble(3, registro.valor)
                registro.data?.let { stmt.bindLong(4, it) } ?: stmt.bindNull(4)
                registro.referenciaDespesa?.let { stmt.bindLong(5, it.toLong()) } ?: stmt.bindNull(5)
        
                stmt.executeInsert()
                stmt.clearBindings()
            }
            
            db.setTransactionSuccessful()
        }
        
        db.endTransaction()
    }
    
    fun getQuantidadeRegistros(): Int {
        val query = "SELECT COUNT(*) FROM $TABELA"
        
        val cursor = readableDatabase.rawQuery(query, null)
        cursor.moveToNext()
        val count = cursor.getInt(0)
        cursor.close()
        return count
    }
    
    fun getTotalValorRegistros(): Double {
        val query = "SELECT SUM($VALOR) FROM $TABELA"
    
        val cursor = readableDatabase.rawQuery(query, null)
        cursor.moveToNext()
        val total = cursor.getDouble(0)
        cursor.close()
        return total
    }
    
    
    fun getTotalValorRegistrosPorDia(dias: Int): Double {
        val calendar = Utils.getUTCCalendar()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        
        calendar.add(Calendar.DAY_OF_MONTH, -dias)
        
        val query = "SELECT SUM($VALOR) FROM $TABELA WHERE $DATA >= ${calendar.timeInMillis}"
    
        val cursor = readableDatabase.rawQuery(query, null)
        cursor.moveToNext()
        val total = cursor.getDouble(0)
        cursor.close()
        return total
    }
    
    companion object {
        const val TABELA = "Registro"
        
        const val DESCRICAO = "descricao"
        const val DATA = "data"
        const val VALOR = "valor"
        const val REFERENCIA_DESPESA = "referencia_despesa"
        const val LOCAL = "local"
        
        fun onCreate(db: SQLiteDatabase) {
            val create =
                "CREATE TABLE $TABELA (" +
                        "$TABLE_ID INTEGER PRIMARY KEY," +
                        "$DESCRICAO TEXT," +
                        "$LOCAL TEXT," +
                        "$DATA NUMERIC," +
                        "$VALOR DECIMAL(10, 2)," +
                        "$REFERENCIA_DESPESA INTEGER" +
                        ")"
            
            db.execSQL(create)
        }
    }
    
}
