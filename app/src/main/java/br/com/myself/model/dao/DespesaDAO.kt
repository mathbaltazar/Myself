package br.com.myself.model.dao

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import br.com.myself.model.database.Database
import br.com.myself.model.entity.Despesa

class DespesaDAO(context: Context) : Database<Despesa>(context) {
    
    private val compiledInsert: SQLiteStatement
    private val compiledUpdate: SQLiteStatement
    
    init {
        compiledInsert = writableDatabase.compileStatement(" INSERT INTO $TAB_DESPESA " +
                " ($NOME, $VALOR, $DIA_VENCIMENTO) " +
                " VALUES (?,?,?)")
    
        compiledUpdate = writableDatabase.compileStatement(" UPDATE $TAB_DESPESA SET " +
                " $NOME = ? , $VALOR = ? , $DIA_VENCIMENTO = ?  WHERE $ID = ? ")
    }
    
    override fun bind(cursor: Cursor, elemento: Despesa) {
        elemento.id = cursor.getLong(cursor.getColumnIndexOrThrow(ID))
        elemento.nome = cursor.getString(cursor.getColumnIndexOrThrow(NOME))
        elemento.valor = cursor.getDouble(cursor.getColumnIndexOrThrow(VALOR))
        elemento.diaVencimento = cursor.getInt(cursor.getColumnIndexOrThrow(DIA_VENCIMENTO))
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
    
    fun inserir(despesa: Despesa): Long {
        compiledInsert.clearBindings()
        compiledInsert.bindString(1, despesa.nome)
        compiledInsert.bindDouble(2, despesa.valor)
        compiledInsert.bindLong(3, despesa.diaVencimento.toLong())
        
        return compiledInsert.executeInsert()
    }
    
    fun alterar(despesa: Despesa) {
        compiledUpdate.clearBindings()
        compiledUpdate.bindString(1, despesa.nome)
        compiledUpdate.bindDouble(2, despesa.valor)
        compiledUpdate.bindLong(3, despesa.diaVencimento.toLong())
        compiledUpdate.bindLong(4, despesa.id)
        
        compiledUpdate.executeUpdateDelete()
    }
    
    fun deletar(despesa: Despesa) {
        val sql = "DELETE FROM $TAB_DESPESA WHERE $ID = ${despesa.id}"
        
        writableDatabase.execSQL(sql)
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
