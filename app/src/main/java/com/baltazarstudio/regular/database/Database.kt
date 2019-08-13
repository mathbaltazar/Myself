package com.baltazarstudio.regular.database

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

abstract class Database<T>(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION), IDAO<T> {

    companion object {
        private const val DATABASE_NAME = "RegularDB"
        private const val DATABASE_VERSION = 1
        const val TABLE_ID = "id"
    }

    override fun onCreate(db: SQLiteDatabase) {
        ItemCarteiraAbertaDAO.onCreate(db)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
    }

    abstract fun bind(cursor: Cursor, objeto: T)
}
