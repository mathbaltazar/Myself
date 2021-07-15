package com.baltazarstudio.regular.database.dao

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.baltazarstudio.regular.database.Database
import com.baltazarstudio.regular.model.Backup

class BackupDAO(context: Context) : Database<Backup>(context) {

    override fun bind(cursor: Cursor, elemento: Backup) {
        elemento.url = cursor.getString(cursor.getColumnIndex(URL))
        elemento.porta = cursor.getString(cursor.getColumnIndex(PORTA))
        elemento.dataUltimaSincronizacao =
            cursor.getLong(cursor.getColumnIndex(DATA_ULTIMA_SINCRONIZACAO))
    }

    fun getUltimoBackup(): Backup {
        val sql = "SELECT * FROM $TABELA_BACKUP"
        val cursor = readableDatabase.rawQuery(sql, null)

        val configuracao = Backup()
        if (cursor.moveToFirst()) {
            bind(cursor, configuracao)
        }

        cursor.close()
        return configuracao
    }


    fun salvarConfiguracao(backup: Backup?) {
        writableDatabase.execSQL("DELETE FROM $TABELA_BACKUP")

        if (backup != null) {
            val sql = "INSERT INTO $TABELA_BACKUP (" +
                    "$URL," +
                    "$PORTA," +
                    "$DATA_ULTIMA_SINCRONIZACAO)" +
                    " VALUES (?, ?, ?)"
            val stmt = writableDatabase.compileStatement(sql)

            stmt.bindString(1, backup.url)
            stmt.bindString(2, backup.porta)
            if (backup.dataUltimaSincronizacao == null) stmt.bindNull(3)
            else stmt.bindLong(3, backup.dataUltimaSincronizacao!!)


            stmt.executeInsert()
        }
    }
    
    companion object {

        const val TABELA_BACKUP = "Backup"

        const val URL = "url"
        const val PORTA = "porta"
        const val DATA_ULTIMA_SINCRONIZACAO = "dataUltimaSincronizacao"

        fun onCreate(db: SQLiteDatabase) {
            val create = "CREATE TABLE ${TABELA_BACKUP} (" +
                    "${URL} TEXT," +
                    "${PORTA} TEXT," +
                    "${DATA_ULTIMA_SINCRONIZACAO} TEXT" +
                    ")"

            db.execSQL(create)
        }
    }
}