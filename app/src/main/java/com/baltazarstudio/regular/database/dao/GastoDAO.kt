package com.baltazarstudio.regular.database.dao

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.baltazarstudio.regular.database.Database
import com.baltazarstudio.regular.model.Gasto

class GastoDAO(context: Context) : Database<Gasto>(context) {
    
    fun getTodosGastos(): List<Gasto> {
        val movimentos = ArrayList<Gasto>()
        val query = "SELECT * FROM $TABELA ORDER BY $DATA DESC, $TABLE_ID DESC"
        
        val cursor = readableDatabase.rawQuery(query, null)
        while (cursor.moveToNext()) {
            val item = Gasto()
            bind(cursor, item)
            
            movimentos.add(item)
        }
        cursor.close()
        
        return movimentos
    }
    
    fun getTodosGastos(pesquisa: String): List<Gasto> {
        val movimentos = ArrayList<Gasto>()
        val query =
            "SELECT * FROM $TABELA WHERE $DESCRICAO LIKE '%$pesquisa%' ORDER BY $DATA, $TABLE_ID DESC"
        
        val cursor = readableDatabase.rawQuery(query, null)
        while (cursor.moveToNext()) {
            val item = Gasto()
            bind(cursor, item)
            
            movimentos.add(item)
        }
        cursor.close()
        
        return movimentos
    }
    
    fun inserir(gasto: Gasto) {
        val insert = writableDatabase.compileStatement(
            "INSERT INTO $TABELA (" +
                "$DESCRICAO," +
                "$VALOR," +
                "$MES," +
                "$ANO," +
                "$DATA," +
                "$GASTO_REFERENCIA_DESPESA," +
                "$GASTO_MARGEM_DESPESA)" +
                " VALUES (?,?,?,?,?,?,?)")
    
        insert.bindString(1, gasto.descricao)
        insert.bindDouble(2, gasto.valor)
        insert.bindLong(3, gasto.mes.toLong())
        insert.bindLong(4, gasto.ano.toLong())
        insert.bindLong(5, gasto.data)
        if (gasto.referenciaDespesa == null) insert.bindNull(6)
        else insert.bindLong(6, gasto.referenciaDespesa!!.toLong())
        insert.bindDouble(7, gasto.margemDespesa)
    
        insert.executeInsert()
    }
    
    fun alterar(gasto: Gasto) {
        val update =
            "UPDATE $TABELA" + " SET " +
                    "$DESCRICAO = '${gasto.descricao}'," +
                    "$MES = ${gasto.mes}," +
                    "$ANO = ${gasto.ano}," +
                    "$DATA = ${gasto.data}," +
                    "$VALOR = ${gasto.valor}" +
                    " WHERE $TABLE_ID = ${gasto.id}"
        
        writableDatabase.execSQL(update)
    }
    
    fun excluir(gasto: Gasto) {
        val query = "DELETE FROM $TABELA WHERE $TABLE_ID = ${gasto.id}"
        writableDatabase.execSQL(query)
    }
    
    override fun bind(cursor: Cursor, elemento: Gasto) {
        elemento.id = cursor.getInt(cursor.getColumnIndex(TABLE_ID))
        elemento.descricao = cursor.getString(cursor.getColumnIndex(DESCRICAO))
        elemento.mes = cursor.getInt(cursor.getColumnIndex(MES))
        elemento.ano = cursor.getInt(cursor.getColumnIndex(ANO))
        elemento.data = cursor.getLong(cursor.getColumnIndex(DATA))
        elemento.valor = cursor.getDouble(cursor.getColumnIndex(VALOR))
        try {
            elemento.referenciaDespesa = cursor.getInt(cursor.getColumnIndex(GASTO_REFERENCIA_DESPESA))
        } catch (ex: Throwable) {}
        
        elemento.margemDespesa = cursor.getDouble(cursor.getColumnIndex(GASTO_MARGEM_DESPESA))
    }
    
    fun getAnosDisponiveis(): ArrayList<Int> {
        val anos = arrayListOf<Int>()
        
        val sql =
            "SELECT DISTINCT $ANO FROM $TABELA ORDER BY $ANO DESC"
        
        val cursor = readableDatabase.rawQuery(sql, null)
        
        while (cursor.moveToNext()) {
            anos.add(cursor.getInt(cursor.getColumnIndex(ANO)))
        }
        
        cursor.close()
        return anos
    }
    
    fun getMesDisponivelPorAno(ano: Int): ArrayList<Int> {
        val meses = arrayListOf<Int>()
        
        val sql =
            "SELECT DISTINCT $MES FROM $TABELA WHERE $ANO = $ano ORDER BY $MES DESC"
        
        val cursor = readableDatabase.rawQuery(sql, null)
        
        while (cursor.moveToNext()) {
            meses.add(cursor.getInt(cursor.getColumnIndex(MES)))
        }
        
        cursor.close()
        return meses
    }
    
    fun restaurarGastos(gastos: List<Gasto>?) {
        val db = writableDatabase
        db.beginTransaction()
        
        db.execSQL("DELETE FROM $TABELA")
        
        if (!gastos.isNullOrEmpty()) {
            val sqlInsertStatement = "INSERT INTO $TABELA (" +
                    "$DESCRICAO," +
                    "$VALOR," +
                    "$MES," +
                    "$ANO," +
                    "$DATA)" +
                    " VALUES (?, ?, ?, ?, ?)"
            val stmt = db.compileStatement(sqlInsertStatement)
            
            gastos.forEach {
                stmt.bindString(1, it.descricao)
                stmt.bindDouble(2, it.valor)
                stmt.bindLong(3, it.mes.toLong())
                stmt.bindLong(4, it.ano.toLong())
                stmt.bindLong(5, it.data)
                
                stmt.executeInsert()
                stmt.clearBindings()
            }
            
            db.setTransactionSuccessful()
        }
        
        db.endTransaction()
    }
    
    companion object {
        internal const val TABELA = "Gasto"
        
        private const val DESCRICAO = "descricao"
        private const val MES = "mes"
        private const val ANO = "ano"
        internal const val DATA = "data"
        private const val VALOR = "valor"
        const val GASTO_REFERENCIA_DESPESA = "referencia_despesa"
        const val GASTO_MARGEM_DESPESA = "margem_despesa"
        
        fun onCreate(db: SQLiteDatabase) {
            val create =
                "CREATE TABLE $TABELA (" +
                        "$TABLE_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "$DESCRICAO TEXT," +
                        "$MES INTEGER," +
                        "$ANO INTEGER," +
                        "$DATA NUMERIC," +
                        "$VALOR DECIMAL(10, 2)," +
                        "$GASTO_REFERENCIA_DESPESA INTEGER," +
                        "$GASTO_MARGEM_DESPESA DECIMAL(10,2)" +
                        ")"
            
            db.execSQL(create)
        }
        
    }
    
}
