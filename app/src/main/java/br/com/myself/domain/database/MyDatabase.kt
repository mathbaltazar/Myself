package br.com.myself.domain.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import br.com.myself.domain.dao.*
import br.com.myself.domain.database.convertors.DateConverter
import br.com.myself.domain.entity.*

@Database(entities = arrayOf(
    Registro::class,
    Despesa::class,
    Entrada::class,
    Crise::class
), version = 3, exportSchema = true)
@TypeConverters(DateConverter::class)
abstract class MyDatabase : RoomDatabase() {
    
    companion object { const val NAME = "_myself-database" }
    
    abstract fun getRegistroDAO(): RegistroDAO
    
    abstract fun getDespesaDAO(): DespesaDAO
    
    abstract fun getEntradaDAO(): EntradaDAO
    
    abstract fun getCriseDAO(): CriseDAO
    
}