package com.baltazarstudio.regular.database

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.baltazarstudio.regular.database.dao.ConfiguracaoDAO
import com.baltazarstudio.regular.database.dao.EntradaDAO
import com.baltazarstudio.regular.database.dao.MovimentoDAO

abstract class Database<T>(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        MovimentoDAO.onCreate(db)
        ConfiguracaoDAO.onCreate(db)
        EntradaDAO.onCreate(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion == 1 && newVersion == 2) {
            ConfiguracaoDAO.onCreate(db)
        } else if (oldVersion == 2 && newVersion == 3) {
            EntradaDAO.onCreate(db)
        }
    }

    abstract fun bind(cursor: Cursor, objeto: T)

    companion object {
        private const val DATABASE_NAME = "RegularDB"
        private const val DATABASE_VERSION = 3
        const val TABLE_ID = "id"
    }

}
