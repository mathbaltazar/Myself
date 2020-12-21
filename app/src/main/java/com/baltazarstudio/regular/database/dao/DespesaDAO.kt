package com.baltazarstudio.regular.database.dao

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.baltazarstudio.regular.database.Database
import com.baltazarstudio.regular.model.Despesa
import kotlin.collections.ArrayList

class DespesaDAO(context: Context) : Database<Despesa>(context) {
    
    override fun bind(cursor: Cursor, elemento: Despesa) {
        elemento.codigo = cursor.getInt(cursor.getColumnIndex(CODIGO))
        elemento.nome = cursor.getString(cursor.getColumnIndex(NOME))
        elemento.valor = cursor.getDouble(cursor.getColumnIndex(VALOR))
    }
    
    fun getTodasDespesas(): ArrayList<Despesa> {
        val selectDespesas = "SELECT * FROM $TABELA ORDER BY $CODIGO DESC"
        val cursorDespesas = readableDatabase.rawQuery(selectDespesas, null)
        
        val despesas = arrayListOf<Despesa>()
        while (cursorDespesas.moveToNext()) {
            val despesa = Despesa()
            bind(cursorDespesas, despesa)
            despesas.add(despesa)
        }
        
        cursorDespesas.close()
        return despesas
    }
    
    fun inserir(despesa: Despesa) {
        val insert =
            "INSERT INTO $TABELA ($NOME, $VALOR) VALUES ('${despesa.nome}', ${despesa.valor})"
        
        writableDatabase.execSQL(insert)
    }
    
    fun restaurarDespesas(despesas: List<Despesa>?) {
        val db = writableDatabase
        db.beginTransaction()
        
        //db.execSQL("DELETE FROM ${TABELA}")
        
        if (!despesas.isNullOrEmpty()) {
    
            val sqlInsertStatement = "INSERT INTO ${TABELA} (" +
                    "${CODIGO}," +
                    "${NOME}," +
                    "${VALOR})" +
                    " VALUES (?, ?, ?)"
            val stmt = db.compileStatement(sqlInsertStatement)
            
            
            despesas.forEach {
                stmt.bindLong(1, it.codigo!!.toLong())
                stmt.bindString(2, it.nome)
                stmt.bindDouble(3, it.valor)
                
                stmt.executeInsert()
                stmt.clearBindings()
            }
            
            db.setTransactionSuccessful()
        }
        
        db.endTransaction()
    }
    
    fun deletar(despesa: Despesa) {
        val sql = "DELETE FROM $TABELA WHERE $CODIGO = ${despesa.codigo}"
        
        writableDatabase.execSQL(sql)
    }
    
    fun getDespesaPorCodigo(codigo: Int): Despesa? {
        val query = "SELECT * FROM $TABELA WHERE $CODIGO = $codigo"
        
        val cursor = readableDatabase.rawQuery(query, null)
        
        if (cursor.moveToNext()) {
            val despesa = Despesa()
            bind(cursor, despesa)
            cursor.close()
            return despesa
        }
        
        cursor.close()
        return null
    }
    
    companion object {
        const val TABELA = "Despesa"
        
        const val NOME = "nome"
        const val VALOR = "valor"
        const val CODIGO = "codigo"
        
        fun onCreate(db: SQLiteDatabase) {
            val sql = "CREATE TABLE $TABELA (" +
                    "$NOME TEXT," +
                    "$VALOR DECIMAL(10,2)," +
                    "$CODIGO INTEGER PRIMARY KEY AUTOINCREMENT)"
            
            db.execSQL(sql)
        }
    }
}
