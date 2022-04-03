package br.com.myself.model.dao

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import br.com.myself.model.entity.Crise
import br.com.myself.model.database.Database

class CriseDAO(context: Context) : Database<Crise>(context) {
    
    private val compiledInsert: SQLiteStatement
    private val compiledUpdate: SQLiteStatement
    
    init {
        val insert =
            " INSERT INTO $TAB_CRISE " +
                    " ($DATA, $OBSERVACOES, $HORARIO1, $HORARIO2) " +
                    " VALUES (?, ?, ?, ?) "
        
        val update = " UPDATE $TAB_CRISE SET " +
                " $DATA = ?, " +
                " $OBSERVACOES = ?, " +
                " $HORARIO1 = ?, " +
                " $HORARIO2 = ? " +
                " WHERE $ID = ? "
                
        compiledInsert = writableDatabase.compileStatement(insert)
        compiledUpdate = writableDatabase.compileStatement(update)
    }
    
    override fun bind(cursor: Cursor, elemento: Crise) {
        elemento.id = cursor.getLong(cursor.getColumnIndexOrThrow(ID))
        elemento.data = cursor.getLong(cursor.getColumnIndexOrThrow(DATA))
        elemento.observacoes = cursor.getString(cursor.getColumnIndexOrThrow(OBSERVACOES))
        elemento.horario1 = cursor.getString(cursor.getColumnIndexOrThrow(HORARIO1))
        elemento.horario2 = cursor.getString(cursor.getColumnIndexOrThrow(HORARIO2))
    }
    
    fun getTodasCrises(): Collection<Crise> {
        val cursor = readableDatabase.rawQuery(
            "SELECT * FROM $TAB_CRISE ORDER BY $DATA DESC, $ID DESC",
            null)
        
        val crises = arrayListOf<Crise>()
        
        while (cursor.moveToNext()) {
            val crise = Crise()
            bind(cursor, crise)
            
            crises.add(crise)
        }
        
        cursor.close()
        return crises
    }
    
    fun inserir(crise: Crise): Long {
        compiledInsert.clearBindings()
        compiledInsert.bindLong(1, crise.data!!)
        compiledInsert.bindString(2, crise.observacoes)
        compiledInsert.bindString(3, crise.horario1)
        compiledInsert.bindString(4, crise.horario2)
        
        return compiledInsert.executeInsert()
    }
    
    fun alterar(crise: Crise) {
        compiledUpdate.clearBindings()
        compiledUpdate.bindLong(1, crise.data!!)
        compiledUpdate.bindString(2, crise.observacoes)
        compiledUpdate.bindString(3, crise.horario1)
        compiledUpdate.bindString(4, crise.horario2)
        compiledUpdate.bindLong(5, crise.id!!)
    
        return compiledUpdate.execute()
    }
    
    fun excluir(crise: Crise) {
        val sql = "DELETE FROM $TAB_CRISE WHERE $ID = ${crise.id}"
        
        writableDatabase.execSQL(sql)
    }
    
    companion object {
        private val TAB_CRISE = "tab_crise"
        private val DATA = "data"
        private val OBSERVACOES = "observacoes"
        private val HORARIO1 = "horario1"
        private val HORARIO2 = "horario2"
        
        fun onCreate(db: SQLiteDatabase) {
            val create = " CREATE TABLE $TAB_CRISE ( " +
                    " $ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " $DATA NUMERIC, " +
                    " $OBSERVACOES TEXT, " +
                    " $HORARIO1 TEXT, " +
                    " $HORARIO2 TEXT )"
            
            db.execSQL(create)
        }
    }
    
}
