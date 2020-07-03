package com.baltazarstudio.regular.database.dao

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.baltazarstudio.regular.database.Database
import com.baltazarstudio.regular.model.Configuracao

class ConfiguracaoDAO(context: Context) : Database<Configuracao>(context) {

    override fun bind(cursor: Cursor, objeto: Configuracao) {
        objeto.url = cursor.getString(cursor.getColumnIndex(CONFIGURACAO_URL))
        objeto.porta = cursor.getString(cursor.getColumnIndex(CONFIGURACAO_PORTA))
        objeto.dataUltimaSincronizacao =
            cursor.getString(cursor.getColumnIndex(CONFIGURACAO_DATA_ULTIMA_SINCRONIZACAO))
    }

    fun getUltimaConfiguracao(): Configuracao {
        val sql = "SELECT * FROM $TABELA_CONFIGURACAO"
        val cursor = readableDatabase.rawQuery(sql, null)

        val configuracao = Configuracao()
        if (cursor.moveToFirst()) {
            bind(cursor, configuracao)
        }

        cursor.close()
        return configuracao
    }


    fun salvarConfiguracao(configuracao: Configuracao?) {
        writableDatabase.execSQL("DELETE FROM $TABELA_CONFIGURACAO")

        if (configuracao != null) {
            val sql = "INSERT INTO $TABELA_CONFIGURACAO (" +
                    "$CONFIGURACAO_URL," +
                    "$CONFIGURACAO_PORTA," +
                    "$CONFIGURACAO_DATA_ULTIMA_SINCRONIZACAO)" +
                    " VALUES (?, ?, ?)"
            val stmt = writableDatabase.compileStatement(sql)

            stmt.bindString(1, configuracao.url)
            stmt.bindString(2, configuracao.porta)
            if (configuracao.dataUltimaSincronizacao == null) stmt.bindNull(3)
            else stmt.bindString(3, configuracao.dataUltimaSincronizacao)


            stmt.executeInsert()
        }
    }

    companion object {

        const val TABELA_CONFIGURACAO = "Configuracao"

        const val CONFIGURACAO_URL = "url"
        const val CONFIGURACAO_PORTA = "porta"
        const val CONFIGURACAO_DATA_ULTIMA_SINCRONIZACAO = "dataUltimaSincronizacao"

        fun onCreate(db: SQLiteDatabase) {
            val create = "CREATE TABLE ${TABELA_CONFIGURACAO} (" +
                    "${CONFIGURACAO_URL} TEXT," +
                    "${CONFIGURACAO_PORTA} TEXT," +
                    "${CONFIGURACAO_DATA_ULTIMA_SINCRONIZACAO} TEXT" +
                    ")"

            db.execSQL(create)
        }
    }
}