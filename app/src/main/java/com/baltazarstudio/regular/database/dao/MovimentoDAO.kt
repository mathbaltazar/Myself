package com.baltazarstudio.regular.database.dao

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.baltazarstudio.regular.database.Database
import com.baltazarstudio.regular.model.Movimento

class MovimentoDAO(context: Context) : Database<Movimento>(context) {
    
    fun get(id: Int): Movimento {
        val query = "SELECT * FROM $TABELA_MOVIMENTO WHERE $TABLE_ID = $id"
        
        val cursor = readableDatabase.rawQuery(query, null)
        val movimento = Movimento()
        cursor.moveToFirst()
        bind(cursor, movimento)
        
        cursor.close()
        return movimento
    }
    
    fun getTodosMovimentos(): List<Movimento> {
        val movimentos = ArrayList<Movimento>()
        val query = "SELECT * FROM $TABELA_MOVIMENTO ORDER BY $MOVIMENTO_DATA DESC"
        
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
            "SELECT * FROM $TABELA_MOVIMENTO" + " WHERE $MOVIMENTO_DESCRICAO LIKE '%$pesquisa%'" + " ORDER BY $MOVIMENTO_DATA DESC"
        
        val cursor = readableDatabase.rawQuery(query, null)
        while (cursor.moveToNext()) {
            val item = Movimento()
            bind(cursor, item)
            
            movimentos.add(item)
        }
        cursor.close()
        
        return movimentos
    }
    
    fun inserir(objeto: Movimento) {
        val insert = "INSERT INTO $TABELA_MOVIMENTO (" +
                "$MOVIMENTO_DESCRICAO," +
                "$MOVIMENTO_VALOR," +
                "$MOVIMENTO_MES," +
                "$MOVIMENTO_ANO," +
                "$MOVIMENTO_DATA)" +
                " VALUES (" +
                "'${objeto.descricao}'," +
                "${objeto.valor}," +
                "${objeto.mes}," +
                "${objeto.ano}," +
                "${objeto.data}" +
                ")"
        
        writableDatabase.execSQL(insert)
    }
    
    fun alterar(item: Movimento) {
        val update =
            "UPDATE $TABELA_MOVIMENTO" + " SET " +
                    "$MOVIMENTO_DESCRICAO = '${item.descricao}'," +
                    "$MOVIMENTO_MES = ${item.mes}," +
                    "$MOVIMENTO_ANO = ${item.ano}," +
                    "$MOVIMENTO_DATA = ${item.data}," +
                    "$MOVIMENTO_VALOR = ${item.valor}" +
                    " WHERE $TABLE_ID = ${item.id}"
        
        writableDatabase.execSQL(update)
    }
    
    fun excluir(objeto: Movimento) {
        val query = "DELETE FROM $TABELA_MOVIMENTO WHERE $TABLE_ID = ${objeto.id}"
        writableDatabase.execSQL(query)
    }
    
    override fun bind(cursor: Cursor, elemento: Movimento) {
        elemento.id = cursor.getInt(cursor.getColumnIndex(TABLE_ID))
        elemento.descricao = cursor.getString(cursor.getColumnIndex(MOVIMENTO_DESCRICAO))
        elemento.mes = cursor.getInt(cursor.getColumnIndex(MOVIMENTO_MES))
        elemento.mes = cursor.getInt(cursor.getColumnIndex(MOVIMENTO_MES))
        elemento.ano = cursor.getInt(cursor.getColumnIndex(MOVIMENTO_ANO))
        elemento.data = cursor.getLong(cursor.getColumnIndex(MOVIMENTO_DATA))
        elemento.valor = cursor.getDouble(cursor.getColumnIndex(MOVIMENTO_VALOR))
    }
    
    fun getAnosDisponiveis(): ArrayList<Int> {
        val anos = arrayListOf<Int>()
        
        val sql =
            "SELECT DISTINCT $MOVIMENTO_ANO FROM $TABELA_MOVIMENTO ORDER BY $MOVIMENTO_ANO DESC"
        
        val cursor = readableDatabase.rawQuery(sql, null)
        
        while (cursor.moveToNext()) {
            anos.add(cursor.getInt(cursor.getColumnIndex(MOVIMENTO_ANO)))
        }
        
        cursor.close()
        return anos
    }
    
    fun getMesDisponivelPorAno(ano: Int): ArrayList<Int> {
        val meses = arrayListOf<Int>()
        
        val sql =
            "SELECT DISTINCT $MOVIMENTO_MES FROM $TABELA_MOVIMENTO WHERE $MOVIMENTO_ANO = $ano ORDER BY $MOVIMENTO_MES DESC"
        
        val cursor = readableDatabase.rawQuery(sql, null)
        
        while (cursor.moveToNext()) {
            meses.add(cursor.getInt(cursor.getColumnIndex(MOVIMENTO_MES)))
        }
        
        cursor.close()
        return meses
    }
    
    fun restaurarMovimentos(movimentos: List<Movimento>?) {
        val db = writableDatabase
        db.beginTransaction()
        
        db.execSQL("DELETE FROM $TABELA_MOVIMENTO")
        
        if (!movimentos.isNullOrEmpty()) {
            val sqlInsertStatement = "INSERT INTO $TABELA_MOVIMENTO (" +
                    //"$TABLE_ID," +
                    "$MOVIMENTO_DESCRICAO," +
                    "$MOVIMENTO_VALOR," +
                    "$MOVIMENTO_MES," +
                    "$MOVIMENTO_ANO," +
                    "$MOVIMENTO_DATA)" +
                    " VALUES (?, ?, ?, ?, ?)"
            val stmt = db.compileStatement(sqlInsertStatement)
            
            movimentos.forEach {
                //stmt.bindLong(1, it.id!!.toLong())
                stmt.bindString(1, it.descricao)
                stmt.bindLong(2, it.mes.toLong())
                stmt.bindLong(3, it.ano.toLong())
                stmt.bindLong(4, it.data)
                stmt.bindDouble(5, it.valor)
                
                stmt.executeInsert()
                stmt.clearBindings()
            }
            
            db.setTransactionSuccessful()
        }
        
        db.endTransaction()
    }
    
    companion object {
        private const val TABELA_MOVIMENTO = "Movimento"
        
        private const val MOVIMENTO_DESCRICAO = "descricao"
        private const val MOVIMENTO_MES = "mes"
        private const val MOVIMENTO_ANO = "ano"
        private const val MOVIMENTO_DATA = "data"
        private const val MOVIMENTO_VALOR = "valor"
        
        fun onCreate(db: SQLiteDatabase) {
            val create =
                "CREATE TABLE $TABELA_MOVIMENTO (" +
                        "$TABLE_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "$MOVIMENTO_DESCRICAO TEXT," +
                        "$MOVIMENTO_MES INTEGER," +
                        "$MOVIMENTO_ANO INTEGER," +
                        "$MOVIMENTO_DATA NUMERIC," +
                        "$MOVIMENTO_VALOR DECIMAL(10, 2)" +
                        ")"
            
            db.execSQL(create)
        }
        
    }
    
}
