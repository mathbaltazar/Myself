package com.baltazarstudio.regular.database.dao

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.baltazarstudio.regular.database.Database
import com.baltazarstudio.regular.model.Despesa
import com.baltazarstudio.regular.model.Movimento
import com.baltazarstudio.regular.util.Utils
import java.util.*
import kotlin.collections.ArrayList

class MovimentoDAO(context: Context) : Database<Movimento>(context) {
    
    fun getTodosMovimentos(): List<Movimento> {
        val movimentos = ArrayList<Movimento>()
        val query = "SELECT * FROM $TABELA ORDER BY $DATA DESC, $TABLE_ID DESC"
        
        val cursor = readableDatabase.rawQuery(query, null)
        while (cursor.moveToNext()) {
            val item = Movimento()
            bind(cursor, item)
            
            movimentos.add(item)
        }
        cursor.close()
        
        return movimentos
    }
    
    
    fun getTodosMovimentos(pesquisa: String): List<Movimento> {
        val movimentos = ArrayList<Movimento>()
        val query =
            "SELECT * FROM $TABELA WHERE $DESCRICAO LIKE '%$pesquisa%' ORDER BY $DATA, $TABLE_ID DESC"
        
        val cursor = readableDatabase.rawQuery(query, null)
        while (cursor.moveToNext()) {
            val item = Movimento()
            bind(cursor, item)
            
            movimentos.add(item)
        }
        cursor.close()
        
        return movimentos
    }
    
    fun getRegistrosPelaDespesa(codigoDespesa: Int): ArrayList<Movimento> {
        val movimentos = ArrayList<Movimento>()
        
        val queryBuilder = StringBuilder()
        queryBuilder.append("SELECT * FROM $TABELA")
        queryBuilder.append(" WHERE $REFERENCIA_DESPESA = $codigoDespesa")
        queryBuilder.append(" ORDER BY $DATA, $TABLE_ID DESC")
    
        val cursor = readableDatabase.rawQuery(queryBuilder.toString(), null)
        while (cursor.moveToNext()) {
            val item = Movimento()
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
    
    fun inserir(movimento: Movimento) {
        val insert = writableDatabase.compileStatement(
            "INSERT INTO $TABELA (" +
                "$DESCRICAO," +
                "$VALOR," +
                "$DATA," +
                "$REFERENCIA_DESPESA)" +
                " VALUES (?,?,?,?,?)")
    
        insert.bindString(1, movimento.descricao)
        insert.bindDouble(2, movimento.valor)
        movimento.data?.let { insert.bindLong(3, it) } ?: insert.bindNull(3)
        movimento.referenciaDespesa?.let { insert.bindLong(4, it.toLong()) } ?: insert.bindNull(4)
    
        insert.executeInsert()
        
    }
    
    fun alterar(movimento: Movimento) {
        val queryBuilder = StringBuilder()
        queryBuilder.append(" UPDATE $TABELA SET ")
        queryBuilder.append(" $DESCRICAO = '${movimento.descricao}', ")
        queryBuilder.append(" $VALOR = ${movimento.valor}, ")
        queryBuilder.append(" $DATA = ${movimento.data}, ")
        queryBuilder.append(" $REFERENCIA_DESPESA = ? ")
        queryBuilder.append(" WHERE $TABLE_ID = ${movimento.id} ")
        
        val update = writableDatabase.compileStatement(queryBuilder.toString())
    
        if (movimento.referenciaDespesa == null) update.bindNull(1)
        else update.bindLong(1, movimento.referenciaDespesa!!.toLong())
        
        update.executeUpdateDelete()
    
    }
    
    fun atualizarRegistrosDaDespesa(despesaEmEdicao: Despesa) {
        val sql = StringBuilder()
        sql.append("UPDATE $TABELA SET")
        sql.append(" $DESCRICAO = '${despesaEmEdicao.nome}'")
        sql.append(" WHERE $REFERENCIA_DESPESA = ${despesaEmEdicao.codigo}")
        
        writableDatabase.execSQL(sql.toString())
    }
    
    fun excluir(movimento: Movimento) {
        val query = "DELETE FROM $TABELA WHERE $TABLE_ID = ${movimento.id}"
        writableDatabase.execSQL(query)
    }
    
    override fun bind(cursor: Cursor, elemento: Movimento) {
        elemento.id = cursor.getInt(cursor.getColumnIndex(TABLE_ID))
        elemento.descricao = cursor.getString(cursor.getColumnIndex(DESCRICAO))
        elemento.data = cursor.getLong(cursor.getColumnIndex(DATA))
        elemento.valor = cursor.getDouble(cursor.getColumnIndex(VALOR))
        elemento.referenciaDespesa = cursor.getInt(cursor.getColumnIndex(REFERENCIA_DESPESA))
    }
    
    fun restaurarMovimentos(movimentos: List<Movimento>?) {
        val db = writableDatabase
        db.beginTransaction()
        
        if (!movimentos.isNullOrEmpty()) {
            val sqlInsertStatement = "INSERT INTO $TABELA (" +
                    "$DESCRICAO," +
                    "$VALOR," +
                    "$DATA," +
                    "$TIPO_MOVIMENTO," +
                    "$REFERENCIA_DESPESA)" +
                    " VALUES (?, ?, ?, ?, ?)"
            val stmt = db.compileStatement(sqlInsertStatement)
    
            movimentos.forEach { movimento ->
                stmt.bindString(1, movimento.descricao)
                stmt.bindDouble(2, movimento.valor)
                movimento.data?.let { stmt.bindLong(3, it) } ?: stmt.bindNull(3)
                movimento.referenciaDespesa?.let { stmt.bindLong(4, it.toLong()) } ?: stmt.bindNull(4)
        
                stmt.executeInsert()
                stmt.clearBindings()
            }
            
            db.setTransactionSuccessful()
        }
        
        db.endTransaction()
    }
    
    fun getQuantidadeMovimentos(): Int {
        val query = "SELECT COUNT(*) FROM $TABELA"
        
        val cursor = readableDatabase.rawQuery(query, null)
        cursor.moveToNext()
        val count = cursor.getInt(0)
        cursor.close()
        return count
    }
    
    fun getTotalValorMovimentos(): Double {
        val query = "SELECT SUM($VALOR) FROM $TABELA"
    
        val cursor = readableDatabase.rawQuery(query, null)
        cursor.moveToNext()
        val total = cursor.getDouble(0)
        cursor.close()
        return total
    }
    
    
    fun getTotalValorMovimentosPorDia(dias: Int): Double {
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
        internal const val TABELA = "Movimento"
        
        private const val DESCRICAO = "descricao"
        internal const val DATA = "data"
        private const val VALOR = "valor"
        const val TIPO_MOVIMENTO = "tipo_movimento"
        const val REFERENCIA_DESPESA = "referencia_despesa"
        
        fun onCreate(db: SQLiteDatabase) {
            val create =
                "CREATE TABLE $TABELA (" +
                        "$TABLE_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "$DESCRICAO TEXT," +
                        "$DATA NUMERIC," +
                        "$VALOR DECIMAL(10, 2)," +
                        "$TIPO_MOVIMENTO INTEGER," +
                        "$REFERENCIA_DESPESA INTEGER" +
                        ")"
            
            db.execSQL(create)
        }
    }
    
}
