package com.baltazarstudio.regular.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.baltazarstudio.regular.model.ItemCarteiraAberta

abstract class Database(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION), IDAO<ItemCarteiraAberta> {

    companion object {
        private val DATABASE_NAME = "RegularDB"
        private val DATABASE_VERSION = 1
        val TABLE_ID = "id"
    }

    override fun onCreate(db: SQLiteDatabase) {
        ItemCarteiraAbertaDAO.onCreate(db)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {

    }
}
