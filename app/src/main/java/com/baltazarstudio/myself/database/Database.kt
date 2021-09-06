package com.baltazarstudio.myself.database

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.baltazarstudio.myself.database.dao.BackupDAO
import com.baltazarstudio.myself.database.dao.DespesaDAO
import com.baltazarstudio.myself.database.dao.EntradaDAO
import com.baltazarstudio.myself.database.dao.RegistroDAO

abstract class Database<T>(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        RegistroDAO.onCreate(db)
        EntradaDAO.onCreate(db)
        DespesaDAO.onCreate(db)
        BackupDAO.onCreate(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) { }

    abstract fun bind(cursor: Cursor, elemento: T)

    companion object {
        private const val DATABASE_NAME = "RegularDB"
        private const val DATABASE_VERSION = 1
        const val TABLE_ID = "id"
    }

}
