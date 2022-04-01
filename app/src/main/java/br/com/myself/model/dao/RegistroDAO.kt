package br.com.myself.database.dao

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import br.com.myself.database.Database
import br.com.myself.model.Registro
import br.com.myself.util.Utils
import java.util.*
import kotlin.collections.ArrayList

class RegistroDAO(context: Context) : Database<Registro>(context) {
    
    
    fun getTodosRegistros(pesquisa: String? = null): List<Registro> {
        val movimentos = ArrayList<Registro>()
        var query = " SELECT * FROM $TAB_REGISTRO "
        
        if (!pesquisa.isNullOrBlank()) {
            query += " WHERE $DESCRICAO LIKE '%$pesquisa%' "
            query += " OR $OUTROS LIKE '%$pesquisa%' "
        }
        
        query += " ORDER BY $DATA DESC, $ID DESC "
        
        val cursor = readableDatabase.rawQuery(query, null)
        while (cursor.moveToNext()) {
            val item = Registro()
            bind(cursor, item)
            
            movimentos.add(item)
        }
        cursor.close()
        
        return movimentos
    }
    
    fun getRegistrosFiltradosPelaDespesa(id_despesa: Long): ArrayList<Registro> {
        val movimentos = ArrayList<Registro>()
        
        val queryBuilder = StringBuilder()
        queryBuilder.append(" SELECT * FROM $TAB_REGISTRO ")
        queryBuilder.append(" WHERE $FK_DESPESA = $id_despesa ")
        queryBuilder.append(" ORDER BY $DATA DESC, $ID DESC ")
    
        val cursor = readableDatabase.rawQuery(queryBuilder.toString(), null)
        while (cursor.moveToNext()) {
            val item = Registro()
            bind(cursor, item)
        
            movimentos.add(item)
        }
        cursor.close()
    
        return movimentos
    }
    
    fun getDataUltimoRegistro(id_despesa: Long): Long {
        
        val sql = "SELECT MAX($DATA) FROM $TAB_REGISTRO WHERE $FK_DESPESA = $id_despesa"
        
        val cursor = readableDatabase.rawQuery(sql, null)
        
        var data = 0L
        if (cursor.moveToNext()) {
            data = cursor.getLong(0)
        }
        
        cursor.close()
        return data
    }
    
    fun inserir(registro: Registro): Registro {
        val insert = writableDatabase.compileStatement(
            "INSERT INTO $TAB_REGISTRO (" +
                "$DESCRICAO," +
                "$OUTROS," +
                "$VALOR," +
                "$DATA," +
                "$REFERENCIA_MES_ANO," +
                "$FK_DESPESA)" +
                " VALUES (?,?,?,?,?,?)")
    
        // Atualizar referência mês/ano do objeto antes de salvar
        registro.referencia_mes_ano = Utils.gerarReferenciaMesAno(registro.data!!)
        
        insert.bindString(1, registro.descricao)
        insert.bindString(2, registro.outros)
        insert.bindDouble(3, registro.valor)
        registro.data?.let { insert.bindLong(4, it) } ?: insert.bindNull(4)
        insert.bindString(5, registro.referencia_mes_ano)
        registro.fk_despesa?.let { insert.bindLong(6, it) } ?: insert.bindNull(6)
        
        
    
        return registro.apply { id = insert.executeInsert() }
    }
    
    fun alterar(registro: Registro) {
        // Atualizar referência mês/ano do objeto antes de salvar
        registro.referencia_mes_ano = Utils.gerarReferenciaMesAno(registro.data!!)
        
        val queryBuilder = StringBuilder()
        queryBuilder.append(" UPDATE $TAB_REGISTRO SET ")
        queryBuilder.append(" $DESCRICAO = '${registro.descricao}', ")
        queryBuilder.append(" $OUTROS = '${registro.outros}', ")
        queryBuilder.append(" $VALOR = ${registro.valor}, ")
        queryBuilder.append(" $DATA = ${registro.data}, ")
        queryBuilder.append(" $REFERENCIA_MES_ANO = ${registro.referencia_mes_ano}, ")
        queryBuilder.append(" $FK_DESPESA = ? ")
        queryBuilder.append(" WHERE $ID = ${registro.id} ")
        
        val update = writableDatabase.compileStatement(queryBuilder.toString())
    
        if (registro.fk_despesa == null) update.bindNull(1)
        else update.bindLong(1, registro.fk_despesa!!.toLong())
        
        update.executeUpdateDelete()
    
    }
    
    fun excluir(registro: Registro) {
        val query = "DELETE FROM $TAB_REGISTRO WHERE $ID = ${registro.id}"
        writableDatabase.execSQL(query)
    }
    
