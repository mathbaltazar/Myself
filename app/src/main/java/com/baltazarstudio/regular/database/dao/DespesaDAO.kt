package com.baltazarstudio.regular.database.dao

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.baltazarstudio.regular.database.Database
import com.baltazarstudio.regular.model.Despesa
import java.lang.StringBuilder
import kotlin.collections.ArrayList

class DespesaDAO(context: Context) : Database<Despesa>(context) {
    
    override fun bind(cursor: Cursor, elemento: Despesa) {
        elemento.codigo = cursor.getInt(cursor.getColumnIndex(CODIGO))
        elemento.nome = cursor.getString(cursor.getColumnIndex(NOME))
        elemento.valor = cursor.getDouble(cursor.getColumnIndex(VALOR))
        elemento.diaVencimento = cursor.getInt(cursor.getColumnIndex(DIA_VENCIMENTO))
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
            "INSERT INTO $TABELA ($NOME, $VALOR, $DIA_VENCIMENTO) VALUES ('${despesa.nome}', ${despesa.valor}, ${despesa.diaVencimento})"
        
        writableDatabase.execSQL(insert)
    }
    
    fun alterar(despesa: Despesa) {
        val update = StringBuilder()
        update.append("UPDATE $TABELA SET")
        update.append(" $NOME = '${despesa.nome}'")
        update.append(", $VALOR = ${despesa.valor}")
        update.append(", $DIA_VENCIMENTO = ${despesa.diaVencimento}")
        update.append(" WHERE $CODIGO = ${despesa.codigo}")
        
        writableDatabase.execSQL(update.toString())
    }
    
    fun restaurarDespesas(despesas: List<Despesa>?) {
        val db = writableDatabase
        db.beginTransaction()
        
        if (!despesas.isNullOrEmpty()) {
    
            val sqlInsertStatement = "INSERT INTO ${TABELA} (" +
                    "${CODIGO}," +
                    "${NOME}," +
                    "${VALOR}," +
                    "${DIA_VENCIMENTO})" +
                    " VALUES (?, ?, ?, ?)"
            val stmt = db.compileStatement(sqlInsertStatement)
            
            
            despesas.forEach {
                stmt.bindLong(1, it.codigo!!.toLong())
                stmt.bindString(2, it.nome)
                stmt.bindDouble(3, it.valor)
                stmt.bindLong(3, it.diaVencimento.toLong())
                
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
    
    fun getQuantidadeDespesas(): Int {
        val sql = "SELECT COUNT(*) FROM $TABELA"
        
        val cursor = readableDatabase.rawQuery(sql, null)
        cursor.moveToNext()
        val count = cursor.getInt(0)
        cursor.close()
        return count
    }
    
    fun getValorTotalDespesas(): Double {
        val sql = "SELECT SUM($VALOR) FROM $TABELA"
        
        val cursor = readableDatabase.rawQuery(sql, null)
        cursor.moveToNext()
        val total = cursor.getDouble(0)
        cursor.close()
        return total
    }
    
    fun getTotalPagoDespesas(): Double {
        val sql = "SELECT SUM($VALOR) FROM ${MovimentoDAO.TABELA} WHERE ${MovimentoDAO.REFERENCIA_DESPESA} <> 0"
    
        val cursor = readableDatabase.rawQuery(sql, null)
        cursor.moveToNext()
        val total = cursor.getDouble(0)
        cursor.close()
        return total
    }
    
    fun addVenc() {
        val addColumn = "ALTER TABLE $TABELA ADD COLUMN $DIA_VENCIMENTO INTEGER"
        
        writableDatabase.execSQL(addColumn)
        
        writableDatabase.execSQL("UPDATE $TABELA SET $DIA_VENCIMENTO = 0")
    }
    
    companion object {
        const val TABELA = "Despesa"
        
        const val CODIGO = "codigo"
        const val NOME = "nome"
        const val VALOR = "valor"
        const val DIA_VENCIMENTO = "dia_vencimento"
        
        fun onCreate(db: SQLiteDatabase) {
            val sql = "CREATE TABLE $TABELA (" +
                    "$NOME TEXT," +
                    "$VALOR DECIMAL(10,2)," +
                    "$DIA_VENCIMENTO INTEGER," +
                    "$CODIGO INTEGER PRIMARY KEY AUTOINCREMENT)"
            
            db.execSQL(sql)
        }
    }
}
