package br.com.myself.domain.database

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import br.com.myself.domain.dao.*

abstract class Database<T>(context: Context) :
    SQLiteOpenHelper(context,
        DATABASE_NAME, null,
        DATABASE_VERSION
    ) {

    override fun onCreate(db: SQLiteDatabase) {
        //RegistroDAO.onCreate(db)
        //EntradaDAO.onCreate(db)
        //DespesaDAO.onCreate(db)
        //BackupDAO.onCreate(db)
        //CriseDAO.onCreate(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) { }

    abstract fun bind(cursor: Cursor, elemento: T)
    
    companion object {
        private const val DATABASE_NAME = "RegularDB"
        private const val DATABASE_VERSION = 1
        const val ID = "id"
    }

}