    override fun bind(cursor: Cursor, elemento: Registro) {
        elemento.id = cursor.getLong(cursor.getColumnIndex(ID))
        elemento.descricao = cursor.getString(cursor.getColumnIndex(DESCRICAO))
        elemento.outros = cursor.getString(cursor.getColumnIndex(OUTROS))
        elemento.data = cursor.getLong(cursor.getColumnIndex(DATA))
        elemento.referencia_mes_ano = cursor.getString(cursor.getColumnIndex(REFERENCIA_MES_ANO))
        elemento.valor = cursor.getDouble(cursor.getColumnIndex(VALOR))
        elemento.fk_despesa = cursor.getLong(cursor.getColumnIndex(FK_DESPESA))
    }
    
    fun restaurarRegistros(registros: List<Registro>?) {
        val db = writableDatabase
        db.beginTransaction()
        
        if (!registros.isNullOrEmpty()) {
            val sqlInsertStatement = "INSERT INTO $TAB_REGISTRO (" +
                    "$DESCRICAO," +
                    "$OUTROS," +
                    "$VALOR," +
                    "$DATA," +
                    "$REFERENCIA_MES_ANO," +
                    "$FK_DESPESA)" +
                    " VALUES (?,?,?,?,?,?)"
            val stmt = db.compileStatement(sqlInsertStatement)
    
            registros.forEach { registro ->
                stmt.bindString(1, registro.descricao)
                stmt.bindString(2, registro.outros)
                stmt.bindDouble(3, registro.valor)
                registro.data?.let { stmt.bindLong(4, it) } ?: stmt.bindNull(4)
                stmt.bindString(5, registro.referencia_mes_ano)
                registro.fk_despesa?.let { stmt.bindLong(6, it) } ?: stmt.bindNull(6)
        
                stmt.executeInsert()
                stmt.clearBindings()
            }
            
            db.setTransactionSuccessful()
        }
        
        db.endTransaction()
    }
    
    fun getQuantidadeRegistros(): Int {
        val query = "SELECT COUNT(*) FROM $TAB_REGISTRO"
        
        val cursor = readableDatabase.rawQuery(query, null)
        cursor.moveToNext()
        val count = cursor.getInt(0)
        cursor.close()
        return count
    }
    
    fun getTotalValorRegistros(): Double {
        val query = "SELECT SUM($VALOR) FROM $TAB_REGISTRO"
    
        val cursor = readableDatabase.rawQuery(query, null)
        cursor.moveToNext()
        val total = cursor.getDouble(0)
        cursor.close()
        return total
    }
    
    
    fun getTotalValorRegistrosPorDia(dias: Int): Double {
        val calendar = Utils.getCalendar()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        
        calendar.add(Calendar.DAY_OF_MONTH, -dias)
        
        val query = "SELECT SUM($VALOR) FROM $TAB_REGISTRO WHERE $DATA >= ${calendar.timeInMillis}"
    
        val cursor = readableDatabase.rawQuery(query, null)
        cursor.moveToNext()
        val total = cursor.getDouble(0)
        cursor.close()
        return total
    }
    
    fun getRegistrosPorReferenciasMesAno(referencias: ArrayList<String>): ArrayList<Registro> {
        
        // MONTAR QUERY IN
        var query_in = "("
        referencias.forEach { referencia ->
            query_in += "'$referencia'"
            
            if (referencias.last() != referencia)
                query_in += ","
        }
        query_in += ")"
        
        var select = " SELECT * FROM $TAB_REGISTRO "
        select += " WHERE $REFERENCIA_MES_ANO IN $query_in "
        select += " ORDER BY $REFERENCIA_MES_ANO DESC, $ID DESC "
        
        val cursor = readableDatabase.rawQuery(select, null)
    
    
        val registros = ArrayList<Registro>()
        
        while (cursor.moveToNext()) {
            val item = Registro()
            bind(cursor, item)
        
            registros.add(item)
        }
        cursor.close()
    
        return registros
    }
    
    companion object {
        const val TAB_REGISTRO = "Registro"
        
        const val DESCRICAO = "descricao"
        const val DATA = "data"
        const val VALOR = "valor"
        const val REFERENCIA_MES_ANO = "referencia_mes_ano"
        const val FK_DESPESA = "fk_despesa"
        const val OUTROS = "outros"
        
        fun onCreate(db: SQLiteDatabase) {
            val create =
                "CREATE TABLE $TAB_REGISTRO (" +
                        "$ID INTEGER PRIMARY KEY," +
                        "$DESCRICAO TEXT," +
                        "$OUTROS TEXT," +
                        "$DATA NUMERIC," +
                        "$REFERENCIA_MES_ANO TEXT," +
                        "$VALOR DECIMAL(10, 2)," +
                        "$FK_DESPESA INTEGER" +
                        ")"
            
            db.execSQL(create)
        }
    }
    
}
