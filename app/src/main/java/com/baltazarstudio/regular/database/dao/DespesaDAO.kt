package com.baltazarstudio.regular.database.dao

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import com.baltazarstudio.regular.database.Database
import com.baltazarstudio.regular.model.Despesa
import com.baltazarstudio.regular.model.Gasto
import java.util.*
import kotlin.collections.ArrayList

class DespesaDAO(context: Context) : Database<Despesa>(context) {
    
    
    
    override fun bind(cursor: Cursor, elemento: Despesa) {
        elemento.nome = cursor.getString(cursor.getColumnIndex(NOME))
        elemento.valor = cursor.getDouble(cursor.getColumnIndex(VALOR))
        elemento.referencia = cursor.getInt(cursor.getColumnIndex(REFERENCIA))
    }
    
    fun carregarTodasDespesas(): ArrayList<Despesa> {
        val selectReferencias = "SELECT ${GastoDAO.GASTO_REFERENCIA_DESPESA}, ${GastoDAO.DATA} FROM ${GastoDAO.TABELA}"
        val cursorReferencias = readableDatabase.rawQuery(selectReferencias, null)
        
        val referecias = arrayListOf<Gasto>()
        while (cursorReferencias.moveToNext()) {
            try {
                val gasto = Gasto()
                gasto.referenciaDespesa =
                    cursorReferencias.getInt(cursorReferencias.getColumnIndex(GastoDAO.GASTO_REFERENCIA_DESPESA))
                gasto.data =
                    cursorReferencias.getLong(cursorReferencias.getColumnIndex(GastoDAO.DATA))
                referecias.add(gasto)
            } catch (e: SQLiteException) {}
        }
        cursorReferencias.close()
    
        val selectDespesas = "SELECT * FROM $TABELA"
        val cursorDespesas = readableDatabase.rawQuery(selectDespesas, null)
        
        val despesas = arrayListOf<Despesa>()
        while (cursorDespesas.moveToNext()) {
            val despesa = Despesa()
            bind(cursorDespesas, despesa)
            
            referecias.filter { it.referenciaDespesa == despesa.referencia }.apply {
                despesa.ultimoRegistro = sortedByDescending { it.data }.first().data
            }
            
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
    
    companion object {
        const val TABELA = "Despesa"
        
        const val NOME = "nome"
        const val VALOR = "valor"
        const val REFERENCIA = "referencia"
        
        fun onCreate(db: SQLiteDatabase) {
            val sql = "CREATE TABLE $TABELA (" +
                    "$NOME TEXT," +
                    "$VALOR DECIMAL(10,2)," +
                    "$REFERENCIA INTEGER PRIMARY KEY AUTOINCREMENT)"
            
            db.execSQL(sql)
        }
    }
}
