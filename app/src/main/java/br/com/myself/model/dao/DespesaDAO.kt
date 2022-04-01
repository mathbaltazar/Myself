package br.com.myself.database.dao

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import br.com.myself.database.Database
import br.com.myself.model.Despesa
import java.lang.StringBuilder
import kotlin.collections.ArrayList

class DespesaDAO(context: Context) : Database<Despesa>(context) {
    
    override fun bind(cursor: Cursor, elemento: Despesa) {
        elemento.id = cursor.getLong(cursor.getColumnIndex(ID))
        elemento.nome = cursor.getString(cursor.getColumnIndex(NOME))
        elemento.valor = cursor.getDouble(cursor.getColumnIndex(VALOR))
        elemento.diaVencimento = cursor.getInt(cursor.getColumnIndex(DIA_VENCIMENTO))
    }
    
    fun getTodasDespesas(): ArrayList<Despesa> {
        val selectDespesas = "SELECT * FROM $TAB_DESPESA ORDER BY $ID DESC"
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
            StringBuilder(" INSERT INTO $TAB_DESPESA ")
        insert.append(" ($NOME, $VALOR, $DIA_VENCIMENTO) ")
        insert.append(" VALUES ")
        insert.append(" ('${despesa.nome}', ${despesa.valor}, ${despesa.diaVencimento}) ")
        
        writableDatabase.execSQL(insert.toString())
    }
    
    fun alterar(despesa: Despesa) {
        val update = StringBuilder()
        update.append("UPDATE $TAB_DESPESA SET")
        update.append(" $NOME = '${despesa.nome}' ")
        update.append(", $VALOR = ${despesa.valor} ")
        update.append(", $DIA_VENCIMENTO = ${despesa.diaVencimento} ")
        update.append(" WHERE $ID = ${despesa.id}")
        
        writableDatabase.execSQL(update.toString())
    }
    
    fun restaurarDespesas(despesas: List<Despesa>?) {
        val db = writableDatabase
        db.beginTransaction()
        
        if (!despesas.isNullOrEmpty()) {
    
            val insertStatement = "INSERT INTO $TAB_DESPESA (" +
                    "$ID," +
                    "$NOME," +
                    "$VALOR," +
                    "$DIA_VENCIMENTO)" +
                    " VALUES (?, ?, ?, ?)"
            val stmt = db.compileStatement(insertStatement)
            
            
            despesas.forEach {
                stmt.bindLong(1, it.id)
                stmt.bindString(2, it.nome)
                stmt.bindDouble(3, it.valor)
                stmt.bindLong(4, it.diaVencimento.toLong())
                
                stmt.executeInsert()
                stmt.clearBindings()
            }
            
            db.setTransactionSuccessful()
        }
        
        db.endTransaction()
    }
    
    fun deletar(despesa: Despesa) {
        val sql = "DELETE FROM $TAB_DESPESA WHERE $ID = ${despesa.id}"
        
        writableDatabase.execSQL(sql)
    }
    
    fun getDespesaPorCodigo(id: Long): Despesa? {
        val query = "SELECT * FROM $TAB_DESPESA WHERE $ID = $id"
        
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
        val sql = "SELECT COUNT(*) FROM $TAB_DESPESA"
        
        val cursor = readableDatabase.rawQuery(sql, null)
        cursor.moveToNext()
        val count = cursor.getInt(0)
        cursor.close()
        return count
    }
    
    fun getValorTotalDespesas(): Double {
        val sql = "SELECT SUM($VALOR) FROM $TAB_DESPESA"
        
        val cursor = readableDatabase.rawQuery(sql, null)
        cursor.moveToNext()
        val total = cursor.getDouble(0)
        cursor.close()
        return total
    }
    
    fun getTotalPagoDespesas(): Double {
        val sql = "SELECT SUM($VALOR) FROM ${RegistroDAO.TAB_REGISTRO} WHERE ${RegistroDAO.FK_DESPESA} <> 0"
    
        val cursor = readableDatabase.rawQuery(sql, null)
        cursor.moveToNext()
        val total = cursor.getDouble(0)
        cursor.close()
        return total
    }
    
    fun atualizarValor(despesa: Despesa) {
        val sql = "UPDATE $TAB_DESPESA SET $VALOR = ${despesa.valor} WHERE $ID = ${despesa.id}"
        
        writableDatabase.execSQL(sql)
    }
    
    fun atualizarNome(despesa: Despesa) {
        val sql = "UPDATE $TAB_DESPESA SET $NOME = '${despesa.nome}' WHERE $ID = ${despesa.id}"
    
        writableDatabase.execSQL(sql)
    }
    
    fun atualizarDiaVencimento(despesa: Despesa) {
        val sql =
            "UPDATE $TAB_DESPESA SET $DIA_VENCIMENTO = ${despesa.diaVencimento} WHERE $ID = ${despesa.id}"
    
        writableDatabase.execSQL(sql)
    }
    
    companion object {
        const val TAB_DESPESA = "Despesa"
        
        const val NOME = "nome"
        const val VALOR = "valor"
        const val DIA_VENCIMENTO = "dia_vencimento"
        
        fun onCreate(db: SQLiteDatabase) {
            val sql = "CREATE TABLE $TAB_DESPESA (" +
                    "$NOME TEXT," +
                    "$VALOR DECIMAL(10,2)," +
                    "$DIA_VENCIMENTO INTEGER," +
                    "$ID INTEGER PRIMARY KEY AUTOINCREMENT)"
            
            db.execSQL(sql)
        }
    }
}
